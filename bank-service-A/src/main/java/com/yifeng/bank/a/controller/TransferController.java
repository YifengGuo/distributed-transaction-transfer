package com.yifeng.bank.a.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yifeng.bank.a.dao.AccountDao;
import com.yifeng.bank.a.manager.TwoPhaseCommitTransactionManager;
import com.yifeng.bank.a.rpc.BankBClient;
import com.yifeng.bank.a.service.impl.RegisterServiceImpl;
import com.yifeng.bank.a.util.SessionFactoryUtils;
import com.yifeng.commons.constant.BankEnum;
import com.yifeng.commons.constant.TransferType;
import com.yifeng.commons.pojo.UndoLog;
import com.yifeng.commons.util.JsonHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.Arrays;

import static com.yifeng.commons.constant.TransferServiceConstant.*;

@RestController
@RequestMapping("/bank-a")
public class TransferController {

    private static final Logger LOG = LoggerFactory.getLogger(TransferController.class);

    @Autowired
    TwoPhaseCommitTransactionManager transactionManager;

    @Autowired
    BankBClient bankBClient;

    private SqlSession session = SessionFactoryUtils.getSession();

    private AccountDao accountMapper = session.getMapper(AccountDao.class);

    @Autowired
    RegisterServiceImpl registerService;

    @Value("${spring.application.name}")
    String sourceBankService;

    @RequestMapping("/transfer")
    public String transfer(@RequestBody JSONObject payload) {
        String sourceBankAccount = payload.getString("source_bank_account");
        String targetBankService = payload.getString("target_bank");
        String targetBankAccount = payload.getString("target_bank_account");
        double amount = payload.getDouble("amount");

        // sanity check on request params
        if (!validParam(sourceBankAccount, targetBankService, targetBankAccount, amount)) {
            LOG.error("wrong request params");
            return "wrong request params";
        }

        // sanity check on target bank service
        if (targetBankAccount.equals(sourceBankService) || !validBank(targetBankService)) {
            LOG.error("wrong target bank {}", targetBankService);
            return "wrong target bank name";
        }

        // check validity of source bank account
        if (!sourceAccountExist(sourceBankAccount)) {
            LOG.error("given source bank account does not exist {}", sourceBankAccount);
            return "given source bank account does not exist";
        }

        if (!sourceAccountHasEnoughBalance(sourceBankAccount, amount)) {
            LOG.error("given source bank account does not have enough balance {}", sourceBankAccount);
            return "given source bank account does not have enough balance";
        }

        /* =============================== Two Phase Commit process starts ============================== */

        LOG.info("2pc transaction begins, source account {} balance is {}" +
                " target account {} balance is {}", sourceBankAccount, accountMapper.getBalance(sourceBankAccount),
                targetBankAccount, bankBClient.getBalance(targetBankAccount));

        // step 1: TM, RM register to TC
        boolean registerResult = registerResourceManagers(targetBankService);
        if (!registerResult) {
            LOG.error("one or more associated services were failed to register to TC. Exiting current transaction");
            return "Service(s) registered failed";
        }

        // step 2: TM opens global transaction and get XID
        final String XID = registerGlobalTransaction(transactionManager.getTransactionManagerId(), sourceBankService,
                targetBankService);
        if (XID == null || XID.equals(REGISTER_GLOBAL_TRANSACTION_FAILED)) {
            LOG.error("register global transaction failed, abort current transfer...");
            return "register global transaction failed, abort current transfer...";
        } else {
            LOG.info("current global transaction id is {}", XID);
        }

        // step 3: bank A registers branch txn, write db and undo_log, commit branch txn
        String sourceBankBranchId = registerService.registerBranchTransaction(XID);
        boolean transactionBranchAResult = handleSourceBankBranchTransaction(XID, sourceBankBranchId, sourceBankAccount, amount);

        // step 4: bank B registers branch txn, write db and undo_log, commit branch txn
        String targetBankBranchId = bankBClient.registerBranchTransaction(XID);
        boolean transactionBranchBResult = handleTargetBankBranchTransaction(XID, targetBankBranchId, targetBankAccount, amount);

        // step 5: TM commit global txn if all branches succeeded, ask TC to release txn mappings in HashMap
        // otherwise do global fall back base on transaction result of each branch
        boolean sourceRollBack = false;
        boolean targetRollBack = false;
        if (transactionBranchAResult && transactionBranchBResult) {
            // TODO: some retry policy to make sure global txn is closed eventually
            boolean closeGlobalTransaction = registerService.closeGlobalTransaction(XID);
        } else {
            // at least one branch txn is failed, need to roll back each branch txn and close global txn then
            sourceRollBack = rollbackSourceBankBranchTransaction(XID, sourceBankBranchId);
            targetRollBack = bankBClient.rollbackTargetBankBranchTransaction(XID, targetBankBranchId);
            boolean closeGlobalTransaction = registerService.closeGlobalTransaction(XID);
        }

        // step 6: RM delete undo_log
        if (sourceRollBack == targetRollBack) { // both rollback or both succeed
            deleteUndoLog(XID, sourceBankBranchId);
            bankBClient.deleteUndoLog(XID, targetBankBranchId);
            LOG.info("both undo_log have been deleted");
        }

        LOG.info("2pc transaction completed, source account {} balance is {}" +
                        " target account {} balance is {}", sourceBankAccount, accountMapper.getBalance(sourceBankAccount),
                targetBankAccount, bankBClient.getBalance(targetBankAccount));
        return "current cross database transfer transaction is complete";
    }

