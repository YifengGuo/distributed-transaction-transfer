package com.yifeng.commons.pojo;

import com.alibaba.fastjson.JSON;

import java.util.Objects;

/**
 * Created by guoyifeng on 11/4/20
 */
public class BaseTransaction {

    protected String transactionManagerId;

    protected String sourceBankId;

    protected String targetBankId;

    public BaseTransaction(String transactionManagerId, String sourceBankId, String targetBankId) {
        this.transactionManagerId = transactionManagerId;
        this.sourceBankId = sourceBankId;
        this.targetBankId = targetBankId;
    }

    public String getTransactionManagerId() {
        return transactionManagerId;
    }

    public void setTransactionManagerId(String transactionManagerId) {
        this.transactionManagerId = transactionManagerId;
    }

    public String getSourceBankId() {
        return sourceBankId;
    }

    public void setSourceBankId(String sourceBankId) {
        this.sourceBankId = sourceBankId;
    }

    public String getTargetBankId() {
        return targetBankId;
    }

    public void setTargetBankId(String targetBankId) {
        this.targetBankId = targetBankId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseTransaction that = (BaseTransaction) o;
        return Objects.equals(transactionManagerId, that.transactionManagerId) &&
                Objects.equals(sourceBankId, that.sourceBankId) &&
                Objects.equals(targetBankId, that.targetBankId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionManagerId, sourceBankId, targetBankId);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
