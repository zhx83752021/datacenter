package com.smart.manager.controller;

import com.smart.manager.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 移动端门户 API
 */
@RestController
@RequestMapping("/api/sm/mobile/portal")
@RequiredArgsConstructor
public class SmMobilePortalController {

    /**
     * 获取移动端首页卡片
     */
    @GetMapping("/cards")
    public Result<List<Map<String, Object>>> getPortalCards() {
        List<Map<String, Object>> cards = new ArrayList<>();

        cards.add(createCard("今日收入", "345.8万", "↑ 12%", "#3b82f6"));
        cards.add(createCard("门诊人次", "1,204", "↓ 3%", "#10b981"));
        cards.add(createCard("在院人数", "856", "↑ 5%", "#8b5cf6"));
        cards.add(createCard("手术量", "42", "↑ 8%", "#f59e0b"));

        return Result.success(cards);
    }

    /**
     * 获取移动端快捷入口
     */
    @GetMapping("/shortcuts")
    public Result<List<Map<String, Object>>> getShortcuts() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(createShortcut("运营监控", "Monitor", "#3b82f6"));
        list.add(createShortcut("预警消息", "Bell", "#ef4444"));
        list.add(createShortcut("科室排名", "TrendCharts", "#10b981"));
        list.add(createShortcut("我的关注", "Star", "#f59e0b"));
        return Result.success(list);
    }

    private Map<String, Object> createCard(String title, String value, String trend, String color) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("value", value);
        map.put("trend", trend);
        map.put("color", color);
        return map;
    }

    private Map<String, Object> createShortcut(String name, String icon, String color) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("icon", icon);
        map.put("color", color);
        return map;
    }
}