    private boolean deleteUndoLog(String XID, String sourceBankBranchId) {
        try {
            String filename = XID + "-" + sourceBankBranchId;
            File undoLogFile = new File(BANK_A_UNDO_LOG_PATH + filename + ".json");
            undoLogFile.delete();
            LOG.info("undo_log {} is deleted", BANK_A_UNDO_LOG_PATH + filename + ".json");
        } catch (Exception e) {
            LOG.error("error in deleting undo_log {}, {}", XID + "-" + sourceBankBranchId, e.getMessage());
            return false;
        }
        return true;
    }

    private boolean rollbackSourceBankBranchTransaction(String XID, String sourceBankBranchId) {
        // check existence of undo_log
        String filename = XID + "-" + sourceBankBranchId;
        File undoLogFile = new File(BANK_A_UNDO_LOG_PATH + filename + ".json");
        if (!undoLogFile.exists()) {
            LOG.error("undo_log {} does not exist", filename);
            return true;
        }
        // reversely replay undo_log
        try (BufferedReader br = new BufferedReader(new FileReader(undoLogFile))) {
            String line = br.readLine();
            UndoLog undoLog = JsonHelper.fromJson(line, UndoLog.class);
            String accountId = undoLog.getAccountId();
            double amount = undoLog.getAmount();
            TransferType type = undoLog.getType();
            if (type.equals(TransferType.CREDIT)) {
                accountMapper.payout(accountId, amount);
            } else {
                accountMapper.credit(accountId, amount);
            }
            session.commit();
            LOG.info("reversely replay undo_log, account {} balance is {}",
                    accountId, accountMapper.getBalance(accountId));
        } catch (Exception e) {
            session.rollback(); // avoid wrong commit
            LOG.error("error in reversely replay undo_log {}", e.getMessage());
            return false;
        }
        return true;
    }

    private boolean registerResourceManagers(String targetBankService) {
        transactionManager.setTransactionManagerId(sourceBankService + "-transaction-manager");
        String rs1 = transactionManager.registerTransactionManager();
        String rs2 = bankBClient.registerResourceManager(targetBankService);
        String rs3 = registerService.registerResourceManager(sourceBankService);
        if (REGISTER_SERVICE_FAILED.equals(rs1) || REGISTER_SERVICE_FAILED.equals(rs2)
                || REGISTER_SERVICE_FAILED.equals(rs3)) {
            LOG.error("error in registering service to TC");
            return false;
        }
        LOG.info("Successfully register {}, {}, {} to TC",
                transactionManager.getTransactionManagerId(), targetBankService, sourceBankService);
        return true;
    }

