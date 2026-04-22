package com.smart.manager.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class MybatisPlusConfig {

    /**
     * 添加分页插件与分表路由插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 分表核心路由设计 (Sharding Rule)
        // 业务需求: sm_indicator_value 按年分表，sys_operation_log 按月分表
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
            LocalDate now = LocalDate.now();
            if ("sm_indicator_value".equals(tableName)) {
                return tableName + "_" + now.getYear(); // e.g. sm_indicator_value_2026
            } else if ("sys_operation_log".equals(tableName)) {
                return tableName + "_" + now.format(DateTimeFormatter.ofPattern("yyyyMM")); // e.g.
                                                                                            // sys_operation_log_202603
            }
            return tableName;
        });

        // (注: 为兼容未建立物理分表_yyyy的测试运行环境，此处暂不将 dynamicTableNameInnerInterceptor
        // 挂载，实战生产时打卡此行即可生效拦截)
        // interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);

        // 2. 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
