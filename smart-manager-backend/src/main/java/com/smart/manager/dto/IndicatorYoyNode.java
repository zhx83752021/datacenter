package com.smart.manager.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 高级同环比数据序列节点
 */
@Data
public class IndicatorYoyNode {
    private String period; // 时间刻度：如 2025-02
    private BigDecimal value; // 动态值或当月值
    private BigDecimal momRate; // 环比增长百分比（比上月）
    private BigDecimal yoyRate; // 同比增长百分比（比去年同月）
    private BigDecimal dispersion; // 偏离基准百分比（相比本次分析所有月份平均值的偏离度%）
}
