package com.yifeng.bank.b.service.impl;

import com.yifeng.bank.b.service.HelloService;
import org.springframework.stereotype.Service;

/**
 * Created by guoyifeng on 11/2/20
 */
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello() {
        return "Hello from B";
    }
}
