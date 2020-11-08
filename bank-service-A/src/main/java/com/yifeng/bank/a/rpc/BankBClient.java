package com.yifeng.bank.a.rpc;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Created by guoyifeng on 11/2/20
 */
@FeignClient(value = "bank-service-B", fallback = BankBClientFallback.class)
public interface BankBClient {

    @GetMapping("/bank-b/hello")
    String hello();

    @PostMapping("/bank-b/transfer")
    boolean transfer(@RequestBody JSONObject payload);

    @GetMapping("/bank-b/register-service")
    String registerResourceManager(@RequestParam("serviceName") String serviceName);

    @GetMapping("/bank-b/register-branch-transaction")
    String registerBranchTransaction(@RequestParam("XID") String XID);

    @GetMapping("/bank-b/rollback-branch-transaction")
    boolean rollbackTargetBankBranchTransaction(@RequestParam("XID") String XID, @RequestParam("branchId") String branchId);

    @GetMapping("/bank-b/delete-undolog")
    boolean deleteUndoLog(@RequestParam("XID") String xid, @RequestParam("branchId") String targetBankBranchId);

    @GetMapping("/bank-b/balance")
    double getBalance(@RequestParam("accountId") String targetBankAccount);
}
