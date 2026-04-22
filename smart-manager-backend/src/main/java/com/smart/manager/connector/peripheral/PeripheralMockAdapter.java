package com.smart.manager.connector.peripheral;

import cn.hutool.core.util.RandomUtil;
import com.smart.manager.connector.IExternalDataCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * LIS/NIS/PHA/BTS/SAT/MED 等外围多源辅助系统数据仿真适配器
 * 严格按照 04_LIS及外围系统数据需求清单.md 的规格生成数据
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "smart-manager.etl.source.peripheral", havingValue = "mock", matchIfMissing = true)
public class PeripheralMockAdapter implements IExternalDataCollector {

    @Override
    public String getSourceType() {
        return "PERIPHERAL"; // 聚合外围系统
    }

    @Override
    public Map<String, Object> fetchRawData(String apiCode, String periodDate, Map<String, Object> params) {
        log.info("外围仿真层：提取接口 [{}]，周期: {}", apiCode, periodDate);
        Map<String, Object> result = new HashMap<>();

        // 模拟网络延迟
        try {
            Thread.sleep(RandomUtil.randomInt(50, 200));
        } catch (InterruptedException ignored) {
        }

        if (apiCode.startsWith("LIS-")) {
            handleLis(apiCode, result);
        } else if (apiCode.startsWith("NIS-")) {
            handleNis(apiCode, result);
        } else if (apiCode.startsWith("PHA-")) {
            handlePha(apiCode, result);
        } else if (apiCode.startsWith("BTS-")) {
            handleBts(apiCode, result);
        } else if (apiCode.startsWith("SAT-")) {
            handleSat(apiCode, result);
        } else if (apiCode.startsWith("MED-")) {
            handleMed(apiCode, result);
        } else {
            log.warn("未知外围系统接口代码: {}", apiCode);
        }

        return result;
    }

    private void handleLis(String apiCode, Map<String, Object> result) {
        if ("LIS-01".equals(apiCode)) {
            result.put("totalCriticalValues", RandomUtil.randomInt(100, 300));
            int inpatientCriticalValues = RandomUtil.randomInt(80, 250);
            result.put("inpatientCriticalValues", inpatientCriticalValues);
            result.put("reportTimeMedianMinutes", RandomUtil.randomInt(5, 15));
            result.put("inpatientSameDayDisposed", (int) (inpatientCriticalValues * 0.98));
        }
    }

    private void handleNis(String apiCode, Map<String, Object> result) {
        if ("NIS-01".equals(apiCode)) {
            result.put("levelSpecialCount", RandomUtil.randomInt(50, 150));
            result.put("level1Count", RandomUtil.randomInt(300, 600));
            result.put("postOpLevelSpecialCount", RandomUtil.randomInt(40, 100));
        }
    }

    private void handlePha(String apiCode, Map<String, Object> result) {
        if ("PHA-01".equals(apiCode)) {
            int specialAntibioticOrders = RandomUtil.randomInt(50, 120);
            result.put("specialAntibioticOrders", specialAntibioticOrders);
            result.put("antibioticConsultationCompleted", (int) (specialAntibioticOrders * 0.95));
        }
    }

    private void handleBts(String apiCode, Map<String, Object> result) {
        if ("BTS-01".equals(apiCode)) {
            int totalTransfusions = RandomUtil.randomInt(100, 200);
            result.put("totalTransfusions", totalTransfusions);
            result.put("postTransfusionEvaluations", (int) (totalTransfusions * 0.92));
            result.put("intraopAutoTransfusions", RandomUtil.randomInt(10, 30));
        }
    }

    private void handleSat(String apiCode, Map<String, Object> result) {
        if ("SAT-01".equals(apiCode)) {
            result.put("satisfactionScore", RandomUtil.randomDouble(90.0, 98.0));
        }
    }

    private void handleMed(String apiCode, Map<String, Object> result) {
        if ("MED-01".equals(apiCode)) {
            int totalApproved = RandomUtil.randomInt(50, 100);
            result.put("totalApproved", totalApproved);
            result.put("stillActive", (int) (totalApproved * 0.85));
        }
    }
}
