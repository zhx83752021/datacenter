package com.smart.manager.controller;

import com.smart.manager.common.Result;
import com.smart.manager.service.IEtlSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ETL 数据同步控制器
 */
@RestController
@RequestMapping("/api/sm/etl")
@RequiredArgsConstructor
public class EtlSyncController {

    private final IEtlSyncService etlSyncService;
    private final com.smart.manager.scheduler.HisDataExtractionTask hisDataExtractionTask;

    /**
     * 手工触发：调试基于《01_HIS系统数据需求清单》标准的模拟数据排程拉取与入库
     */
    @PostMapping("/mock/triggerHis")
    public Result<String> triggerHisMockExtraction() {
        hisDataExtractionTask.extractOutpatientData();
        hisDataExtractionTask.extractInpatientData();
        return Result.success("HIS Mock 采集模拟任务已投递，请在控制台查看处理日志。");
    }

    /**
     * 手动触发数据同步
     *
     * 
     * @param periodDate 统计周期 (yyyy-MM)
     * @param deptCode   科室编码 (可选，不传则同步全院)
     */
    @PostMapping("/sync")
    public Result<String> syncData(@RequestParam String periodDate,
            @RequestParam(required = false) String deptCode) {
        int count;
        if (deptCode != null && !deptCode.isEmpty() && !"ALL".equals(deptCode)) {
            count = etlSyncService.syncDeptData(periodDate, deptCode);
        } else {
            count = etlSyncService.syncBaseData(periodDate);
        }
        return Result.success("同步成功，共处理 " + count + " 项基础指标。");
    }

    /**
     * 手动触发指定周期的复合指标重计算
     */
    @PostMapping("/recalculate")
    public Result<String> recalculate(@RequestParam String periodDate) {
        etlSyncService.triggerCompositeCalculation(periodDate);
        return Result.success("计算指令已下发，正在更新复合指标数据。");
    }
}
