package com.yifeng.bank.b.controller;

import com.yifeng.bank.b.service.impl.RegisterServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bank-b")
public class RegisterController {

    @Autowired
    RegisterServiceImpl registerService;

    @RequestMapping("/register-service")
    public String registerResourceManager(@RequestParam("serviceName") String serviceName) {
        return registerService.registerResourceManager(serviceName);
    }


    @RequestMapping("/register-branch-transaction")
    public String registerBranchTransaction() {
        return null;
    }
}
