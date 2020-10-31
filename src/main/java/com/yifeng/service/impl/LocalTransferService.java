package com.yifeng.service.impl;

import com.yifeng.dao.AccountDao;
import com.yifeng.pojo.Account;
import com.yifeng.service.ITransferService;
import com.yifeng.util.SessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by guoyifeng on 10/30/20
 */
@Service
public class LocalTransferService implements ITransferService {

    private SqlSession session = SessionFactoryUtils.getSession();

    private AccountDao mapper = session.getMapper(AccountDao.class);

    private static final Logger LOG = LoggerFactory.getLogger(LocalTransferService.class);

    @Override
    public List<Account> findAll() {
        return mapper.findAll();
    }

    @Override
    public boolean transfer(String from, String to, double value) {
        try {
            mapper.payout(from, value);
//            int i = 1 / 0;
            mapper.credit(to, value);
        } catch (Exception e) {
            session.rollback();
            LOG.error("error in transferring transaction, has been rolled back", e.getMessage());
            return false;
        }

        session.commit();
        return true;
    }
}
