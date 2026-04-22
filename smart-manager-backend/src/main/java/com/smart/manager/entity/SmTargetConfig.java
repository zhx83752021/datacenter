package com.smart.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 指标目标配置实体
 */
@Data
@TableName("sm_target_config")
public class SmTargetConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 指标ID */
    private Long indicatorId;

    /** 目标年度/期间 */
    private String period;

    /** 适用科室（空为全院） */
    private String deptCode;

    /** 目标值 */
    private BigDecimal targetValue;

    /** 挑战值 (Optional) */
    private BigDecimal challengeValue;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
