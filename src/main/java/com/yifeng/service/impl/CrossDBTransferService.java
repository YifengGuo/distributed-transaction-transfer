package com.yifeng.service.impl;

import com.yifeng.pojo.Account;
import com.yifeng.service.ITransferService;

/**
 * Created by guoyifeng on 10/30/20
 */
public class CrossDBTransferService implements ITransferService {

    @Override
    public boolean transfer(Account from, Account to, double value) {
        return false;
    }
}
