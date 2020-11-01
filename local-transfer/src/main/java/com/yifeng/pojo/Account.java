package com.yifeng.pojo;

import com.alibaba.fastjson.JSON;

/**
 * Created by guoyifeng on 10/30/20
 */
public class Account {

    private String id;

    private String accountId;

    private double balance;  // double for simplicity

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
