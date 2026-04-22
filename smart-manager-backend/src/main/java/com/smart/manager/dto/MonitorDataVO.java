package com.smart.manager.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MonitorDataVO implements Serializable {
    private Long indicatorId;
    private String indicatorName;
    private String indicatorCode;
    private String unit;

    private String periodDate;
    private BigDecimal value;

    // 同比增幅
    private BigDecimal yoyRate;
    // 环比增幅
    private BigDecimal momRate;

    // 目标值
    private BigDecimal targetValue;
    // 预警状态 (0:正常, 1:预警, 2:超标)
    private Integer alertStatus;
}
