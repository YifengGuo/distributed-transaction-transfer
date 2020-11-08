package com.yifeng.bank.b.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yifeng.bank.b.dao.AccountDao;
import com.yifeng.bank.b.rpc.CoordinatorClient;
import com.yifeng.bank.b.service.RegisterService;
import com.yifeng.bank.b.util.SessionFactoryUtils;
import com.yifeng.commons.constant.TransferType;
import com.yifeng.commons.pojo.UndoLog;
import com.yifeng.commons.util.JsonHelper;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static com.yifeng.commons.constant.TransferServiceConstant.BANK_A_UNDO_LOG_PATH;
import static com.yifeng.commons.constant.TransferServiceConstant.BANK_B_UNDO_LOG_PATH;

@Service
public class RegisterServiceImpl implements RegisterService {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterServiceImpl.class);

    @Autowired
    CoordinatorClient coordinatorClient;

    private SqlSession session = SessionFactoryUtils.getSession();

    private AccountDao accountMapper = session.getMapper(AccountDao.class);

    @Override
    public String registerResourceManager(String serviceName) {
        return coordinatorClient.registerService(serviceName);
    }

    @Override
    public String registerBranchTransaction(String XID) {
        return coordinatorClient.registerBranchTransaction(XID);
    }

    @Override
    public boolean rollback(String XID, String branchId) {
        // check existence of undo_log
        String filename = XID + "-" + branchId;
        File undoLogFile = new File(BANK_B_UNDO_LOG_PATH + filename + ".json");
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

    @Override
    public boolean deleteUndoLog(String XID, String branchId) {
        try {
            String filename = XID + "-" + branchId;
            File undoLogFile = new File(BANK_B_UNDO_LOG_PATH + filename + ".json");
            undoLogFile.delete();
            LOG.info("undo_log {} is deleted", BANK_B_UNDO_LOG_PATH + filename + ".json");
        } catch (Exception e) {
            LOG.error("error in deleting undo_log {}, {}", XID + "-" + branchId, e.getMessage());
            return false;
        }
        return true;
    }
}
