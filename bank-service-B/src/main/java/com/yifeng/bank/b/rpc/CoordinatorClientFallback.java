package com.yifeng.bank.b.rpc;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import static com.yifeng.commons.constant.TransferServiceConstant.*;
import static com.yifeng.commons.constant.TransferServiceConstant.REGISTER_BRANCH_TRANSACTION_FAILED;

@Component
public class CoordinatorClientFallback implements CoordinatorClient {
    @Override
    public String registerService(String serviceName) {
        return REGISTER_SERVICE_FAILED;
    }

    @Override
    public String registerGlobalTxn(JSONObject payload) {
        return REGISTER_GLOBAL_TRANSACTION_FAILED;
    }

    @Override
    public String registerBranchTransaction(String XID) {
        return REGISTER_BRANCH_TRANSACTION_FAILED;
    }

    @Override
    public String handleBranchTransactionReport(JSONObject payload) {
        return HANDLE_BRANCH_TRANSACTION_FAILED;
    }
}
