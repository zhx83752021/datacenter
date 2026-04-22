package com.smart.manager.controller;

import com.smart.manager.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.smart.manager.annotation.DataScope;
import com.smart.manager.entity.SmIndicatorValue;

import java.util.*;

/**
 * 运营主题分析控制器
 */
@RestController
@RequestMapping("/api/sm/analysis")
@RequiredArgsConstructor
public class SmAnalysisController {

        /**
         * 获取手术效率分析数据
         */
        @DataScope(deptAlias = "d")
        @GetMapping("/surgical-efficiency")
        public Result<Map<String, Object>> getSurgicalEfficiency(SmIndicatorValue queryParams) {
                Map<String, Object> data = new HashMap<>();

                // 趋势数据
                data.put("trend", Map.of(
                                "xAxis", Arrays.asList("W1", "W2", "W3", "W4"),
                                "current", Arrays.asList(120, 132, 101, 134),
                                "previous", Arrays.asList(110, 120, 95, 125)));

                // 构成数据
                data.put("composition", Arrays.asList(
                                Map.of("value", 1048, "name", "三级手术"),
                                Map.of("value", 735, "name", "四级手术"),
                                Map.of("value", 580, "name", "微创手术"),
                                Map.of("value", 484, "name", "其他")));

                // 排名数据
                data.put("ranking", Map.of(
                                "yAxis", Arrays.asList("神经外科", "胸外科", "普外科", "骨科", "泌尿外科"),
                                "values", Arrays.asList(320, 302, 301, 334, 390)));

                // 目标达成
                data.put("targetRate", 85);

                // AI 洞察
                data.put("insight", "本月手术效率总体呈上升趋势，环比增长 12.5%。其中神经外科表现最为突出。建议关注手术间周转时间。");

                return Result.success(data);
        }

        /**
         * 获取医疗收入分析数据
         */
        @DataScope(deptAlias = "d")
        @GetMapping("/income-analysis")
        public Result<Map<String, Object>> getIncomeAnalysis(SmIndicatorValue queryParams) {
                Map<String, Object> data = new HashMap<>();

                data.put("trend", Map.of(
                                "xAxis", Arrays.asList("W1", "W2", "W3", "W4"),
                                "current", Arrays.asList(450, 480, 420, 510),
                                "previous", Arrays.asList(400, 410, 430, 440)));

                data.put("composition", Arrays.asList(
                                Map.of("value", 2100, "name", "药品收入"),
                                Map.of("value", 1500, "name", "检查收入"),
                                Map.of("value", 1200, "name", "手术收入"),
                                Map.of("value", 800, "name", "耗材收入")));

                data.put("ranking", Map.of(
                                "yAxis", Arrays.asList("心内科", "骨科", "妇产科", "儿科", "消化科"),
                                "values", Arrays.asList(850, 720, 680, 540, 490)));

                data.put("targetRate", 92);
                data.put("insight", "本月医疗收入稳步增长，检查收入占比略有下降。药品加成控制良好，符合预期。");

                return Result.success(data);
        }

        /**
         * 获取医疗质量监控数据
         */
        @DataScope(deptAlias = "d")
        @GetMapping("/quality-monitor")
        public Result<Map<String, Object>> getQualityMonitor(SmIndicatorValue queryParams) {
                Map<String, Object> data = new HashMap<>();

                data.put("trend", Map.of(
                                "xAxis", Arrays.asList("W1", "W2", "W3", "W4"),
                                "current", Arrays.asList(98.5, 99.1, 98.8, 99.4),
                                "previous", Arrays.asList(97.2, 97.5, 98.1, 98.4)));

                data.put("composition", Arrays.asList(
                                Map.of("value", 40, "name", "优"),
                                Map.of("value", 30, "name", "良"),
                                Map.of("value", 20, "name", "中"),
                                Map.of("value", 10, "name", "一般")));

                data.put("ranking", Map.of(
                                "yAxis", Arrays.asList("呼吸科", "肾内科", "肿瘤科", "内分泌", "血液科"),
                                "values", Arrays.asList(99, 98, 97, 96, 95)));

                data.put("targetRate", 99);
                data.put("insight", "全院核心质控指标达标率为 99.2%，院感发生率维持在较低水平。需持续关注三级质控反馈。");

                return Result.success(data);
        }
}
