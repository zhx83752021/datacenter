package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.manager.entity.SmAlertRule;
import com.smart.manager.mapper.SmAlertRuleMapper;
import com.smart.manager.service.ISmAlertRuleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmAlertRuleServiceImpl extends ServiceImpl<SmAlertRuleMapper, SmAlertRule> implements ISmAlertRuleService {

    @Override
    public List<SmAlertRule> getRulesByIndicator(Long indicatorId) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<SmAlertRule>()
                .eq(SmAlertRule::getIndicatorId, indicatorId)
                .eq(SmAlertRule::getStatus, 1));
    }
}
