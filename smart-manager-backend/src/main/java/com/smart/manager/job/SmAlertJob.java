package com.smart.manager.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.manager.entity.SmAlertMessage;
import com.smart.manager.entity.SmAlertRule;
import com.smart.manager.entity.SmFeedback;
import com.smart.manager.entity.SmIndicatorValue;
import com.smart.manager.service.ISmAlertMessageService;
import com.smart.manager.service.ISmAlertRuleService;
import com.smart.manager.service.ISmFeedbackService;
import com.smart.manager.service.ISmIndicatorValueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmAlertJob {

    private final ISmAlertRuleService ruleService;
    private final ISmIndicatorValueService valueService;
    private final ISmAlertMessageService messageService;
    private final ISmFeedbackService feedbackService;

    /**
     * 定时检测预警 (每小时执行一次)
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkAlerts() {
        log.info("开始执行指标预警检测任务...");

        // 1. 获取所有启用规则
        List<SmAlertRule> rules = ruleService.list(new LambdaQueryWrapper<SmAlertRule>().eq(SmAlertRule::getStatus, 1));
        if (rules.isEmpty()) {
            return;
        }

        // 2. 按指标分组获取最新值 (简化逻辑: 获取最近一条记录)
        // 更好的逻辑: 获取当前周期的值。这里简单取指标的最新值检测。
        // 为了避免全表扫描，最好在入库时触发。这里作为兜底或特定周期检测。

        // 遍历规则检测 (Not efficient but simple for now)
        for (SmAlertRule rule : rules) {
            try {
                checkRule(rule);
            } catch (Exception e) {
                log.error("规则检测失败: " + rule.getId(), e);
            }
        }

        log.info("预警检测任务结束");
    }

    private void checkRule(SmAlertRule rule) {
        // 获取该指标最新的一条数据
        // Let's use list with limit 1 order by time desc
        LambdaQueryWrapper<SmIndicatorValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmIndicatorValue::getIndicatorId, rule.getIndicatorId());
        wrapper.orderByDesc(SmIndicatorValue::getPeriodDate);
        wrapper.last("LIMIT 1");

        SmIndicatorValue latestValue = valueService.getOne(wrapper);
        if (latestValue == null) {
            return;
        }

        boolean triggered = false;
        BigDecimal val = latestValue.getValue();
        BigDecimal threshold = rule.getThreshold();

        if (val == null || threshold == null)
            return;

        switch (rule.getOperator()) {
            case "GT": // >
                triggered = val.compareTo(threshold) > 0;
                break;
            case "LT": // <
                triggered = val.compareTo(threshold) < 0;
                break;
            case "GE": // >=
                triggered = val.compareTo(threshold) >= 0;
                break;
            case "LE": // <=
                triggered = val.compareTo(threshold) <= 0;
                break;
            case "EQ": // ==
                triggered = val.compareTo(threshold) == 0;
                break;
        }

        if (triggered) {
            // Check if already alerted recently? (Skip for now)
            saveAlert(rule, val);
        }
    }

    private void saveAlert(SmAlertRule rule, BigDecimal val) {
        SmAlertMessage msg = new SmAlertMessage();
        msg.setIndicatorId(rule.getIndicatorId());
        msg.setRuleId(rule.getId());
        msg.setLevel(rule.getLevel());
        msg.setStatus(0); // Unread
        String content = "指标触发预警: 当前值 " + val + " " + getOpText(rule.getOperator()) + " 阈值 " + rule.getThreshold();
        msg.setContent(content);
        messageService.save(msg);

        // 自动生成系统级工单
        createSystemFeedback(rule, content);
    }

    private void createSystemFeedback(SmAlertRule rule, String content) {
        SmFeedback feedback = new SmFeedback();
        feedback.setIndicatorId(rule.getIndicatorId());
        feedback.setContent("【系统预警自动建单】" + content);
        feedback.setStatus(0); // 待处理
        feedback.setCreateBy("SYSTEM");
        feedback.setCreateTime(LocalDateTime.now());
        feedback.setUpdateTime(LocalDateTime.now());
        feedbackService.save(feedback);
    }

    private String getOpText(String op) {
        if ("GT".equals(op))
            return "大于";
        if ("LT".equals(op))
            return "小于";
        if ("GE".equals(op))
            return "大于等于";
        if ("LE".equals(op))
            return "小于等于";
        if ("EQ".equals(op))
            return "等于";
        return op;
    }
}
