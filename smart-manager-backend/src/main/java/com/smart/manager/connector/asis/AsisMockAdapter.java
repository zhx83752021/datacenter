package com.smart.manager.connector.asis;

import cn.hutool.core.util.RandomUtil;
import com.smart.manager.connector.IExternalDataCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * ASIS (手术麻醉) 系统数据仿真适配器
 * 严格按照 03_手术麻醉系统数据需求清单.md 的 JSON 规格生成手术质控数据
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "smart-manager.etl.source.asis", havingValue = "mock", matchIfMissing = true)
public class AsisMockAdapter implements IExternalDataCollector {

    @Override
    public String getSourceType() {
        return "ASIS";
    }

    @Override
    public Map<String, Object> fetchRawData(String apiCode, String periodDate, Map<String, Object> params) {
        log.info("ASIS仿真层：提取接口 [{}]，周期: {}", apiCode, periodDate);
        Map<String, Object> result = new HashMap<>();

        // 模拟网络延迟
        try {
            Thread.sleep(RandomUtil.randomInt(150, 400));
        } catch (InterruptedException ignored) {
        }

        switch (apiCode) {
            case "OPE-01": // 术前讨论统计
                int totalSurgeries = RandomUtil.randomInt(800, 1200);
                int electiveSurgeries = (int) (totalSurgeries * 0.7);
                int preopDiscussionCompleted = (int) (electiveSurgeries * 0.95);

                result.put("totalSurgeries", totalSurgeries);
                result.put("electiveSurgeries", electiveSurgeries);
                result.put("preopDiscussionCompleted", preopDiscussionCompleted);
                result.put("surgeonAttended", (int) (preopDiscussionCompleted * 0.98));
                result.put("plannedProcedureMatch", (int) (totalSurgeries * 0.92));
                result.put("plannedSurgeonMatch", (int) (totalSurgeries * 0.96));
                break;

            case "OPE-02": // 手术重合检查
                result.put("totalSurgeries", RandomUtil.randomInt(800, 1200));
                result.put("surgeonOverlaps", RandomUtil.randomInt(0, 5));
                result.put("anesthesiologistOverlaps", RandomUtil.randomInt(0, 3));
                break;

            case "OPE-03": // 手术分级与并发症
                int gradeIV = RandomUtil.randomInt(100, 200);
                int gradeIII = RandomUtil.randomInt(300, 500);
                result.put("gradeIVSurgeries", gradeIV);
                result.put("gradeIIISurgeries", gradeIII);
                result.put("gradeIVComplicationCount", RandomUtil.randomInt(2, 8));
                result.put("gradeIIIComplicationCount", RandomUtil.randomInt(5, 15));
                result.put("gradeIVDeathCount", RandomUtil.randomInt(0, 2));
                result.put("gradeIIIDeathCount", RandomUtil.randomInt(0, 3));
                result.put("gradeIVMdtCompleted", (int) (gradeIV * 0.9));
                result.put("registeredProcedures", 200);
                result.put("actualProcedures", 160);
                break;

            default:
                log.warn("未知 ASIS 接口代码: {}", apiCode);
        }

        return result;
    }
}
