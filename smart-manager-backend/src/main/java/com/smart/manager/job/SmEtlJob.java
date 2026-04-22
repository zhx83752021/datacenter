package com.smart.manager.job;

import com.smart.manager.service.IEtlSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 指标数据 ETL 定时任务调度中枢
 * 负责按照业务要求的不同频次 (日/月) 发起数据采集指令
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmEtlJob {

    private final IEtlSyncService etlSyncService;

    /**
     * 战役 1：建立每日采集器基准
     * 每日凌晨 02:00 执行，主要采集 HIS/EMR 的日流水业务指标 (EFF/RT)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailySync() {
        String yesterday = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        log.info(">>>> [调度心跳] 触发每日核心数据同步 (02:00). 目标日期: {} <<<<", yesterday);

        int count = etlSyncService.syncBaseData(yesterday);

        log.info(">>>> [调度心跳] 每日同步完成，影响原子指标项: {} <<<<", count);
    }

    /**
     * 战役 2：建立每月 1 号大采集基准
     * 每月 1 号凌晨 03:00 执行，采集 财务、运营 相关的月度聚合指标 (ECO/QUA)
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    public void monthlySync() {
        String lastMonth = LocalDateTime.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
        log.info(">>>> [调度心跳] 触发月度业务指标汇总 (1st 03:00). 统计月份: {} <<<<", lastMonth);

        int count = etlSyncService.syncBaseData(lastMonth);

        log.info(">>>> [调度心跳] 月度同步完成，影响原子指标项: {} <<<<", count);
    }
}
