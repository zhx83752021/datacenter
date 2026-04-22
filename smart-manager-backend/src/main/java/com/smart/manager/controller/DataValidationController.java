package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SmIndicatorLib;
import com.smart.manager.entity.SmIndicatorValue;
import com.smart.manager.service.ISmIndicatorLibService;
import com.smart.manager.service.ISmIndicatorValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 指标数据对账验证控制器
 * 用于验证各系统（HIS/EMR/ASIS/LIS/PACS等）同步后的数据准确性
 */
@RestController
@RequestMapping("/api/test/validation")
@RequiredArgsConstructor
public class DataValidationController {

    private final ISmIndicatorLibService indicatorService;
    private final ISmIndicatorValueService valueService;

    /**
     * 获取指定日期的各源系统同步对账单
     */
    @GetMapping("/audit")
    public Result<List<Map<String, Object>>> audit(@RequestParam String periodDate) {
        List<Map<String, Object>> report = new ArrayList<>();

        // 关键核对指标清单（跨5大系统）
        String[] codes = {
                "EFF001", "ECO001", // HIS
                "QUA002_N", "QUA014_N", // EMR
                "QUA015_N", "QUA024_N", // ASIS
                "QUA031", "SVC001", // PERIPHERAL
                "PACS_EFF_01", "HRP_ECO_01", // RESOURCE
                "EFF_MIX_01", // COMPOSITE
                "QUA017", "QUA017_N", "QUA017_D", // SURGERY CONSISTENCY
                "QUA019", "QUA019_N", "QUA019_D", // DEATH 5D
                "QUA020", "QUA020_N", "QUA020_D", // DEATH RATIO
                "QUA021", "QUA021_N", "QUA021_D", // DEATH HOST
                "QUA022", "QUA022_N", "QUA022_D" // DEATH UPLOAD
        };

        for (String code : codes) {
            SmIndicatorLib lib = indicatorService
                    .getOne(new LambdaQueryWrapper<SmIndicatorLib>().eq(SmIndicatorLib::getCode, code));
            if (lib == null)
                continue;

            SmIndicatorValue val = valueService.getOne(new LambdaQueryWrapper<SmIndicatorValue>()
                    .eq(SmIndicatorValue::getIndicatorId, lib.getId())
                    .eq(SmIndicatorValue::getPeriodDate, periodDate)
                    .eq(SmIndicatorValue::getDeptCode, "ALL"));

            Map<String, Object> item = new HashMap<>();
            item.put("code", code);
            item.put("name", lib.getName());
            item.put("source", lib.getDataSource());
            item.put("value", val != null ? val.getValue() : "MISSING");
            item.put("status", val != null ? "SUCCESS" : "FAIL");
            report.add(item);
        }

        return Result.success(report);
    }
}
