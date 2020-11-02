package com.yifeng.bank.a.rpc;

import org.springframework.stereotype.Component;

/**
 * Created by guoyifeng on 11/2/20
 */
@Component
public class BankBClientFallback implements BankBClient {

    @Override
    public String hello() {
        return "fallback";
    }

    @Override
    public String transfer(Double amount) {
        return "fallback";
    }
}
