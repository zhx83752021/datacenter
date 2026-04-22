package com.smart.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 看板管理实体
 * 用于注册和发布看板，支持挂载外部链接（IFrame URL）
 */
@Data
@TableName("sm_dashboard")
public class SmDashboard implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 看板名称（25字以内） */
    private String name;

    /** 看板类型（cockpit-决策驾驶舱, theme-运营主题分析, monitor-监测仪表盘, report-数据分析报表） */
    private String category;

    /** 描述 */
    private String description;

    /** 指标主题（关联指标分类ID） */
    private Long themeId;

    /** 指标主题名称（冗余字段，展示用） */
    @TableField(exist = false)
    private String themeName;

    /** 发布对象类型（role-角色, dept-科室, user-指定用户） */
    private String publishType;

    /** 发布对象ID（角色ID/科室ID/用户ID，多个逗号分隔） */
    private String publishTarget;

    /** 看板地址（IFrame外部链接URL） */
    private String url;

    /** 大屏布局配置(JSON格式) — 保留兼容旧大屏设计器 */
    private String layoutConfig;

    /** 缩略图链接 */
    private String thumbnail;

    /** 状态（online-已上线, offline-已下线, draft-草稿） */
    private String status;

    /** 是否为模板(0否 1是) */
    private String isTemplate;

    /** 发布人 */
    private String publishBy;

    /** 发布时间 */
    private Date publishTime;

    /** 删除标志 */
    @TableLogic
    private String delFlag;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
