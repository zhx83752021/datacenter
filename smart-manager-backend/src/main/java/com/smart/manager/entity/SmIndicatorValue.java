package com.smart.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 指标数据值实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sm_indicator_value")
public class SmIndicatorValue extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 指标ID */
    private Long indicatorId;

    /** 部门ID (用于权限过滤) */
    private Long deptId;

    /** 科室编码 (全院级为ALL或空) */
    private String deptCode;

    /** 统计周期（yyyy-MM-dd / yyyy-MM / yyyy） */
    private String periodDate;

    /** 指标值 */
    private BigDecimal value;

    /** 同比值 (Optional, can be calculated) */
    @TableField(exist = false)
    private BigDecimal yoyValue;

    /** 环比值 (Optional, can be calculated) */
    @TableField(exist = false)
    private BigDecimal momValue;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
