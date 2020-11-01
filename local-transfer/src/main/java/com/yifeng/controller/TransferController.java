package com.yifeng.controller;

import com.yifeng.config.Config;
import com.yifeng.pojo.Account;
import com.yifeng.service.impl.LocalTransferService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by guoyifeng on 10/30/20
 */
@RestController
@RequestMapping("/transfer")
@CrossOrigin(origins = "*")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class TransferController {

    @Autowired
    private LocalTransferService localTransferService;

    @Test
    public void test1() {
        List<Account> list = localTransferService.findAll();
        for (Account account : list) {
            System.out.println(account);
        }
    }

    @Test
    public void test2_localTransfer() {
        for (int i = 0; i < 100; ++i) {
            localTransferService.transfer("A", "B", 200);
            test1();
            System.out.println();
        }
    }

}
