package com.yifeng.coordinator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yifeng.commons.constant.TransferServiceConstant.*;

@RestController
@RequestMapping("/coordinator")
public class CoordinatorController {

    private static final Logger LOG = LoggerFactory.getLogger(CoordinatorController.class);

    private static final List<String> REGISTERED_SERVICES = new ArrayList<>();

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

    @RequestMapping(value = "/register-global-transaction", method = RequestMethod.GET)
    public String registerGlobalTxn() {
        // TODO: return XID
        return "";
    }

    @RequestMapping(value = "/register-branch-transaction", method = RequestMethod.GET)
    public String registerBranchTxn() {
        // TODO: return branchId
        return "";
    }
}
