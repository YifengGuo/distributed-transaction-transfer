package com.yifeng;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author guoyifeng on 11/1/20
 */
@EnableEurekServer
@SpringBootApplication
public class RegistryApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(RegistryApp.class).web(WebApplicationType.SERVLET).run(args);
    }
}
