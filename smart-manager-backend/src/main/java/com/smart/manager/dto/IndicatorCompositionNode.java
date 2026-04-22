package com.smart.manager.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 构成分析/指标树节点 DTO
 */
@Data
public class IndicatorCompositionNode {
    private String id;
    private String code;
    private String name;
    private BigDecimal value;
    private String formula;
    private Boolean isComposite;
    private List<IndicatorCompositionNode> children;
}
