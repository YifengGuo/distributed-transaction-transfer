package com.yifeng.bank.b.dao;

import com.yifeng.commons.pojo.Account;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guoyifeng on 11/8/20
 */
@Repository
public interface AccountDao {
    List<Account> findAll();

    Account findById(@Param("accountId") String accountId);

    void payout(@Param("accountId") String accountId, @Param("amount") double amount);

    void credit(@Param("accountId") String accountId, @Param("amount") double amount);

    double getBalance(@Param("accountId") String accountId);
}
