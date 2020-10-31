package com.yifeng.dao;

import com.yifeng.pojo.Account;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountDao {

    List<Account> findAll();

    void payout(@Param("accountId") String accountId, @Param("amount") double amount);

    void credit(@Param("accountId") String accountId, @Param("amount") double amount);
}
