package com.yifeng;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author guoyifeng on 11/1/20
 */
@EnableEurekaServer
@SpringBootApplication
public class RegistryApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(RegistryApp.class).web(WebApplicationType.SERVLET).run(args);
    }
}
