package com.smart.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.manager.entity.SmIndicatorValue;
import java.util.List;

public interface ISmIndicatorValueService extends IService<SmIndicatorValue> {
    /**
     * 查询指标趋势
     */
    List<SmIndicatorValue> queryTrend(Long indicatorId, String deptCode, String startPeriod, String endPeriod);

    List<SmIndicatorValue> selectValueList(SmIndicatorValue val);
}
