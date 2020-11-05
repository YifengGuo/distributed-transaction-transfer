package com.yifeng.bank.a.rpc;

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
    public String transfer(Double amount) {
        return FALL_BACK;
    }

    @Override
    public String registerResourceManager(String serviceName) {
        return REGISTER_SERVICE_FAILED;
    }

    @Override
    public String registerBranchTransaction(String XID) {
        return REGISTER_BRANCH_TRANSACTION_FAILED;
    }
}
