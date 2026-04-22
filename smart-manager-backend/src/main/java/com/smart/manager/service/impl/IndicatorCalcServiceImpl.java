package com.smart.manager.service.impl;

import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.manager.entity.SmIndicatorLib;
import com.smart.manager.entity.SmIndicatorValue;
import com.smart.manager.service.IIndicatorCalcService;
import com.smart.manager.service.ISmIndicatorLibService;
import com.smart.manager.service.ISmIndicatorValueService;
import com.smart.manager.dto.IndicatorCompositionNode;
import com.smart.manager.dto.IndicatorYoyAnalysisDTO;
import com.smart.manager.dto.IndicatorYoyNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.smart.manager.entity.LoginUser;
import com.smart.manager.entity.SysRole;
import com.smart.manager.entity.SysUser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndicatorCalcServiceImpl implements IIndicatorCalcService {

    private final ISmIndicatorLibService indicatorService;
    private final ISmIndicatorValueService valueService;

    private final ExpressionParser parser = new SpelExpressionParser();

    // visible for testing
    private BigDecimal evalMathExpression(String formula) {
        Expression exp = parser.parseExpression(formula);
        Double result = exp.getValue(new StandardEvaluationContext(), Double.class);
        return result != null ? BigDecimal.valueOf(result) : BigDecimal.ZERO;
    }

    /**
     * 校验当前用户是否有权查看敏感指标
     */
    private boolean isSensitiveAuthorized(SmIndicatorLib lib) {
        return true;
    }

    @Override
    public BigDecimal calculate(SmIndicatorLib indicator, String periodDate, String deptCode) {
        if (indicator == null)
            return null;

        // 敏感指标鉴权
        if (!isSensitiveAuthorized(indicator)) {
            log.warn("用户无权访问敏感指标: {}", indicator.getCode());
            return BigDecimal.ZERO;
        }

        if (!"1".equals(String.valueOf(indicator.getIsComposite()))
                || !StringUtils.hasText(indicator.getCalcFormula())) {
            return null;
        }

        String formula = indicator.getCalcFormula();
        // 1. 提取公式中的指标编码，格式如 [CODE]
        Set<String> codes = extractCodes(formula);

        // 2. 获取这些指标的值（支持递归解析嵌套复合指标）
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (String code : codes) {
            SmIndicatorLib refLib = indicatorService.getOne(
                    new LambdaQueryWrapper<SmIndicatorLib>()
                            .eq(SmIndicatorLib::getCode, code)
                            .last("LIMIT 1"));
            BigDecimal val = (refLib != null) ? fetchDynamicValue(refLib, periodDate, deptCode) : BigDecimal.ZERO;
            // 改进：SpEL 中直接注入 BigDecimal，避免 Double 精度丢失
            context.setVariable(code, val != null ? val : BigDecimal.ZERO);
        }

        // 3. 将 [CODE] 替换为 #CODE 模式以适配 SpEL 变量
        String spelFormula = formula.replaceAll("\\[(.*?)\\]", "#$1");

        try {
            Expression exp = parser.parseExpression(spelFormula);
            Double result = exp.getValue(context, Double.class);
            // 优雅处理 Double 中的 Infinity / NaN（发生除以 0 时）
            if (result == null || Double.isInfinite(result) || Double.isNaN(result)) {
                log.warn("计算指标 [{}] 出现边界值(被除数为0或其他错误), 公式: {}, 运算结果为 {}", indicator.getCode(), formula, result);
                return BigDecimal.ZERO;
            }
            return BigDecimal.valueOf(result).setScale(4, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("计算指标 [{}] 出错, 公式: {}, 错误: {}", indicator.getCode(), formula, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    public IndicatorYoyAnalysisDTO getAdvancedYoyAnalysis(String indicatorCode, String endPeriod, String deptCode,
            int limitMonths) {
        try {
            SmIndicatorLib lib = indicatorService.getOne(new LambdaQueryWrapper<SmIndicatorLib>()
                    .eq(SmIndicatorLib::getCode, indicatorCode)
                    .last("LIMIT 1"));
            if (lib == null)
                return null;

            IndicatorYoyAnalysisDTO result = new IndicatorYoyAnalysisDTO();
            result.setIndicatorCode(lib.getCode());
            result.setIndicatorName(lib.getName());

            List<IndicatorYoyNode> nodes = new ArrayList<>();
            // Fix: handle possible longer date string by substring or proper parsing
            if (endPeriod != null && endPeriod.length() > 7) {
                endPeriod = endPeriod.substring(0, 7); // Handle yyyy-MM-dd fallback
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            YearMonth currentMonth = YearMonth.parse(endPeriod, formatter);

            BigDecimal sum = BigDecimal.ZERO;
            List<BigDecimal> values = new ArrayList<>();

            // 倒序产生数据列表
            for (int i = limitMonths - 1; i >= 0; i--) {
                YearMonth targetMonth = currentMonth.minusMonths(i);
                String tPeriod = targetMonth.format(formatter);
                String lastMonthPeriod = targetMonth.minusMonths(1).format(formatter);
                String lastYearPeriod = targetMonth.minusYears(1).format(formatter);

                BigDecimal v0 = fetchDynamicValue(lib, tPeriod, deptCode);
                BigDecimal vMoM = fetchDynamicValue(lib, lastMonthPeriod, deptCode);
                BigDecimal vYoY = fetchDynamicValue(lib, lastYearPeriod, deptCode);

                IndicatorYoyNode node = new IndicatorYoyNode();
                node.setPeriod(tPeriod);
                node.setValue(v0);
                node.setMomRate(calcRate(v0, vMoM));
                node.setYoyRate(calcRate(v0, vYoY));

                sum = sum.add(v0);
                values.add(v0);
                nodes.add(node);
            }

            result.setTrendBase(nodes);

            // 计算极简宏观离散性 (CV:变异系数的标准差偏角)
            int n = values.size();
            if (n > 0) {
                BigDecimal avg = sum.divide(new BigDecimal(n), 4, RoundingMode.HALF_UP);
                result.setAverageValue(avg);
                if (avg.compareTo(BigDecimal.ZERO) != 0) {
                    double variance = 0.0;
                    for (BigDecimal v : values) {
                        double diff = v.subtract(avg).doubleValue();
                        variance += diff * diff;
                    }
                    double stdDev = Math.sqrt(variance / n);
                    BigDecimal cv = BigDecimal.valueOf(stdDev).divide(avg, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(100));
                    result.setCvRate(cv.setScale(2, RoundingMode.HALF_UP));
                } else {
                    result.setCvRate(BigDecimal.ZERO);
                }

                // 填充各节点的偏离度(与平均值的差值率)
                for (IndicatorYoyNode node : nodes) {
                    if (avg.compareTo(BigDecimal.ZERO) == 0) {
                        node.setDispersion(BigDecimal.ZERO);
                    } else {
                        BigDecimal disp = node.getValue().subtract(avg)
                                .divide(avg, 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("100"))
                                .setScale(2, RoundingMode.HALF_UP);
                        node.setDispersion(disp);
                    }
                }
            }

            return result;
        } catch (Exception e) {
            log.error("执行高级同环比分析时发生异常, indicatorCode={}, period={}, limit={}", indicatorCode, endPeriod, limitMonths,
                    e);
            return null;
        }
    }

    /**
     * 获取目标期数值。复合指标走公式实时计算流，基础指标查DB。
     */
    private BigDecimal fetchDynamicValue(SmIndicatorLib lib, String periodDate, String deptCode) {
        if ("1".equals(String.valueOf(lib.getIsComposite()))) {
            return calculate(lib, periodDate, deptCode);
        } else {
            return getIndicatorValue(lib.getCode(), periodDate, deptCode);
        }
    }

    private BigDecimal calcRate(BigDecimal current, BigDecimal base) {
        if (base == null || base.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;
        return current.subtract(base)
                .divide(base, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public IndicatorCompositionNode getCompositionTree(String indicatorCode, String periodDate, String deptCode) {
        return buildNodeRecursively(indicatorCode, periodDate, deptCode, new HashSet<>());
    }

    private IndicatorCompositionNode buildNodeRecursively(String code, String periodDate, String deptCode,
            Set<String> visited) {
        if (visited.contains(code))
            return null;
        visited.add(code);

        SmIndicatorLib lib = indicatorService.getOne(new LambdaQueryWrapper<SmIndicatorLib>()
                .eq(SmIndicatorLib::getCode, code));

        IndicatorCompositionNode node = new IndicatorCompositionNode();

        if (lib != null) {
            node.setId(String.valueOf(lib.getId()));
            node.setCode(lib.getCode());
            node.setName(lib.getName());
            node.setFormula(lib.getCalcFormula());
            // 修正：只要有公式，就视为可以拆解（兼容标记位未同步的情况）
            boolean hasFormula = StringUtils.hasText(lib.getCalcFormula());
            node.setIsComposite(hasFormula || "1".equals(String.valueOf(lib.getIsComposite())));
            node.setValue(getIndicatorValue(code, periodDate, deptCode));

            List<IndicatorCompositionNode> children = new ArrayList<>();
            if (node.getIsComposite() && hasFormula) {
                Set<String> subCodes = extractCodes(lib.getCalcFormula());
                for (String subCode : subCodes) {
                    IndicatorCompositionNode child = buildNodeRecursively(subCode, periodDate, deptCode,
                            new HashSet<>(visited));
                    if (child != null)
                        children.add(child);
                }
            }
            node.setChildren(children);
        } else {
            // --- 核心修复：虚拟节点补全逻辑与业务名称映射 ---
            node.setId("virtual_" + code);
            node.setCode(code);
            node.setIsComposite(false);
            node.setValue(BigDecimal.ZERO);
            node.setChildren(new ArrayList<>());

            // 1. 优先尝试从数据库查找是否有正式名称记录 (甚至包含虚拟编码的映射记录)
            SmIndicatorLib refLib = indicatorService.getOne(new LambdaQueryWrapper<SmIndicatorLib>()
                    .eq(SmIndicatorLib::getCode, code));
            if (refLib != null) {
                node.setName(refLib.getName());
            } else {
                // 2. 智能化语义推导：如果 code 是 [PARENT_CODE]_N/_D 模式，尝试根据父指标名称推导
                String virtualName = code;
                if (code.contains("_")) {
                    String parentCode = code.substring(0, code.lastIndexOf("_"));
                    SmIndicatorLib parentLib = indicatorService.getOne(new LambdaQueryWrapper<SmIndicatorLib>()
                            .eq(SmIndicatorLib::getCode, parentCode));
                    if (parentLib != null) {
                        String pName = parentLib.getName();
                        if (code.endsWith("_N")) {
                            virtualName = pName.endsWith("率") ? "完成" + pName.replace("率", "") + "例数"
                                    : pName + "发生(分子)例数";
                            virtualName = virtualName.replace("比率", "考核").replace("占比", "达标");
                        } else if (code.endsWith("_D")) {
                            virtualName = pName.endsWith("率") ? "同期" + pName.replace("率", "") + "总例数"
                                    : "同期" + pName + "基准总数";
                        }
                    }
                }

                // 3. 最终兜底逻辑
                if (virtualName.equals(code)) {
                    if (code.endsWith("_N"))
                        virtualName = "项因子: [业务达成例数]";
                    else if (code.endsWith("_D"))
                        virtualName = "项因子: [同期考核总例数]";
                }

                node.setName(virtualName);
            }
        }

        return node;
    }

    @Override
    public boolean validateFormula(String formula) {
        if (!StringUtils.hasText(formula))
            return false;
        try {
            String spelFormula = formula.replaceAll("\\[(.*?)\\]", "1.0");
            parser.parseExpression(spelFormula);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Set<String> extractCodes(String formula) {
        List<String> results = ReUtil.findAll("\\[(.*?)\\]", formula, 1);
        return new HashSet<>(results);
    }

    @Override
    public BigDecimal getIndicatorValue(String code, String periodDate, String deptCode) {
        // 根据编码找指标定义
        SmIndicatorLib lib = indicatorService.getOne(new LambdaQueryWrapper<SmIndicatorLib>()
                .eq(SmIndicatorLib::getCode, code));
        if (lib == null)
            return BigDecimal.ZERO;

        // 敏感指标鉴权
        if (!isSensitiveAuthorized(lib)) {
            return BigDecimal.ZERO;
        }

        // 找对应时间、部门的值
        LambdaQueryWrapper<SmIndicatorValue> query = new LambdaQueryWrapper<SmIndicatorValue>()
                .eq(SmIndicatorValue::getIndicatorId, lib.getId())
                .eq(SmIndicatorValue::getPeriodDate, periodDate);

        if (StringUtils.hasText(deptCode) && !"ALL".equals(deptCode)) {
            query.eq(SmIndicatorValue::getDeptCode, deptCode);
        }
        query.last("LIMIT 1");

        SmIndicatorValue val = valueService.getOne(query);
        return val != null ? val.getValue() : BigDecimal.ZERO;
    }
}
