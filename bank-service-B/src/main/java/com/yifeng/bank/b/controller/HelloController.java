package com.yifeng.bank.b.controller;

import com.yifeng.bank.b.service.impl.HelloServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by guoyifeng on 11/2/20
 */
@RestController
@RequestMapping("/bank-b")
public class HelloController {

    @Autowired
    private HelloServiceImpl helloService;

    @RequestMapping("/hello")
    public String hello() {
        return helloService.hello();
    }
}
