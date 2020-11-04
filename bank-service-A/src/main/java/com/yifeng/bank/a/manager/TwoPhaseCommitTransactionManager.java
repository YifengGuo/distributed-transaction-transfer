package com.yifeng.bank.a.manager;

import com.yifeng.bank.a.rpc.CoordinatorClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Transaction manager which coordinates and manages transactions
 * it holds global transaction and manages life cycle of transaction and coordinates each Resource Manager (bank service)
 */
@Component
public class TwoPhaseCommitTransactionManager {

    @Autowired
    CoordinatorClient coordinatorClient;

    private String transactionManagerId;

    public String getTransactionManagerId() {
        return transactionManagerId;
    }

    public void setTransactionManagerId(String transactionManagerId) {
        this.transactionManagerId = transactionManagerId;
    }

    public String registerTransactionManager() {
        return coordinatorClient.registerService(this.transactionManagerId);
    }


}
