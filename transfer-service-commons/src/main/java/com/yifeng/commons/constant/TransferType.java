package com.yifeng.commons.constant;

/**
 * Created by guoyifeng on 11/5/20
 */
public enum TransferType {
    PAYOUT("payout"),
    CREDIT("credit");

    private String type;

    TransferType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.getType();
    }
}
