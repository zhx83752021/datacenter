package com.smart.manager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 指标目标实体
 */
@Data
@TableName("sm_target")
public class SmTarget {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long indicatorId;

    /** 目标值 */
    private BigDecimal targetValue;

    private String unit;

    /** 周期类型 (月/季/年) */
    private String periodType;

    /** 期指日期 (如 2024-05) */
    private String periodDate;

    /** 适用科室 */
    private String deptCode;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
