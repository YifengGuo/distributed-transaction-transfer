package com.yifeng.bank.a.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by guoyifeng on 11/2/20
 */
@FeignClient(value = "bank-service-B", fallback = BankBClientFallback.class)
public interface BankBClient {

    @GetMapping("/bank-b/hello")
    String hello();

    @GetMapping("/bank-b/transfer")
    String transfer(@RequestParam("amount") Double amount);

    @GetMapping("/bank-b/register-service")
    String registerResourceManager(@RequestParam("serviceName") String serviceName);

    @GetMapping("/bank-b/register-branch-transaction")
    String registerBranchTransaction();
}
