package com.yifeng.bank.b.service.impl;

import com.yifeng.bank.b.rpc.CoordinatorClient;
import com.yifeng.bank.b.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    CoordinatorClient coordinatorClient;

    @Override
    public String registerResourceManager(String serviceName) {
        return coordinatorClient.registerService(serviceName);
    }

    @Override
    public String registerBranchTransaction() {
        return null;
    }
}
