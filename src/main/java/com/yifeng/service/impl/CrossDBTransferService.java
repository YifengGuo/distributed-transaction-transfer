package com.yifeng.service.impl;

import com.yifeng.pojo.Account;
import com.yifeng.service.ITransferService;

import java.util.List;

/**
 * Created by guoyifeng on 10/30/20
 */
public class CrossDBTransferService implements ITransferService {

    @Override
    public List<Account> findAll() {
        return null;
    }

    @Override
    public boolean transfer(String from, String to, double value) {
        return false;
    }
}
