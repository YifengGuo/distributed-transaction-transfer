package com.yifeng.commons.constant;

/**
 * Created by guoyifeng on 11/5/20
 */
public enum BranchTransactionState {

    NONE("none"),
    FAILURE("failure"),
    SUCCESS("success"),
    FINISHED("finished");

    private String state;

    BranchTransactionState(String state) {

        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
