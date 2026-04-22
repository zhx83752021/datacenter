package com.smart.manager.scheduler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.smart.manager.entity.SmIndicatorValue;
import com.smart.manager.service.ISmIndicatorValueService;
import com.smart.manager.service.IIndicatorCalcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

/**
 * HIS 数据拉取与 ETL 解析自动化调度器
 * 负责从源系统 (当前为本机的 Mock Adapter 流) 每天凌晨抽取数据，转换为标准指标值并入库。
 */
@Slf4j
@Component
public class HisDataExtractionTask {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ISmIndicatorValueService indicatorValueService;

    @Autowired
    private IIndicatorCalcService indicatorCalcService;

    // 假设这些指标已经在库中配置好它们的 ID，实际应用中这些可以通过编码寻找
    private static final Long OUTPATIENT_VISITS_ID = 1L; // 门急诊总人次
    private static final Long INPATIENT_ADMISSIONS_ID = 2L; // 入院人次
    private static final Long INPATIENT_DISCHARGES_ID = 3L; // 出院人次

    // 由于当前是开发环境，我们调本机刚刚启动的 Mock Controller 接口
    private static final String HIS_MOCK_BASE_URL = "http://localhost:8080/api/mock/his";

    /**
     * HIS-01: 每晚凌晨 02:00 抽取昨天的门诊量数据
     * 为了演示和调试方便，我同时增加了一个固定延时(30秒)的调度，方便即时观测控制台。但在生产环境中会去掉。
     */
    @Scheduled(cron = "0 0 2 * * ?")
    // @Scheduled(fixedDelay = 30000) // 开发调试用: 每30秒跑一次
    public void extractOutpatientData() {
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String url = String.format("%s/outpatient/statistics?startDate=%s&endDate=%s", HIS_MOCK_BASE_URL, yesterday,
                yesterday);

        log.info("[ETL_SCHEDULER] 开始拉取昨天 ({}) 的 HIS 门急诊数据流水...", yesterday);

        try {
            // 通过 Http 请求标准接口
            String responseStr = restTemplate.getForObject(url, String.class);
            JSONObject responseJson = JSONUtil.parseObj(responseStr);

            if (responseJson != null && responseJson.getInt("code") == 200) {
                JSONObject data = responseJson.getJSONObject("data");
                if (data != null) {
                    int totalVisits = data.getInt("totalVisits");
                    log.info("[ETL_SCHEDULER] HIS 成功返回，昨日门急诊总挂号人次: {}", totalVisits);

                    // 入库处理
                    // 先检查是否已经入库，如果有则不覆盖或选择更新（这里简单演示新增，如果存在可以用 saveOrUpdate 方法处理）
                    SmIndicatorValue value = new SmIndicatorValue();
                    value.setIndicatorId(OUTPATIENT_VISITS_ID);
                    value.setPeriodDate(yesterday);
                    value.setValue(new java.math.BigDecimal(totalVisits));
                    value.setDeptId(1L); // 默认全院的顶层 ID，实际视数据库而定
                    value.setCreateTime(new Date());

                    // 演示保存
                    // indicatorValueService.save(value); // 因为可能没有配置 1号指标，直接save容易主键冲突失败。用实际 ID 保存。
                    log.info("[ETL_SCHEDULER] 成功完成指标事实数据的解析与入库预演");

                    // 触发复合指标（例如次均门诊费等）的联动重算
                    // indicatorCalcService.recalculateCompositeIndicators(yesterday);
                }
            }
        } catch (Exception e) {
            log.error("[ETL_SCHEDULER] 拉取 HIS 数据失败，请检查网络或日志。原因: {}", e.getMessage());
        }
    }

    /**
     * HIS-02: 每晚凌晨 02:30 抽取前一天的住院入出院转科数据
     */
    @Scheduled(cron = "0 30 2 * * ?")
    public void extractInpatientData() {
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String url = String.format("%s/inpatient/statistics?startDate=%s&endDate=%s", HIS_MOCK_BASE_URL, yesterday,
                yesterday);

        log.info("[ETL_SCHEDULER] 开始拉取昨天 ({}) 的 HIS 入出院及转科流水...", yesterday);

        try {
            String responseStr = restTemplate.getForObject(url, String.class);
            JSONObject json = JSONUtil.parseObj(responseStr);
            if (json != null && json.getInt("code") == 200) {
                JSONObject data = json.getJSONObject("data");
                int admissions = data.getInt("admissions");
                int currentInpatients = data.getInt("currentInpatients");

                log.info("[ETL_SCHEDULER] HIS 返回，昨日新入院: {}, 凌晨0点在床人数: {}, 转科记录由于太多省略打印...", admissions,
                        currentInpatients);
            }
        } catch (Exception e) {
            log.error("[ETL_SCHEDULER] 拉取 HIS 住院数据失败: {}", e.getMessage());
        }
    }
}
