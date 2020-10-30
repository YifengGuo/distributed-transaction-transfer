package com.yifeng.pojo;

/**
 * Created by guoyifeng on 10/30/20
 */
public class Account {

    private String accountId;

    private double balance;  // double for simplicity

    public Account(String accountId, double balance) {
        this.accountId = accountId;
        this.balance = balance;
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
}
