package com.yifeng.coordinator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/coordinator")
public class CoordinatorController {

    private static final Map<String, String> REGISTERED_SERVICES = new HashMap<>();

    private static final Map<String, String> REGISTERED_BRANCH_TRANSACTIONS = new HashMap<>();


    // shall be invoked in post-construct method of each involved service
    @RequestMapping("/register-service")
    public String register() {
        return "";
    }

    @RequestMapping("/register-global-transaction")
    public String registerGlobalTxn() {
        // TODO: return XID
        return "";
    }

    @RequestMapping("/register-branch-transaction")
    public String registerBranchTxn() {
        // TODO: return branchId
        return "";
    }
}
