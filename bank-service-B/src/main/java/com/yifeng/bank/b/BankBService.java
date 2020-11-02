package com.yifeng.bank.b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created by guoyifeng on 11/2/20
 *
 * act as service provider
 * service would be invoked by bank-service-A
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableDiscoveryClient
public class BankBService {
    public static void main(String[] args) {
        SpringApplication.run(BankBService.class, args);
    }
}
