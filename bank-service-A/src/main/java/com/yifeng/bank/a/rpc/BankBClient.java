package com.yifeng.bank.a.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by guoyifeng on 11/2/20
 */
@FeignClient(value = "bank-service-B", fallback = BankBClientFallback.class)
public interface BankBClient {

    @GetMapping("/transfer/bank-b/hello")
    String hello();

    @GetMapping("/transfer/bank-b/transfer")
    String transfer(@RequestParam("amount") Double amount);
}
