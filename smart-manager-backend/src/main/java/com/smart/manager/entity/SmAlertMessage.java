package com.smart.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 预警消息实体
 */
@Data
@TableName("sm_alert_message")
public class SmAlertMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 指标ID */
    private Long indicatorId;

    /** 触发规则ID */
    private Long ruleId;

    /** 预警等级 */
    private Integer level;

    /** 消息内容 */
    private String content;

    /** 状态 (0:未读, 1:已读) */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
