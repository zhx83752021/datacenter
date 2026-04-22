package com.smart.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预警规则实体
 */
@Data
@TableName("sm_alert_rule")
public class SmAlertRule implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 指标ID */
    private Long indicatorId;

    /** 规则名称 */
    private String ruleName;

    /** 比较操作符 (GT:大于, LT:小于, GE:大于等于, LE:小于等于, EQ:等于) */
    private String operator;

    /** 阈值 */
    private BigDecimal threshold;

    /** 预警等级 (1:普通, 2:严重, 3:紧急) */
    private Integer level;

    /** 是否启用 (0:停用, 1:启用) */
    private Integer status;

    /** 消息模板 */
    private String messageTemplate;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
