package com.smart.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 指标知识库实体
 */
@Data
@TableName("sm_indicator_lib")
public class SmIndicatorLib extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类ID */
    private Long categoryId;

    /** 指标名称 */
    @ExcelProperty("指标名称")
    private String name;

    /** 指标编码 */
    @ExcelProperty("指标编码")
    private String code;

    /** 指标描述/定义 */
    private String description;

    /** 计量单位 */
    @ExcelProperty("单位")
    private String unit;

    /** 统计频率（D:日, W:周, M:月, Q:季, Y:年） */
    private String frequency;

    /** 数据来源（System:系统自动, Manual:人工填报） */
    private String dataSource;

    /** 是否敏感指标（0:否, 1:是） */
    private Integer isSensitive;

    /** 是否复合指标（0:否, 1:是） */
    private Integer isComposite;

    /** 计算公式 */
    @ExcelProperty("计算公式")
    private String calcFormula;

    /** 政策依据（如: 2025版核心制度指标-指标一） */
    @ExcelProperty("政策依据")
    private String policySource;

    /** 核心制度归属（如: 首诊负责制、三级查房制度） */
    @ExcelProperty("核心制度")
    private String coreSystem;

    /** 分子指标编码 */
    private String numeratorCode;

    /** 分母指标编码 */
    private String denominatorCode;

    /** 状态（0正常 1停用） */
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
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
