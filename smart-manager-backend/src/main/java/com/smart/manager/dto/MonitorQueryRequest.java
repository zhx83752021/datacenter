package com.smart.manager.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class MonitorQueryRequest implements Serializable {
    /**
     * 关心的指标ID集合
     */
    private List<Long> indicatorIds;

    /**
     * 单个指标ID（用于趋势分析）
     */
    private Long indicatorId;

    /**
     * 部门编码（可选，空则为全院）
     */
    private String deptCode;

    /**
     * 具体的统计期间（如 2023-01）
     */
    private String period;

    /**
     * 开始期间（趋势分析用）
     */
    private String startPeriod;

    /**
     * 结束期间（趋势分析用）
     */
    private String endPeriod;
}
