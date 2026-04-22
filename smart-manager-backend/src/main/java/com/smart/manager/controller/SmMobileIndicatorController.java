package com.smart.manager.controller;

import com.smart.manager.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 移动端指标监测 API
 */
@RestController
@RequestMapping("/api/sm/mobile/indicator")
@RequiredArgsConstructor
public class SmMobileIndicatorController {

    /**
     * 今日动态指标
     */
    @GetMapping("/today")
    public Result<Map<String, Object>> getTodayMetrics() {
        Map<String, Object> data = new HashMap<>();
        data.put("updateTime", "2026-02-27 14:00:00");

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(createMetric("门诊均次费用", "456.2", "元", 4.2));
        list.add(createMetric("平均住院日", "7.2", "天", -1.5));
        list.add(createMetric("药占比", "28.4%", "", -2.1));

        data.put("metrics", list);
        return Result.success(data);
    }

    /**
     * 移动端趋势分析
     */
    @GetMapping("/trend")
    public Result<Map<String, Object>> getTrend(@RequestParam String code) {
        Map<String, Object> res = new HashMap<>();
        res.put("xAxis", Arrays.asList("02-21", "02-22", "02-23", "02-24", "02-25", "02-26", "02-27"));
        res.put("values", Arrays.asList(120, 132, 101, 134, 90, 230, 210));
        return Result.success(res);
    }

    private Map<String, Object> createMetric(String name, String value, String unit, double mom) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("value", value);
        map.put("unit", unit);
        map.put("mom", mom);
        return map;
    }
}
