package com.smart.manager.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

/**
 * Railway / 控制台粘贴用户名密码时易带入首尾空格，会导致 MySQL 报 Access denied for user ' root'。
 */
@Configuration
public class HikariTrimConfiguration implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof HikariDataSource ds) {
            String u = ds.getUsername();
            if (u != null) {
                ds.setUsername(u.trim());
            }
            String p = ds.getPassword();
            if (p != null) {
                ds.setPassword(p.trim());
            }
        }
        return bean;
    }
}
