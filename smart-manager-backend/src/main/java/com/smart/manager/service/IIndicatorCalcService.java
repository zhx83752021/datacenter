package com.smart.manager.service;

import com.smart.manager.entity.SmIndicatorLib;
import com.smart.manager.dto.IndicatorCompositionNode;
import com.smart.manager.dto.IndicatorYoyAnalysisDTO;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 指标计算服务接口
 */
public interface IIndicatorCalcService {

    /**
     * 计算复合指标值
     *
     * @param indicator  指标定义
     * @param periodDate 统计周期
     * @param deptCode   科室编码
     * @return 计算结果
     */
    BigDecimal calculate(SmIndicatorLib indicator, String periodDate, String deptCode);

    /**
     * 构建指标溯源拆解树
     *
     * @param indicatorCode 靶向指标编码
     * @param periodDate    统计周期
     * @param deptCode      科室编码
     * @return 构成分析节点树
     */
    IndicatorCompositionNode getCompositionTree(String indicatorCode, String periodDate, String deptCode);

    /**
     * 高级同环比（YOY / MOM）计算引擎：跨年12个月数据连拼并计算离散率。
     * 支持多基准期对比，结合基准库计算 YoY(年同比) 和 MoM(月环比)
     *
     * @param indicatorCode 目标分析的指标编号
     * @param endPeriod     分析终点自然月(如：2026-02)
     * @param deptCode      部门过滤
     * @param limitMonths   往前连拼的时长（默认12个月）
     * @return 包含月度截面、变异散布特征和基础信息的汇总DTO
     */
    IndicatorYoyAnalysisDTO getAdvancedYoyAnalysis(String indicatorCode, String endPeriod, String deptCode,
            int limitMonths);

    /**
     * 验证公式是否合法
     *
     * @param formula 计算公式
     * @return 是否合法
     */
    boolean validateFormula(String formula);

    /**
     * 获取指标在指定周期和科室下的值
     *
     * @param code       指标编码
     * @param periodDate 统计周期
     * @param deptCode   科室编码
     * @return 指标值，无数据时返回 ZERO
     */
    BigDecimal getIndicatorValue(String code, String periodDate, String deptCode);
}
