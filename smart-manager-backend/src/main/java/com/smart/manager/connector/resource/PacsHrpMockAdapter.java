package com.smart.manager.connector.resource;

import cn.hutool.core.util.RandomUtil;
import com.smart.manager.connector.IExternalDataCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * PACS (影像系统) 与 HRP (资源规划系统) 仿真适配器
 * 严格按照 05_PACS与HRP系统需求清单.md 的规格生成业财融合与资源监测数据
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "smart-manager.etl.source.resource", havingValue = "mock", matchIfMissing = true)
public class PacsHrpMockAdapter implements IExternalDataCollector {

    @Override
    public String getSourceType() {
        return "RESOURCE"; // 业财与资源聚合数据源
    }

    @Override
    public Map<String, Object> fetchRawData(String apiCode, String periodDate, Map<String, Object> params) {
        log.info("资源仿真层：提取接口 [{}]，周期: {}", apiCode, periodDate);
        Map<String, Object> result = new HashMap<>();

        // 模拟网络延迟
        try {
            Thread.sleep(RandomUtil.randomInt(200, 500));
        } catch (InterruptedException ignored) {
        }

        if ("PACS-01".equals(apiCode)) {
            handlePacs(result);
        } else if ("HRP-01".equals(apiCode)) {
            handleHrp(result);
        } else {
            log.warn("未知资源类接口代码: {}", apiCode);
        }

        return result;
    }

    private void handlePacs(Map<String, Object> result) {
        int mri = RandomUtil.randomInt(500, 1000);
        int ct = RandomUtil.randomInt(2000, 4000);
        int ultrasound = RandomUtil.randomInt(3000, 5000);
        int dr = RandomUtil.randomInt(4000, 6000);

        result.put("totalExams", mri + ct + ultrasound + dr);
        result.put("mriExams", mri);
        result.put("ctExams", ct);
        result.put("ultrasoundExams", ultrasound);
        result.put("drExams", dr);
        result.put("avgWaitTimeMinutes", RandomUtil.randomDouble(15.0, 45.0));
        result.put("avgReportTimeMinutes", RandomUtil.randomDouble(30.0, 120.0));
        result.put("positiveRate", RandomUtil.randomDouble(20.0, 45.0));
    }

    private void handleHrp(Map<String, Object> result) {
        double totalCost = RandomUtil.randomDouble(5000000, 10000000);
        result.put("totalCost", totalCost);
        result.put("hrCost", totalCost * 0.35); // 人力成本占比约 35%
        result.put("materialCost", totalCost * 0.45); // 耗材占比约 45%
        result.put("fixedAssetDepreciation", totalCost * 0.15); // 折旧约 15%

        result.put("staffCount_doctors", RandomUtil.randomInt(300, 500));
        result.put("staffCount_nurses", RandomUtil.randomInt(800, 1200));
        result.put("staffCount_technicians", RandomUtil.randomInt(100, 200));
        result.put("bedToNurseRatio", RandomUtil.randomDouble(2.0, 2.5)); // 假设配置
        result.put("bedToDoctorRatio", RandomUtil.randomDouble(4.0, 6.0));
    }
}
