package com.smart.manager.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

/**
 * Railway 网页 SQL 常无法一次执行长脚本，导致缺 sm_indicator_lib 等表。
 * 在首次连接到的库（与 jdbc url 中 DB_NAME 一致）自动 CREATE TABLE IF NOT EXISTS。
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class EnsureMonitorTablesRunner implements CommandLineRunner {

    private final DataSource dataSource;

    @Override
    public void run(String... args) {
        ClassPathResource resource = new ClassPathResource("sql/monitor_tables_if_not_exists.sql");
        if (!resource.exists()) {
            log.warn("classpath:sql/monitor_tables_if_not_exists.sql 不存在，跳过监控表自检");
            return;
        }
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new EncodedResource(resource, StandardCharsets.UTF_8));
            log.info("监控链业务表已自检（CREATE TABLE IF NOT EXISTS）");
        } catch (Exception e) {
            log.error("执行监控链建表脚本失败: {}", e.getMessage());
        }
    }
}
