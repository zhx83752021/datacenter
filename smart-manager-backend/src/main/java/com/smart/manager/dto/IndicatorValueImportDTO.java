package com.smart.manager.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class IndicatorValueImportDTO {

    @ExcelProperty("指标编码")
    private String indicatorCode;

    @ExcelProperty("科室编码")
    private String deptCode;

    @ExcelProperty("统计日期")
    private String periodDate; // Expect string format yyyy-MM-dd

    @ExcelProperty("数值")
    private BigDecimal value;
}
