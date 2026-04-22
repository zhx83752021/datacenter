package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.manager.annotation.DataScope;
import com.smart.manager.entity.SmIndicatorValue;
import com.smart.manager.mapper.SmIndicatorValueMapper;
import com.smart.manager.service.ISmIndicatorValueService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SmIndicatorValueServiceImpl extends ServiceImpl<SmIndicatorValueMapper, SmIndicatorValue>
        implements ISmIndicatorValueService {

    @Override
    public List<SmIndicatorValue> queryTrend(Long indicatorId, String deptCode, String startPeriod, String endPeriod) {
        LambdaQueryWrapper<SmIndicatorValue> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(SmIndicatorValue::getIndicatorId, indicatorId);

        if (StringUtils.hasText(deptCode)) {
            wrapper.eq(SmIndicatorValue::getDeptCode, deptCode);
        }

        if (StringUtils.hasText(startPeriod)) {
            wrapper.ge(SmIndicatorValue::getPeriodDate, startPeriod);
        }
        if (StringUtils.hasText(endPeriod)) {
            wrapper.le(SmIndicatorValue::getPeriodDate, endPeriod);
        }

        wrapper.orderByAsc(SmIndicatorValue::getPeriodDate);
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<SmIndicatorValue> selectValueList(SmIndicatorValue val) {
        return baseMapper.selectValueList(val);
    }
}
