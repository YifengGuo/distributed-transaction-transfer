package com.yifeng.bank.a.rpc;

import org.springframework.stereotype.Component;

import static com.yifeng.commons.constant.TransferServiceConstant.REGISTER_SERVICE_FAILED;

@Component
public class CoordinatorClientFallback implements CoordinatorClient {
    @Override
    public String registerService(String serviceName) {
        return REGISTER_SERVICE_FAILED;
    }

    @Override
    public String registerGlobalTxn() {
        return REGISTER_SERVICE_FAILED;
    }

    @Override
    public String registerBranchTransaction() {
        return REGISTER_SERVICE_FAILED;
    }
}
