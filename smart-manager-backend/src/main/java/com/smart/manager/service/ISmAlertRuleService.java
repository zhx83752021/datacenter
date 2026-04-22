package com.smart.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.manager.entity.SmAlertRule;
import java.util.List;

public interface ISmAlertRuleService extends IService<SmAlertRule> {
    /**
     * 获取指定指标的启用规则
     */
    List<SmAlertRule> getRulesByIndicator(Long indicatorId);
}
