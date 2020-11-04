package com.yifeng.bank.a.rpc;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import static com.yifeng.commons.constant.TransferServiceConstant.*;

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
    public String registerBranchTransaction() {
        return REGISTER_BRANCH_TRANSACTION_FAILED;
    }
}
