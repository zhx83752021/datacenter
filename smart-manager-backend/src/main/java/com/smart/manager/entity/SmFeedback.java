package com.smart.manager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 指标反馈业务工单实体类
 */
@Data
@TableName("sm_feedback")
public class SmFeedback {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联指标ID
     */
    private Long indicatorId;

    /**
     * 反馈内容/详情
     */
    private String content;

    /**
     * 当前状态 (0: 待处理, 1: 处理中, 2: 已打回, 3: 已修复)
     */
    private Integer status;

    /**
     * 上报人 (系统级则为 'SYSTEM')
     */
    private String createBy;

    /**
     * 处理人
     */
    private String processor;

    /**
     * 处理结果回溯记录
     */
    private String resultMsg;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
