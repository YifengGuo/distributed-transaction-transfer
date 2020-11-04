package com.yifeng.bank.a.controller;

import com.alibaba.fastjson.JSONObject;
import com.yifeng.bank.a.dao.AccountDao;
import com.yifeng.bank.a.manager.TwoPhaseCommitTransactionManager;
import com.yifeng.bank.a.rpc.BankBClient;
import com.yifeng.bank.a.service.impl.RegisterServiceImpl;
import com.yifeng.commons.constant.BankEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    AccountDao accountMapper;

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

        // step 1: TM, RM register to TC
        boolean registerResult = registerResourceManagers(targetBankAccount);
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

        // step 4: bank B registers branch txn, write db and undo_log, commit branch txn

        // step 5: TM commit global txn

        // step 6: RM delete undo_log

        return null;
    }

    private boolean registerResourceManagers(String targetBankAccount) {
        transactionManager.setTransactionManagerId(sourceBankService + "-transaction-manager");
        String rs1 = transactionManager.registerTransactionManager();
        String rs2 = bankBClient.registerResourceManager(targetBankAccount);
        String rs3 = registerService.registerResourceManager(sourceBankService);
        if (REGISTER_SERVICE_FAILED.equals(rs1) || REGISTER_SERVICE_FAILED.equals(rs2)
                || REGISTER_SERVICE_FAILED.equals(rs3)) {
            LOG.error("error in registering service to TC");
            return false;
        }
        LOG.info("Successfully register {}, {}, {} to TC",
                transactionManager.getTransactionManagerId(), targetBankAccount, sourceBankService);
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
}
