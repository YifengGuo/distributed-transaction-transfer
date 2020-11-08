package com.yifeng.bank.a.rpc;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import static com.yifeng.commons.constant.TransferServiceConstant.*;

/**
 * Created by guoyifeng on 11/2/20
 */
@Component
public class BankBClientFallback implements BankBClient {

    @Override
    public String hello() {
        return FALL_BACK;
    }

    @Override
    public boolean transfer(JSONObject payload) {
        return false;
    }

    @Override
    public String registerResourceManager(String serviceName) {
        return REGISTER_SERVICE_FAILED;
    }

    @Override
    public String registerBranchTransaction(String XID) {
        return REGISTER_BRANCH_TRANSACTION_FAILED;
    }

    @Override
    public boolean rollbackTargetBankBranchTransaction(String XID, String branchId) {
        return false;
    }

    @Override
    public boolean deleteUndoLog(String xid, String targetBankBranchId) {
        return false;
    }

    @Override
    public double getBalance(String targetBankAccount) {
        return -1;
    }
}
