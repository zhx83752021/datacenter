package com.smart.manager.connector.his;

import cn.hutool.core.util.RandomUtil;
import com.smart.manager.connector.IExternalDataCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HIS 系统数据仿真适配器 (Mock 实现)
 * 严格按照 01_HIS系统数据需求清单.md 的 JSON 规格生成数据
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "smart-manager.etl.source.his", havingValue = "mock", matchIfMissing = true)
public class HisMockAdapter implements IExternalDataCollector {

    @Override
    public String getSourceType() {
        return "HIS";
    }

    @Override
    public Map<String, Object> fetchRawData(String apiCode, String periodDate, Map<String, Object> params) {
        log.info("HIS仿真层：提取接口 [{}]，周期: {}", apiCode, periodDate);
        Map<String, Object> result = new HashMap<>();

        // 模拟网络延迟
        try {
            Thread.sleep(RandomUtil.randomInt(100, 500));
        } catch (InterruptedException ignored) {
        }

        switch (apiCode) {
            case "HIS-01": // 门诊挂号统计
                result.put("totalVisits", RandomUtil.randomInt(1000, 2000));
                result.put("emergencyVisits", RandomUtil.randomInt(200, 500));
                result.put("normalVisits", RandomUtil.randomInt(800, 1500));
                break;

            case "HIS-02": // 入出院统计
                result.put("admissions", RandomUtil.randomInt(50, 150));
                result.put("discharges", RandomUtil.randomInt(40, 130));
                result.put("currentInpatients", RandomUtil.randomInt(800, 1200));

                List<Map<String, Object>> transfers = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    Map<String, Object> transfer = new HashMap<>();
                    transfer.put("patientId", "P" + RandomUtil.randomNumbers(6));
                    transfer.put("isWithin48h", RandomUtil.randomBoolean());
                    transfers.add(transfer);
                }
                result.put("transfers", transfers);
                break;

            case "HIS-03": // 床位运营统计
                result.put("authorizedBeds", 1500);
                result.put("openBeds", 1450);
                result.put("occupiedBedDays", RandomUtil.randomInt(30000, 40000));
                result.put("openBedDays", 1450 * 30);
                result.put("dischargeCount", RandomUtil.randomInt(1000, 2000));
                break;

            case "HIS-04": // 收入汇总
                result.put("totalRevenue", randomVal(500, 800));
                result.put("medicalRevenue", randomVal(450, 750));
                result.put("drugRevenue", randomVal(150, 250));
                result.put("materialRevenue", randomVal(80, 150));
                result.put("outpatientRevenue", randomVal(200, 300));
                result.put("inpatientRevenue", randomVal(300, 500));
                result.put("otherRevenue", randomVal(10, 50));
                break;

            case "HIS-05": // 医嘱执行记录
                result.put("totalLongTermOrders", RandomUtil.randomInt(5000, 8000));
                result.put("sameDayCancelledOrders", RandomUtil.randomInt(50, 200));
                break;

            case "HIS-06": // 手术分级与交接班
                result.put("totalSurgeries", RandomUtil.randomInt(1000, 1500));
                result.put("nonPlannedSurgeries", RandomUtil.randomInt(5, 20));
                result.put("gradeIVSurgeries", RandomUtil.randomInt(100, 300));
                result.put("gradeIVWithBedsideHandover", RandomUtil.randomInt(80, 280));
                break;

            default:
                log.warn("未知 HIS 接口代码: {}", apiCode);
        }

        return result;
    }

    private BigDecimal randomVal(double min, double max) {
        // 以万元为单位模拟
        return BigDecimal.valueOf(RandomUtil.randomDouble(min, max)).setScale(2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("10000"));
    }
}
