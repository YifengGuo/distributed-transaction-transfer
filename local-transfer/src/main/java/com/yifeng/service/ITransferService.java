package com.yifeng.service;

import com.yifeng.pojo.Account;

import java.util.List;

/**
 * Created by guoyifeng on 10/30/20
 */
public interface ITransferService {

    List<Account> findAll();

    boolean transfer(String from, String to, double value);
}
