package com.yifeng.bank.a.controller;

import com.yifeng.bank.a.manager.TransactionManager;
import com.yifeng.bank.a.rpc.BankBClient;
import com.yifeng.bank.a.service.impl.RegisterServiceImpl;
import com.yifeng.commons.constant.BankEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static com.yifeng.commons.constant.TransferServiceConstant.REGISTER_SERVICE_FAILED;

@RestController
@RequestMapping("/bank-a")
public class TransferController {

    private static final Logger LOG = LoggerFactory.getLogger(TransferController.class);

    @Autowired
    TransactionManager transactionManager;

    @Autowired
    BankBClient bankBClient;

    @Autowired
    RegisterServiceImpl registerService;

    @Value("${spring.application.name}")
    String serviceName;

    @RequestMapping("/transfer")
    public String transfer(@RequestParam("target-bank") String targetBankAccount, @RequestParam double amount) {

        if (targetBankAccount.equals(serviceName) || !validBank(targetBankAccount)) {
            LOG.error("wrong target bank {}", targetBankAccount);
            return "wrong target bank name";
        }

        // step 1: TM, RM register to TC
        registerResourceManagers(targetBankAccount);

        // step 2: TM opens global transaction

        // step 3: bank A registers branch txn, write db and undo_log, commit branch txn

        // step 4: bank B registers branch txn, write db and undo_log, commit branch txn

        // step 5: TM commit global txn

        // step 6: RM delete undo_log

        return null;
    }

    private boolean registerResourceManagers(String targetBankAccount) {
        transactionManager.setTransactionManagerId(serviceName + "-transaction-manager");
        String rs1 = transactionManager.registerTransactionManager();
        String rs2 = bankBClient.registerResourceManager(targetBankAccount);
        String rs3 = registerService.registerResourceManager(serviceName);
        if (REGISTER_SERVICE_FAILED.equals(rs1) || REGISTER_SERVICE_FAILED.equals(rs2)
                || REGISTER_SERVICE_FAILED.equals(rs3)) {
            LOG.error("error in registering service to TC");
            return false;
        }
        LOG.info("Successfully register {}, {}, {} to TC",
                transactionManager.getTransactionManagerId(), targetBankAccount, serviceName);
        return true;
    }

    private boolean validBank(String bank) {
        return Arrays.stream(BankEnum.values())
                .anyMatch(e -> e.name().equals(bank));
    }
}
