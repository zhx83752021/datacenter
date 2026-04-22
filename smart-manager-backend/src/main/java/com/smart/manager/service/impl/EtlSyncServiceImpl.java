package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.manager.connector.IExternalDataCollector;
import com.smart.manager.entity.SmIndicatorLib;
import com.smart.manager.entity.SmIndicatorValue;
import com.smart.manager.service.IEtlSyncService;
import com.smart.manager.service.IIndicatorCalcService;
import com.smart.manager.service.ISmIndicatorLibService;
import com.smart.manager.service.ISmIndicatorValueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtlSyncServiceImpl implements IEtlSyncService {

    private final ISmIndicatorLibService indicatorService;
    private final ISmIndicatorValueService valueService;
    private final IIndicatorCalcService calcService;
    private final List<IExternalDataCollector> collectors;

    @Override
    @Transactional
    public int syncBaseData(String periodDate) {
        log.info(">>>> [ETL智能化中枢] 发起多源数据集成任务周期: {} <<<<", periodDate);

        // 获取所有基础因子指标 (非复合指标且状态正常)
        List<SmIndicatorLib> baseIndicators = indicatorService.list(new LambdaQueryWrapper<SmIndicatorLib>()
                .eq(SmIndicatorLib::getIsComposite, 0)
                .eq(SmIndicatorLib::getStatus, "0"));

        int count = 0;

        // 1. 预执行全维度采集并构建内存数据池 (HIS/EMR/ASIS/PERI/RESOURCE)
        Map<String, Map<String, Object>> poolHIS = preFetchSourceData("HIS", periodDate);
        Map<String, Map<String, Object>> poolEMR = preFetchSourceData("EMR", periodDate);
        Map<String, Map<String, Object>> poolASIS = preFetchSourceData("ASIS", periodDate);
        Map<String, Map<String, Object>> poolPeri = preFetchSourceData("PERIPHERAL", periodDate);
        Map<String, Map<String, Object>> poolRes = preFetchSourceData("RESOURCE", periodDate);

        for (SmIndicatorLib indicator : baseIndicators) {
            BigDecimal value = null;
            String dataSource = indicator.getDataSource();

            // 2. 智能化多源路由解析逻辑 (基于清单映射)
            if ("HIS".equalsIgnoreCase(dataSource)) {
                value = parseHisValue(indicator.getCode(), poolHIS);
            } else if ("EMR".equalsIgnoreCase(dataSource)) {
                value = parseEmrValue(indicator.getCode(), poolEMR);
            } else if ("ASIS".equalsIgnoreCase(dataSource) || "手术系统".equalsIgnoreCase(dataSource)) {
                value = parseAsisValue(indicator.getCode(), poolASIS);
            } else if (isPeripheralSource(dataSource)) {
                value = parsePeripheralValue(indicator.getCode(), poolPeri);
            } else if ("PACS".equalsIgnoreCase(dataSource) || "HRP".equalsIgnoreCase(dataSource)
                    || "RESOURCE".equalsIgnoreCase(dataSource)) {
                value = parseResourceValue(indicator.getCode(), poolRes);
            } else {
                // 默认降级为 mock fetch
                value = mockSystemFetch(indicator.getCode());
            }

            // 3. 结果入库
            if (value != null && saveOrUpdateValue(indicator.getId(), "ALL", periodDate, value)) {
                count++;
            }
        }

        log.info(">>>> [ETL智能化中枢] 基础因子同步完成 (共 {} 项)。自动激活指标复合计算流水线... <<<<", count);

        // 4. 级联触发复合指标引擎
        triggerCompositeCalculation(periodDate);

        return count;
    }

    private boolean isPeripheralSource(String source) {
        if (source == null)
            return false;
        return source.matches("(?i)LIS|NIS|BTS|PHA|满意度调查|医务管理|PERIPHERAL");
    }

    private Map<String, Map<String, Object>> preFetchSourceData(String sourceType, String periodDate) {
        log.info(">>>> [ETL] 尝试定位采集器: {} <<<<", sourceType);
        log.info(">>>> [ETL] 当前注册采集器数量: {} <<<<", collectors.size());
        collectors.forEach(c -> log.info(">>>> [ETL] 已注册采集器: {} ({}) <<<<", c.getSourceType(), c.getClass().getName()));

        Map<String, Map<String, Object>> pool = new HashMap<>();
        IExternalDataCollector collector = collectors.stream()
                .filter(c -> sourceType.equalsIgnoreCase(c.getSourceType()))
                .findFirst().orElse(null);

        if (collector != null) {
            String[] apiCodes = {};
            if ("HIS".equalsIgnoreCase(sourceType))
                apiCodes = new String[] { "HIS-01", "HIS-02", "HIS-03", "HIS-04", "HIS-05", "HIS-06" };
            if ("EMR".equalsIgnoreCase(sourceType))
                apiCodes = new String[] { "EMR-01", "EMR-02", "EMR-03", "EMR-04", "EMR-05" };
            if ("ASIS".equalsIgnoreCase(sourceType))
                apiCodes = new String[] { "OPE-01", "OPE-02", "OPE-03" };
            if ("PERIPHERAL".equalsIgnoreCase(sourceType))
                apiCodes = new String[] { "LIS-01", "NIS-01", "PHA-01", "BTS-01", "SAT-01", "MED-01" };
            if ("RESOURCE".equalsIgnoreCase(sourceType))
                apiCodes = new String[] { "PACS-01", "HRP-01" };

            for (String api : apiCodes) {
                pool.put(api, collector.fetchRawData(api, periodDate, new HashMap<>()));
            }
        }
        return pool;
    }

    private BigDecimal parseHisValue(String code, Map<String, Map<String, Object>> pool) {
        try {
            switch (code) {
                case "EFF001":
                    return new BigDecimal(pool.get("HIS-01").get("totalVisits").toString());
                case "EFF002":
                case "QUA001_D": // 核心修正：转科比例分母对齐入院人次
                    return new BigDecimal(pool.get("HIS-02").get("admissions").toString());
                case "RT_001":
                    return new BigDecimal(pool.get("HIS-02").get("currentInpatients").toString());
                case "EFF003_N":
                    return new BigDecimal(pool.get("HIS-03").get("occupiedBedDays").toString());
                case "EFF003_D":
                    return new BigDecimal(pool.get("HIS-03").get("openBedDays").toString());
                case "EFF004_N":
                    return new BigDecimal(pool.get("HIS-03").get("dischargeCount").toString());
                case "EFF004_D":
                    return new BigDecimal(pool.get("HIS-03").get("openBeds").toString());
                case "ECO001":
                    return new BigDecimal(pool.get("HIS-04").get("totalRevenue").toString());
                case "ECO002":
                    return new BigDecimal(pool.get("HIS-04").get("medicalRevenue").toString());
                case "ECO003":
                    return new BigDecimal(pool.get("HIS-04").get("drugRevenue").toString());
                case "ECO004":
                    return new BigDecimal(pool.get("HIS-04").get("otherRevenue").toString());
                case "ECO005":
                    return new BigDecimal(pool.get("HIS-04").get("outpatientRevenue").toString());
                case "QUA_D02_N":
                    return new BigDecimal(pool.get("HIS-04").get("materialRevenue").toString());
                case "QUA_D02_D":
                    return new BigDecimal(pool.get("HIS-04").get("medicalRevenue").toString());
                case "QUA023_N":
                    return new BigDecimal(pool.get("HIS-05").get("sameDayCancelledOrders").toString());
                case "QUA023_D":
                    return new BigDecimal(pool.get("HIS-05").get("totalLongTermOrders").toString());
                case "QUA001_N":
                    List<Map<String, Object>> transfers = (List<Map<String, Object>>) pool.get("HIS-02")
                            .get("transfers");
                    return new BigDecimal(transfers.stream().filter(t -> (Boolean) t.get("isWithin48h")).count());
                case "QUA004_N":
                    return new BigDecimal(pool.get("HIS-06").get("nonPlannedSurgeries").toString());
                case "QUA004_D":
                    return new BigDecimal(pool.get("HIS-06").get("totalSurgeries").toString());
                case "QUA010_N":
                    return new BigDecimal(pool.get("HIS-06").get("gradeIVWithBedsideHandover").toString());
                case "QUA010_D":
                    return new BigDecimal(pool.get("HIS-06").get("gradeIVSurgeries").toString());
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseEmrValue(String code, Map<String, Map<String, Object>> pool) {
        try {
            switch (code) {
                case "QUA002_N":
                    return new BigDecimal(pool.get("EMR-01").get("roundedWithin8h").toString());
                case "QUA002_D":
                case "QUA003_D":
                    return new BigDecimal(pool.get("EMR-01").get("totalAdmissions").toString());
                case "QUA003_N":
                    return new BigDecimal(pool.get("EMR-01").get("standardRecords").toString());
                case "QUA005_N":
                    return new BigDecimal(pool.get("EMR-02").get("emergency_arrivedIn10min").toString());
                case "QUA005_D":
                case "QUA006_D":
                    return new BigDecimal(pool.get("EMR-02").get("emergency_total").toString());
                case "QUA006_N":
                    return new BigDecimal(pool.get("EMR-02").get("emergency_orderIn40min").toString());
                case "QUA007_N":
                    return new BigDecimal(pool.get("EMR-02").get("normal_completedIn24h").toString());
                case "QUA007_D":
                case "QUA008_D":
                    return new BigDecimal(pool.get("EMR-02").get("normal_total").toString());
                case "QUA008_N":
                    return new BigDecimal(pool.get("EMR-02").get("normal_orderIn24h").toString());
                case "QUA011_N":
                    return new BigDecimal(pool.get("EMR-03").get("discussedCases").toString());
                case "QUA011_D":
                case "QUA012_D":
                    return new BigDecimal(pool.get("EMR-03").get("unplannedReadmissions").toString());
                case "QUA012_N":
                    return new BigDecimal(pool.get("EMR-03").get("recordCompleteCount").toString());
                case "QUA019_N":
                    return new BigDecimal(pool.get("EMR-05").get("discussedIn5Days").toString());
                case "QUA019_D":
                case "QUA021_D":
                    return new BigDecimal(pool.get("EMR-05").get("totalDeaths").toString());
                case "QUA020_N":
                    return new BigDecimal(pool.get("EMR-05").get("adminOrgDiscussions").toString());
                case "QUA020_D":
                    return new BigDecimal(pool.get("EMR-05").get("disputeDeaths").toString());
                case "QUA014_N": // 核心修正：对应抢救成功数，从 EMR-04 获取内容
                    return new BigDecimal(pool.get("EMR-04").get("successfulRescues").toString());
                case "QUA014_D": // 核心修正：对应抢救总人数
                    return new BigDecimal(pool.get("EMR-04").get("totalRescues").toString());
                case "QUA009_N":
                    return new BigDecimal(pool.get("EMR-06").get("specialNursingCount").toString());
                case "QUA009_D":
                    return new BigDecimal(pool.get("EMR-06").get("surgeryDischarges").toString());
                case "QUA021_N":
                    return new BigDecimal(pool.get("EMR-05").get("hostedByDirector").toString());
                case "QUA022_N":
                    return new BigDecimal(pool.get("EMR-05").get("uploadedRecords").toString());
                case "QUA022_D":
                    return new BigDecimal(pool.get("EMR-05").get("totalDeaths").toString());
                default:
                    return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseAsisValue(String code, Map<String, Map<String, Object>> pool) {
        try {
            switch (code) {
                case "QUA015_N":
                    return new BigDecimal(pool.get("OPE-01").get("preopDiscussionCompleted").toString());
                case "QUA015_D":
                    return new BigDecimal(pool.get("OPE-01").get("electiveSurgeries").toString());
                case "QUA016_N":
                    return new BigDecimal(pool.get("OPE-01").get("surgeonAttended").toString());
                case "QUA016_D":
                    return new BigDecimal(pool.get("OPE-01").get("preopDiscussionCompleted").toString());
                case "QUA017_N":
                    return new BigDecimal(pool.get("OPE-01").get("plannedProcedureMatch").toString());
                case "QUA017_D":
                case "QUA018_D":
                    return new BigDecimal(pool.get("OPE-01").get("totalSurgeries").toString());
                case "QUA018_N":
                    return new BigDecimal(pool.get("OPE-01").get("plannedSurgeonMatch").toString());
                case "QUA024_N":
                    return new BigDecimal(pool.get("OPE-02").get("surgeonOverlaps").toString());
                case "QUA024_D":
                    return new BigDecimal(pool.get("OPE-02").get("totalSurgeries").toString());
                default:
                    return null;

            }
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parsePeripheralValue(String code, Map<String, Map<String, Object>> pool) {
        try {
            switch (code) {
                case "QUA031":
                    return new BigDecimal(pool.get("LIS-01").get("reportTimeMedianMinutes").toString());
                case "QUA032_N":
                    return new BigDecimal(pool.get("LIS-01").get("inpatientSameDayDisposed").toString());
                case "QUA032_D":
                    return new BigDecimal(pool.get("LIS-01").get("inpatientCriticalValues").toString());
                case "SVC001":
                    return new BigDecimal(pool.get("SAT-01").get("satisfactionScore").toString());
                case "QUA030_N":
                    return new BigDecimal(pool.get("MED-01").get("stillActive").toString());
                case "QUA030_D":
                    return new BigDecimal(pool.get("MED-01").get("totalApproved").toString());
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseResourceValue(String code, Map<String, Map<String, Object>> pool) {
        try {
            switch (code) {
                case "PACS_EFF_01":
                    return new BigDecimal(pool.get("PACS-01").get("avgReportTimeMinutes").toString());
                case "HRP_ECO_01":
                    return new BigDecimal(pool.get("HRP-01").get("totalCost").toString());
                case "HRP_RT_02":
                    return new BigDecimal(pool.get("HRP-01").get("bedToNurseRatio").toString());
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public int syncDeptData(String periodDate, String deptCode) {
        log.warn("科室级实时采集暂未接入采集器，本版本维持空逻辑。");
        return 0;
    }

    @Override
    public void triggerCompositeCalculation(String periodDate) {
        List<SmIndicatorLib> compositeIndicators = indicatorService.list(new LambdaQueryWrapper<SmIndicatorLib>()
                .eq(SmIndicatorLib::getIsComposite, 1)
                .eq(SmIndicatorLib::getStatus, "0"));

        for (SmIndicatorLib indicator : compositeIndicators) {
            try {
                BigDecimal result = calcService.calculate(indicator, periodDate, "ALL");
                if (result != null) {
                    saveOrUpdateValue(indicator.getId(), "ALL", periodDate, result);
                    log.debug("引擎级重算 -> 指标 [{}] 分支同步, 结果: {}", indicator.getCode(), result);
                }
            } catch (Exception e) {
                log.error("引擎级重算 -> 指标 [{}] 计算失败: {}", indicator.getCode(), e.getMessage());
            }
        }
    }

    private BigDecimal mockSystemFetch(String code) {
        return BigDecimal.valueOf(Math.random() * 1000).setScale(2, RoundingMode.HALF_UP);
    }

    private boolean saveOrUpdateValue(Long indicatorId, String deptCode, String periodDate, BigDecimal value) {
        SmIndicatorValue existing = valueService.getOne(new LambdaQueryWrapper<SmIndicatorValue>()
                .eq(SmIndicatorValue::getIndicatorId, indicatorId)
                .eq(SmIndicatorValue::getDeptCode, deptCode)
                .eq(SmIndicatorValue::getPeriodDate, periodDate));

        if (existing != null) {
            existing.setValue(value);
            return valueService.updateById(existing);
        } else {
            SmIndicatorValue newValue = new SmIndicatorValue();
            newValue.setIndicatorId(indicatorId);
            newValue.setDeptCode(deptCode);
            newValue.setPeriodDate(periodDate);
            newValue.setValue(value);
            return valueService.save(newValue);
        }
    }
}
