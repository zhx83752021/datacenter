package com.smart.manager.connector.emr;

import cn.hutool.core.util.RandomUtil;
import com.smart.manager.connector.IExternalDataCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * EMR (电子病历) 系统数据仿真适配器
 * 严格按照 02_EMR系统数据需求清单.md 的 JSON 规格生成医疗质量监测数据
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "smart-manager.etl.source.emr", havingValue = "mock", matchIfMissing = true)
public class EmrMockAdapter implements IExternalDataCollector {

    @Override
    public String getSourceType() {
        return "EMR";
    }

    @Override
    public Map<String, Object> fetchRawData(String apiCode, String periodDate, Map<String, Object> params) {
        log.info("EMR仿真层：提取接口 [{}]，周期: {}", apiCode, periodDate);
        Map<String, Object> result = new HashMap<>();

        // 模拟网络延迟
        try {
            Thread.sleep(RandomUtil.randomInt(100, 300));
        } catch (InterruptedException ignored) {
        }

        switch (apiCode) {
            case "EMR-01": // 查房记录统计
                int admissions = RandomUtil.randomInt(500, 1000);
                result.put("totalAdmissions", admissions);
                result.put("roundedWithin8h", (int) (admissions * RandomUtil.randomDouble(0.85, 0.98)));
                result.put("seniorDoctorRoundRecords", (int) (admissions * RandomUtil.randomDouble(0.9, 1.0)));
                result.put("standardRecords", (int) (admissions * RandomUtil.randomDouble(0.8, 0.95)));
                break;

            case "EMR-02": // 会诊记录统计
                // 急会诊
                result.put("emergency_total", RandomUtil.randomInt(50, 100));
                result.put("emergency_arrivedIn10min", RandomUtil.randomInt(40, 50));
                result.put("emergency_orderIn40min", RandomUtil.randomInt(45, 50));
                // 普通会诊
                result.put("normal_total", RandomUtil.randomInt(200, 400));
                result.put("normal_completedIn24h", RandomUtil.randomInt(180, 200));
                result.put("normal_orderIn24h", RandomUtil.randomInt(170, 200));
                break;

            case "EMR-03": // 疑难病例讨论记录
                int unplannedReadmissions = RandomUtil.randomInt(20, 50);
                result.put("unplannedReadmissions", unplannedReadmissions);
                result.put("discussedCases", (int) (unplannedReadmissions * 0.9));
                result.put("recordCompleteCount", (int) (unplannedReadmissions * 0.85));
                int highCostPatients = RandomUtil.randomInt(10, 30);
                result.put("highCostPatients", highCostPatients);
                result.put("highCostDiscussed", (int) (highCostPatients * 0.8));
                break;

            case "EMR-04": // 急危重症抢救记录
                int totalRescues = RandomUtil.randomInt(30, 80);
                result.put("totalRescues", totalRescues);
                result.put("successfulRescues", (int) (totalRescues * RandomUtil.randomDouble(0.88, 0.95)));
                break;

            case "EMR-05": // 死亡病例讨论记录
                int totalDeaths = RandomUtil.randomInt(5, 20);
                result.put("totalDeaths", totalDeaths);
                result.put("discussedIn5Days", (int) (totalDeaths * 0.95));
                result.put("hostedByDirector", (int) (totalDeaths * 0.9));
                result.put("uploadedRecords", (int) (totalDeaths * 1.0));
                result.put("adminOrgDiscussions", RandomUtil.randomInt(1, 5));
                result.put("disputeDeaths", RandomUtil.randomInt(1, 3));
                break;

            case "EMR-06": // 护理与交接班补充
                int surDischarge = RandomUtil.randomInt(200, 500);
                result.put("surgeryDischarges", surDischarge);
                result.put("specialNursingCount", (int) (surDischarge * RandomUtil.randomDouble(0.1, 0.2)));
                break;

            default:
                log.warn("未知 EMR 接口代码: {}", apiCode);
        }

        return result;
    }
}
