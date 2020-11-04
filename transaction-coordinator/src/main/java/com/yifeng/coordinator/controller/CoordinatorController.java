package com.yifeng.coordinator.controller;

import com.alibaba.fastjson.JSONObject;
import com.yifeng.commons.pojo.BaseTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.yifeng.commons.constant.TransferServiceConstant.*;

@RestController
@RequestMapping("/coordinator")
public class CoordinatorController {

    private static final Logger LOG = LoggerFactory.getLogger(CoordinatorController.class);

    private static final List<String> REGISTERED_SERVICES = new ArrayList<>();

    private static final Map<String, BaseTransaction> REGISTERED_GLOBAL_TRANSACTIONS = new HashMap<>();

    private static final Set<String> XID_SET = new HashSet<>();

    private static final Map<String, String> REGISTERED_BRANCH_TRANSACTIONS = new HashMap<>();

    // shall be invoked in post-construct method of each involved service
    @RequestMapping(value = "/register-service", method = RequestMethod.GET)
    public String register(@RequestParam String serviceName) {
        try {
            REGISTERED_SERVICES.add(serviceName);
            LOG.info("service {} registered on TC", serviceName);
            return REGISTER_SERVICE_SUCCESS;
        } catch (Throwable e) {
            LOG.error("error in registering {}", serviceName);
        }
        return REGISTER_SERVICE_FAILED;
    }

    @RequestMapping(value = "/register-global-transaction", method = RequestMethod.POST)
    public String registerGlobalTxn(@RequestBody JSONObject payload) {
        String transactionManagerId = payload.getString(TRANSACTION_MANAGER_ID);
        String sourceBank = payload.getString(SOURCE_BANK);
        String targetBank = payload.getString(TARGET_BANK);
        BaseTransaction currentGlobalTxn = new BaseTransaction(transactionManagerId, sourceBank, targetBank);
        REGISTERED_GLOBAL_TRANSACTIONS.put(transactionManagerId, currentGlobalTxn);
        String XID = UUID.randomUUID().toString();
        LOG.info("generate XID {} for current global transaction {}", XID, currentGlobalTxn);
        return XID;
    }

    @RequestMapping(value = "/register-branch-transaction", method = RequestMethod.GET)
    public String registerBranchTxn() {
        // TODO: return branchId
        return "";
    }
}
