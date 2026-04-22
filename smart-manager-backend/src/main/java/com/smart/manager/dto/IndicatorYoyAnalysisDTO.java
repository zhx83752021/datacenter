package com.smart.manager.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 某指标高级同环比分析全周期汇总结果
 */
@Data
public class IndicatorYoyAnalysisDTO {
    private String indicatorCode;
    private String indicatorName;
    private BigDecimal averageValue; // 本分析跨度内的平均值
    private BigDecimal cvRate; // 分析跨度内的变异系数/整体离散率 (表示这几个月间的总体波动大小)
    private List<IndicatorYoyNode> trendBase; // 月度连拼明细数组
}
