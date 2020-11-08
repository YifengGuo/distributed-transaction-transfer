package com.yifeng.bank.b.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by guoyifeng on 11/8/20
 */
public interface TransferService {

    boolean transfer(JSONObject payload);

    double getBalance(String accountId);
}
