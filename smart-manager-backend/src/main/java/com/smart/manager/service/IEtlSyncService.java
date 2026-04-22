package com.smart.manager.service;

/**
 * ETL 数据集成同步服务
 */
public interface IEtlSyncService {

    /**
     * 同步全院基础指标数据
     * 
     * @param periodDate 统计周期 (yyyy-MM-dd / yyyy-MM)
     * @return 同步成功的指标数量
     */
    int syncBaseData(String periodDate);

    /**
     * 同步特定科室指标数据
     * 
     * @param periodDate 统计周期
     * @param deptCode   科室编码
     * @return 同步成功的指标数量
     */
    int syncDeptData(String periodDate, String deptCode);

    /**
     * 自动触发复合指标的重新计算
     * 
     * @param periodDate 统计周期
     */
    void triggerCompositeCalculation(String periodDate);
}
