package com.yifeng.bank.a.controller;

import com.yifeng.bank.a.rpc.BankBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by guoyifeng on 11/2/20
 *
 * test eureka client
 */
@RestController
public class HelloController {

    @Autowired
    private BankBClient bankBClient;

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * test rpc
     * @return
     */
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
       return bankBClient.hello();
    }

    /**
     * test service discover client
     * @return
     */
    @RequestMapping(value = "/discover-client", method = RequestMethod.GET)
    public String index() {
        List<String> services = discoveryClient.getServices();
        List<ServiceInstance> instances = discoveryClient.getInstances("bank-service-A");
        System.out.println(services);
        for (ServiceInstance instance : instances) {
            // 10.17.37.139  BANK-SERVICE-A
            System.out.println(instance.getHost() + "  " + instance.getServiceId());
        }
        return "Hello";
    }
}
