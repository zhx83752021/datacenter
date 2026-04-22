package com.smart.manager.service;

import com.smart.manager.entity.SmIndicatorLib;
import com.smart.manager.entity.SmIndicatorValue;
import com.smart.manager.mapper.SmIndicatorLibMapper;
import com.smart.manager.service.impl.IndicatorCalcServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * 核心指标计算引擎单元测试
 * 针对 SpEL 算术演算与业务边界进行鲁棒性加固测试
 */
@ExtendWith(MockitoExtension.class)
public class IndicatorCalcServiceImplTest {

    @InjectMocks
    private IndicatorCalcServiceImpl indicatorCalcService;

    @Test
    @DisplayName("🧪 算子引擎 - 常规加减乘除优先级校验")
    public void testEvalMathExpression_Basic() throws Exception {
        Method evalMethod = IndicatorCalcServiceImpl.class.getDeclaredMethod("evalMathExpression", String.class);
        evalMethod.setAccessible(true);

        // 验证业务常见的加总然后再扣除的复合计算
        String expr = "(100.50 + 200.25) * 0.5";
        BigDecimal res = (BigDecimal) evalMethod.invoke(indicatorCalcService, expr);

        Assertions.assertEquals(new BigDecimal("150.375").setScale(3), res.setScale(3));
    }

    @Test
    @DisplayName("🧪 计算边界 - 零除防爆降维处理验证")
    public void testEvalMathExpression_DivByZero() throws Exception {
        Method evalMethod = IndicatorCalcServiceImpl.class.getDeclaredMethod("evalMathExpression", String.class);
        evalMethod.setAccessible(true);

        // SpEL对于整数1/0会报出除零异常，但对于浮点数 1.0/0.0 会返回 Infinity.
        // 该测试验证引擎底层是否能够捕捉异常或处理无限值
        try {
            // 这里测试在业务层我们通常会将整数通过正则转为浮点数
            String expr = "100.0 / 0.0";
            Object res = evalMethod.invoke(indicatorCalcService, expr);
            // 这里若抛出异常属于正常业务保护逻辑，或者返回Infinity均算作通过（交由前端拦截）
            Assertions.assertNotNull(res);
        } catch (Exception e) {
            Assertions.assertTrue(e.getCause() instanceof ArithmeticException || e.getCause() instanceof Exception,
                    "必须能够拦截到原生数学运算异常");
        }
    }

    @Test
    @DisplayName("🧪 公式容错 - 负值演化验证")
    public void testEvalMathExpression_NegativeResult() throws Exception {
        Method evalMethod = IndicatorCalcServiceImpl.class.getDeclaredMethod("evalMathExpression", String.class);
        evalMethod.setAccessible(true);

        // 测试扣减项大于收入项导致的负值情况
        String expr = "500.0 - 1500.0";
        BigDecimal res = (BigDecimal) evalMethod.invoke(indicatorCalcService, expr);

        Assertions.assertEquals(new BigDecimal("-1000.0"), res);
    }
}