    private boolean validBank(String bank) {
        return Arrays.stream(BankEnum.values())
                .anyMatch(e -> e.getName().equals(bank));
    }

    private boolean validParam(String sourBankAccount, String targetBankService, String targetBankAccount
            , double amount) {
        return StringUtils.isNotBlank(sourBankAccount) &&
                StringUtils.isNotBlank(targetBankService) &&
                StringUtils.isNotBlank(targetBankAccount) &&
                amount >= 0;
    }

    private boolean sourceAccountExist(String sourceBankAccount) {
        return accountMapper.findById(sourceBankAccount) != null;
    }

    private boolean sourceAccountHasEnoughBalance(String sourceBankAccount, double amount) {
        return accountMapper.getBalance(sourceBankAccount) - amount >= 0;
    }

    private String registerGlobalTransaction(
            String transactionManagerId, String sourceBankService, String targetBankService) {
        JSONObject payload = new JSONObject();
        payload.put(TRANSACTION_MANAGER_ID, transactionManagerId);
        payload.put(SOURCE_BANK, sourceBankService);
        payload.put(TARGET_BANK, targetBankService);
        String XID = registerService.registerGlobalTransaction(payload);
        return XID;
    }

    /**
     * write db
     * write undo_log
     * commit txn
     * report success or failure to TC
     *
     * @return
     */
    private boolean handleSourceBankBranchTransaction(String XID, String branchId,
                                                      String accountId, double amount) {
        try {
            // write db
            accountMapper.payout(accountId, amount);
            // write undo_log
            writeUndoLog(XID, branchId, sourceBankService, accountId, TransferType.PAYOUT, amount);
            // commit txn here to release resource retention quickly
            session.commit();
            LOG.info("account {} paid {} from source bank {}", accountId, amount, sourceBankService);
        } catch (Exception e) {
            // report failure to TC
            JSONObject requestBody = new JSONObject();
            requestBody.put("xid", XID);
            requestBody.put("branch_id", branchId);
            requestBody.put("result", false);
            registerService.handleBranchTransactionReport(requestBody);
            LOG.error("error in transferring from account {} of bank {}", accountId, sourceBankService);
            return false;
        }

        // report success to TC
        JSONObject requestBody = new JSONObject();
        requestBody.put("xid", XID);
        requestBody.put("branch_id", branchId);
        requestBody.put("result", true);
        registerService.handleBranchTransactionReport(requestBody);
        LOG.info("transfer succeeded from bank {}, reporting to transaction coordinator", sourceBankService);
        return true;
    }

    private void writeUndoLog(String XID, String branchId, String bank, String accountId, TransferType type, double amount)
            throws IOException {
        File directory = new File(BANK_A_UNDO_LOG_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }
        UndoLog undoLog = new UndoLog();
        undoLog.setXid(XID);
        undoLog.setBranchId(branchId);
        undoLog.setBank(bank);
        undoLog.setAccountId(accountId);
        undoLog.setType(type);
        undoLog.setAmount(amount);
        String undoLogJson = JSON.toJSONString(undoLog);
        String filename = XID + "-" + branchId;
        File file = new File(BANK_A_UNDO_LOG_PATH + filename + ".json");
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(undoLogJson);
        LOG.info("writing current undo_log of bank {}, which is {}",
                sourceBankService, undoLogJson);
        bw.close();
    }

    private boolean handleTargetBankBranchTransaction(String XID, String branchId, String accountId, double amount) {
        JSONObject payload = new JSONObject();
        payload.put("xid", XID);
        payload.put("branch_id", branchId);
        payload.put("account_id", accountId);
        payload.put("amount", amount);
        return bankBClient.transfer(payload);
    }
}
