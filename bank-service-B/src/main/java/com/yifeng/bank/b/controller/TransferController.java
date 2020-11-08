package com.yifeng.bank.b.controller;

import com.alibaba.fastjson.JSONObject;
import com.yifeng.bank.b.service.impl.TransferServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by guoyifeng on 11/8/20
 */
@RestController
@RequestMapping("/bank-b")
public class TransferController {

    @Autowired
    private TransferServiceImpl transferService;

    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public boolean transfer(@RequestBody JSONObject payload) {
        return transferService.transfer(payload);
    }

    @RequestMapping(value = "/balance", method = RequestMethod.GET)
    public double getBalance(@RequestParam("accountId") String accountId) {
        return transferService.getBalance(accountId);
    }

}
