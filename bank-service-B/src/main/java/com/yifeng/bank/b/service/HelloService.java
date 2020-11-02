package com.yifeng.bank.b.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by guoyifeng on 11/2/20
 */
@FeignClient("bank-service-B")
public interface HelloService {

    @RequestMapping("/hello")
    String hello();
}
