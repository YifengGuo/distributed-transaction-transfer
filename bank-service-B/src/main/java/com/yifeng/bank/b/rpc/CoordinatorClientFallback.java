package com.yifeng.bank.b.rpc;

import org.springframework.stereotype.Component;

import static com.yifeng.commons.constant.TransferServiceConstant.FALL_BACK;

@Component
public class CoordinatorClientFallback implements CoordinatorClient {
    @Override
    public String registerService(String serviceName) {
        return FALL_BACK;
    }

    @Override
    public String registerGlobalTxn() {
        return FALL_BACK;
    }

    @Override
    public String registerBranchTransaction() {
        return FALL_BACK;
    }
}
