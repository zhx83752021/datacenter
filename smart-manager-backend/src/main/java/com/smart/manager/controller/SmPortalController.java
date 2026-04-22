package com.smart.manager.controller;

import com.smart.manager.common.Result;
import com.smart.manager.entity.SmAlertMessage;
import com.smart.manager.service.ISmAlertMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 门户首页接口
 */
@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
public class SmPortalController {

    private final ISmAlertMessageService alertMessageService;

    /**
     * 获取数据概览统计
     */
    @GetMapping("/stats")
    public Result<List<Map<String, Object>>> getStats() {
        List<Map<String, Object>> stats = new ArrayList<>();

        stats.add(createStat("门诊人次", "12,840", "up", "12%"));
        stats.add(createStat("住院人次", "1,240", "up", "5%"));
        stats.add(createStat("平均住院日", "7.2", "down", "2%"));
        stats.add(createStat("床位使用率", "92.5%", "up", "1%"));

        return Result.success(stats);
    }

    /**
     * 获取核心看板列表
     */
    @GetMapping("/dashboards")
    public Result<List<Map<String, Object>>> getDashboards() {
        List<Map<String, Object>> list = new ArrayList<>();

        list.add(createDashboard(1L, "院长驾驶舱", "全院核心KPI实时监控与决策支持", "DataAnalysis", "/cockpit", "#0dbda8"));
        list.add(createDashboard(2L, "全景运营监控", "多维度运营指标全景态势分析", "TrendCharts", "/monitor", "#4FC3F7"));
        list.add(createDashboard(3L, "重点效率监控", "效率类核心指标深度穿透监控", "Operation", "/analysis/theme", "#FFB84D"));

        return Result.success(list);
    }

    /**
     * 获取预警消息列表
     */
    @GetMapping("/alerts")
    public Result<List<Map<String, Object>>> getAlerts() {
        // 先从数据库查最近的，没有则返回 mock
        List<SmAlertMessage> messages = alertMessageService.list();
        List<Map<String, Object>> list = new ArrayList<>();

        if (messages.isEmpty()) {
            list.add(createAlert(1L, "critical", "药占比严重异常", "全院药占比已连续3日超过35%阈值", "10分钟前"));
            list.add(createAlert(2L, "warning", "平均住院日偏高", "外科平均住院日环比上升15%", "2小时前"));
            list.add(createAlert(3L, "info", "系统数据更新完成", "2024年02月月度运营数据已加载", "1天前"));
        } else {
            for (SmAlertMessage msg : messages) {
                list.add(createAlert(msg.getId(),
                        msg.getLevel() == 3 ? "critical" : (msg.getLevel() == 2 ? "warning" : "info"),
                        "系统自动预警",
                        msg.getContent(),
                        "刚刚"));
            }
        }

        return Result.success(list);
    }

    private Map<String, Object> createStat(String label, String value, String trend, String trendValue) {
        Map<String, Object> map = new HashMap<>();
        map.put("label", label);
        map.put("value", value);
        map.put("trend", trend);
        map.put("trendValue", trendValue);
        return map;
    }

    private Map<String, Object> createDashboard(Long id, String title, String desc, String icon, String path,
            String color) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("desc", desc);
        map.put("icon", icon);
        map.put("path", path);
        map.put("color", color);
        return map;
    }

    private Map<String, Object> createAlert(Long id, String level, String title, String desc, String time) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("level", level);
        map.put("title", title);
        map.put("desc", desc);
        map.put("time", time);
        return map;
    }
}
