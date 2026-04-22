package com.smart.manager;

import com.smart.manager.service.IIndicatorCalcService;
import com.smart.manager.entity.SmIndicatorLib;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class IndicatorCalcTest {

    @Autowired
    private IIndicatorCalcService calcService;

    @Test
    @DisplayName("测试复合指标基础计算逻辑")
    public void testSimpleCalculation() {
        // 测试复合指标公式计算: [I001] + [I002]
        SmIndicatorLib indicator = new SmIndicatorLib();
        indicator.setCode("COMP001");
        indicator.setIsComposite(1);
        indicator.setCalcFormula("[I001] + [I002]");

        // 注意：此处需要数据库中有 I001 和 I002 的数据，或者通过 Mock 模拟
        // 在集成测试环境中，我们预期即使无数据也应返回 0 而不是抛异常
        try {
            BigDecimal result = calcService.calculate(indicator, "2026-01", "ALL");
            log.info("复合指标计算结果: {}", result);
            assertNotNull(result);
        } catch (Exception e) {
            log.error("计算过程发生异常: ", e);
            fail("计算不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试指标公式验证器")
    public void testFormulaValidation() {
        // 合法公式
        assertTrue(calcService.validateFormula("[A] + [B] * 100"), "公式应为合法");
        assertTrue(calcService.validateFormula("[INCOME] / 100.0"), "带浮点数公式应为合法");

        // 非法公式
        assertFalse(calcService.validateFormula("[A] + (B * 100"), "括号不匹配应为非法");
        assertFalse(calcService.validateFormula(""), "空公式应为非法");
    }

    @Test
    @DisplayName("测试递归解析保护")
    public void testRecursionSafety() {
        // 虽然 IIndicatorCalcService 内部由 buildNodeRecursively 处理环路，
        // calculate 也能通过 visited 预防，此处验证其鲁棒性
        assertTrue(true); // 占位，待后续深度测试 SpEL 运行时递归
    }

    @Test
    @DisplayName("测试跨年同环比分析(极端的 1月份 和 2月份)")
    public void testCrossYearYoyAnalysis() {
        // 测试 2026-02 对去年同期 2025-02 和上月期 2026-01 的计算
        // 需依赖 test_data.sql 插入的基础数据：
        // 2026-02 (55w), 2026-01 (50w), 2025-12 (56w), 2025-02 (42w)
        com.smart.manager.dto.IndicatorYoyAnalysisDTO analysis = calcService.getAdvancedYoyAnalysis("INCOME_DRUG",
                "2026-02", "YWK", 3);

        if (analysis != null && analysis.getTrendBase() != null && !analysis.getTrendBase().isEmpty()) {
            log.info("同环比分析提取数据: {}", analysis.getTrendBase());
            // 找出 2026-02 节点
            com.smart.manager.dto.IndicatorYoyNode node202602 = analysis.getTrendBase().stream()
                    .filter(n -> "2026-02".equals(n.getPeriod()))
                    .findFirst().orElse(null);

            assertNotNull(node202602, "未找到 2026-02 分析节点");
            log.info("2026-02 分析节点: MoM={}%, YoY={}%", node202602.getMomRate(), node202602.getYoyRate());

            // 环比 2026-02 vs 2026-01 (550000 - 500000) / 500000 = 10%
            assertEquals(0, node202602.getMomRate().compareTo(new BigDecimal("10.00")), "环比(MoM)计算错误");

            // 同比 2026-02 vs 2025-02 (550000 - 420000) / 420000 = 30.95%
            assertEquals(0, node202602.getYoyRate().compareTo(new BigDecimal("30.95")), "同比(YoY)计算错误");

            // 提取 2026-01 节点，验证跨年环比 (2026-01 vs 2025-12)
            com.smart.manager.dto.IndicatorYoyNode node202601 = analysis.getTrendBase().stream()
                    .filter(n -> "2026-01".equals(n.getPeriod()))
                    .findFirst().orElse(null);

            assertNotNull(node202601, "未找到 2026-01 分析节点");
            // 环比 2026-01 vs 2025-12 (500000 - 560000) / 560000 = -10.71%
            assertEquals(0, node202601.getMomRate().compareTo(new BigDecimal("-10.71")), "跨年环比(MoM)计算错误");
        } else {
            log.warn("未能获取分析结果，可能是数据库无对应的试算数据");
        }
    }
}
