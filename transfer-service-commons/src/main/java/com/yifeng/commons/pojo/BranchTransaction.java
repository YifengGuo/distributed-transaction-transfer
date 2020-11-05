package com.yifeng.commons.pojo;

import com.alibaba.fastjson.JSON;
import com.yifeng.commons.constant.BranchTransactionState;

import java.util.Objects;

/**
 * Created by guoyifeng on 11/5/20
 */
public class BranchTransaction extends BaseTransaction {

    private String branchTransactionId;

    private BranchTransactionState state;  // current branch complete or not

    public BranchTransaction(String transactionManagerId, String sourceBankId,
                             String targetBankId, String branchTransactionId) {
        super(transactionManagerId, sourceBankId, targetBankId);
        this.branchTransactionId = branchTransactionId;
        this.state = BranchTransactionState.NONE;
    }

    public BranchTransactionState getState() {
        return state;
    }

    public void setState(BranchTransactionState state) {
        this.state = state;
    }

    public String getBranchTransactionId() {
        return branchTransactionId;
    }

    public void setBranchTransactionId(String branchTransactionId) {
        this.branchTransactionId = branchTransactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BranchTransaction that = (BranchTransaction) o;
        return Objects.equals(branchTransactionId, that.branchTransactionId) &&
                state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), branchTransactionId, state);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
