package com.yifeng.service;

import com.yifeng.pojo.Account;

/**
 * Created by guoyifeng on 10/30/20
 */
public interface ITransferService {

    boolean transfer(Account from, Account to, double value);
}
