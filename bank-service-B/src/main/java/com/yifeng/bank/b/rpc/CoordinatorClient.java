package com.yifeng.bank.b.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "transaction-coordinator", fallback = CoordinatorClientFallback.class)
public interface CoordinatorClient {

    @GetMapping("/coordinator/register-service")
    String registerService(@RequestParam("serviceName") String serviceName);

    @GetMapping("/coordinator/register-global-transaction")
    String registerGlobalTxn();

    @GetMapping("/coordinator/register-branch-transaction")
    String registerBranchTransaction();
}
