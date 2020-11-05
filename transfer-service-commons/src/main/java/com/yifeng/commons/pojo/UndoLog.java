package com.yifeng.commons.pojo;

import com.yifeng.commons.constant.TransferType;

/**
 * Created by guoyifeng on 11/5/20
 */
public class UndoLog {

    private String bank;

    private String accountId;

    private TransferType type;

    private String xid;

    private String branchId;

    private double amount;

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public TransferType getType() {
        return type;
    }

    public void setType(TransferType type) {
        this.type = type;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
