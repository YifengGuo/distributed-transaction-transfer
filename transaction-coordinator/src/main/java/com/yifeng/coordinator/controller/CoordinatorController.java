package com.yifeng.coordinator.controller;

import com.alibaba.fastjson.JSONObject;
import com.yifeng.commons.constant.BranchTransactionState;
import com.yifeng.commons.pojo.BaseTransaction;
import com.yifeng.commons.pojo.BranchTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.yifeng.commons.constant.TransferServiceConstant.*;

@RestController
@RequestMapping("/coordinator")
public class CoordinatorController {

    private static final Logger LOG = LoggerFactory.getLogger(CoordinatorController.class);

    private static final Set<String> REGISTERED_SERVICES = new HashSet<>();

    // key: XID
    private static final Map<String, BaseTransaction> REGISTERED_GLOBAL_TRANSACTIONS = new HashMap<>();

    // key: XID, value map: key: branchId, value: branchTxn
    private static final Map<String, Map<String, BranchTransaction>> REGISTERED_BRANCH_TRANSACTIONS = new HashMap<>();

    // shall be invoked in post-construct method of each involved service
    @RequestMapping(value = "/register-service", method = RequestMethod.GET)
    public String register(@RequestParam String serviceName) {
        try {
            // in production service name shall be added with random postfix to be unique
            REGISTERED_SERVICES.add(serviceName);
            LOG.info("service {} registered on TC", serviceName);
            return REGISTER_SERVICE_SUCCESS;
        } catch (Exception e) {
            LOG.error("error in registering {}", serviceName);
            throw new RuntimeException("REGISTERED_SERVICE_FAILED");
        }
    }

    @RequestMapping(value = "/register-global-transaction", method = RequestMethod.POST)
    public String registerGlobalTxn(@RequestBody JSONObject payload) {
        try {
            String transactionManagerId = payload.getString(TRANSACTION_MANAGER_ID);
            String sourceBank = payload.getString(SOURCE_BANK);
            String targetBank = payload.getString(TARGET_BANK);
            BaseTransaction currentGlobalTxn = new BaseTransaction(transactionManagerId, sourceBank, targetBank);
            String XID = UUID.randomUUID().toString();
            LOG.info("generate XID {} for current global transaction {}", XID, currentGlobalTxn);
            REGISTERED_GLOBAL_TRANSACTIONS.put(XID, currentGlobalTxn);
            return XID;
        } catch (Exception e) {
            LOG.error("error in registering global transaction");
            throw new RuntimeException("REGISTERED_GLOBAL_TRANSACTIONS_FAILED");
        }
    }

    @RequestMapping(value = "/register-branch-transaction", method = RequestMethod.GET)
    public String registerBranchTxn(@RequestParam("XID") String XID) {
        if (!REGISTERED_GLOBAL_TRANSACTIONS.containsKey(XID)) {
            LOG.error("current XID {} does not exist or is expired", XID);
            throw new RuntimeException("current XID does not exist or is expired");
        }
        try {
            String branchId = UUID.randomUUID().toString();
            BaseTransaction baseTransaction = REGISTERED_GLOBAL_TRANSACTIONS.get(XID);
            BranchTransaction branchTransaction = new BranchTransaction(
                    baseTransaction.getTransactionManagerId(),
                    baseTransaction.getSourceBankId(),
                    baseTransaction.getTargetBankId(),
                    branchId
            );
            REGISTERED_BRANCH_TRANSACTIONS.compute(XID, (k, v) -> {
                if (v == null) {
                    v = new HashMap<>();
                }
                v.put(branchId, branchTransaction);
                LOG.info("current branch map is {}", REGISTERED_BRANCH_TRANSACTIONS.get(XID));
                return v;
            });
            LOG.info("Generate branchId {} under XID {}", branchId, XID);
            return branchId;
        } catch (Exception e) {
            LOG.error("failed to register branch id under XID {}", XID);
            throw new RuntimeException("failed to register branch id under XID " + XID);
        }
    }

    @RequestMapping(value = "/branch-report", method = RequestMethod.POST)
    public String handleBranchTransactionReport(@RequestBody JSONObject payload) {
        try {
            String XID = payload.getString("xid");
            String branchId = payload.getString("branch_id");
            boolean branchTxnResult = payload.getBoolean("result");
            if (branchTxnResult) {
                REGISTERED_BRANCH_TRANSACTIONS.get(XID).get(branchId).setState(BranchTransactionState.SUCCESS);
            } else {
                REGISTERED_BRANCH_TRANSACTIONS.get(XID).get(branchId).setState(BranchTransactionState.FAILURE);
            }
            LOG.info("received branch txn {}, result is {}", branchId, branchTxnResult);
            return "OK";
        } catch (Exception e) {
            LOG.error("error in handling branch transaction", e);
            return "error";
        }
    }

    @RequestMapping(value = "/close-global-transaction", method = RequestMethod.GET)
    public boolean closeGlobalTransaction(@RequestParam("XID") String XID) {
        try {
            // remove service name in set and mappings in map
            BaseTransaction baseTransaction = REGISTERED_GLOBAL_TRANSACTIONS.get(XID);
            REGISTERED_SERVICES.remove(baseTransaction.getSourceBankId());
            REGISTERED_SERVICES.remove(baseTransaction.getTargetBankId());
            REGISTERED_SERVICES.remove(baseTransaction.getTransactionManagerId());

            REGISTERED_GLOBAL_TRANSACTIONS.remove(XID);

            REGISTERED_BRANCH_TRANSACTIONS.remove(XID);

            LOG.info("global txn {} has been deleted", XID);

        } catch (Exception e) {
            LOG.error("error in closing global transaction, please retry... {}", e.getMessage());
            return false;
        }
        return true;
    }
}
