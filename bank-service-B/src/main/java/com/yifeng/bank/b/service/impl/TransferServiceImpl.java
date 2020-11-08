package com.yifeng.bank.b.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yifeng.bank.b.dao.AccountDao;
import com.yifeng.bank.b.rpc.CoordinatorClient;
import com.yifeng.bank.b.service.TransferService;
import com.yifeng.bank.b.util.SessionFactoryUtils;
import com.yifeng.commons.constant.TransferType;
import com.yifeng.commons.pojo.UndoLog;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.yifeng.commons.constant.TransferServiceConstant.BANK_B_UNDO_LOG_PATH;

/**
 * Created by guoyifeng on 11/8/20
 */
@Service
public class TransferServiceImpl implements TransferService {

    private static final Logger LOG = LoggerFactory.getLogger(TransferServiceImpl.class);

    @Autowired
    CoordinatorClient coordinatorClient;

    private SqlSession session = SessionFactoryUtils.getSession();

    private AccountDao accountMapper = session.getMapper(AccountDao.class);

    @Value("${spring.application.name}")
    String targetBankService;

    @Override
    public boolean transfer(JSONObject payload) {
        String XID = "";
        String branchId = "";
        String accountId = "";
        double amount = 0;
        try {
            XID = payload.getString("xid");
            branchId = payload.getString("branch_id");
            amount = payload.getDouble("amount");
            accountId = payload.getString("account_id");
            // write db
            accountMapper.credit(accountId, amount);
            // write undo_log
            writeUndoLog(XID, branchId, targetBankService, accountId, TransferType.CREDIT, amount);
            // commit txn here to release resource retention quickly
            session.commit();
            LOG.info("account {} received {} of target bank {}", accountId, amount, targetBankService);
        } catch (Exception e) {
            // report failure to TC
            JSONObject requestBody = new JSONObject();
            requestBody.put("xid", XID);
            requestBody.put("branch_id", branchId);
            requestBody.put("result", false);
            LOG.info("bank B rollback request body is {}", requestBody.toJSONString());
            coordinatorClient.handleBranchTransactionReport(requestBody);
            LOG.error("error in transferring to target account {} of bank {}. {}", accountId, targetBankService, e.getMessage());
            return false;
        }
        // report success to TC
        JSONObject requestBody = new JSONObject();
        requestBody.put("xid", XID);
        requestBody.put("branch_id", branchId);
        requestBody.put("result", true);
        coordinatorClient.handleBranchTransactionReport(requestBody);
        LOG.info("transfer succeeded to bank {}, reporting to transaction coordinator", targetBankService);
        return true;
    }

    private void writeUndoLog(String XID, String branchId, String bank, String accountId, TransferType type, double amount)
            throws IOException {
        // create undolog dir if not exists
        File directory = new File(BANK_B_UNDO_LOG_PATH);
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
        File file = new File(BANK_B_UNDO_LOG_PATH + filename + ".json");
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(undoLogJson);
        LOG.info("writing current undo_log of bank {}, which is {}",
                targetBankService, undoLogJson);
        bw.close();
    }

    @Override
    public double getBalance(String accountId) {
        return accountMapper.getBalance(accountId);
    }
}
