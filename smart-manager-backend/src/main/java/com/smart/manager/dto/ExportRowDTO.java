package com.smart.manager.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 指标数据导出行 DTO
 */
@Data
@HeadRowHeight(30)
public class ExportRowDTO {

    /** 指标编码 */
    @ExcelProperty("指标编码")
    @ColumnWidth(18)
    private String indicatorCode;

    /** 指标名称 */
    @ExcelProperty("指标名称")
    @ColumnWidth(20)
    private String indicatorName;

    /** 单位 */
    @ExcelProperty("单位")
    @ColumnWidth(10)
    private String unit;

    /** 本期值 */
    @ExcelProperty("本期值")
    @ColumnWidth(15)
    private BigDecimal currentValue;

    /** 上期值(环比基准) */
    @ExcelProperty("上期值")
    @ColumnWidth(15)
    private BigDecimal lastMonthValue;

    /** 环比(%) */
    @ExcelProperty("环比(%)")
    @ColumnWidth(12)
    private BigDecimal momRate;

    /** 上年同期值(同比基准) */
    @ExcelProperty("上年同期值")
    @ColumnWidth(15)
    private BigDecimal lastYearValue;

    /** 同比(%) */
    @ExcelProperty("同比(%)")
    @ColumnWidth(12)
    private BigDecimal yoyRate;
}
