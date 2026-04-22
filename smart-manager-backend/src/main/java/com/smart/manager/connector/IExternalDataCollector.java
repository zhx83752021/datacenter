package com.smart.manager.connector;

import java.util.Map;

/**
 * 外部数据采集器接口
 * 定义了从 HIS/EMR 等第三方系统获取数据的标准
 */
public interface IExternalDataCollector {

    /**
     * 获取数据源名称 (HIS/EMR/LIS等)
     */
    String getSourceType();

    /**
     * 采集指定时间范围或周期的数据
     *
     * @param apiCode    接口代码 (如 HIS-01, HIS-02)
     * @param periodDate 统计周期 (yyyy-MM-dd 或 yyyy-MM)
     * @param params     其他过滤参数 (如 deptCode)
     * @return 采集到的结构化数据 Map
     */
    Map<String, Object> fetchRawData(String apiCode, String periodDate, Map<String, Object> params);
}
