package com.yifeng.bank.b.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class SessionFactoryUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SessionFactoryUtils.class);

    private static SqlSessionFactory factory;

    static {
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        try (InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml")) {
            factory = builder.build(inputStream);
        } catch (IOException e) {
            LOG.error("error in creating sql session factory ", e);
        }
    }

    public static SqlSessionFactory getSessionFactory() {
        return factory; // initialized in static block
    }

    public static SqlSession getSession() {
        return factory.openSession();
    }

}
