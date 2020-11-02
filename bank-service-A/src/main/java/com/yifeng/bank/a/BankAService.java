package com.yifeng.bank.a;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Created by guoyifeng on 11/2/20
 */
@SpringBootApplication
@EnableEurekaClient
public class BankAService {
    public static void main(String[] args) {
        SpringApplication.run(BankAService.class, args);
    }
}
