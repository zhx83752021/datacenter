package com.smart.manager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 专项报表中心记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sm_report")
public class SmReport extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 报表名称 */
    private String name;

    /** 报表类型 (月报/季报/年报/专项) */
    private String type;

    /** 所属科室 */
    private String dept;

    /** 部门ID (用于权限过滤) */
    private Long deptId;

    /** 状态 (草稿/已发布/待审核/已归档) */
    private String status;

    /** 报表文件存储路径 (文件系统或OSS路径) */
    private String filePath;

    /** 删除标志 (0代表存在 2代表删除) */
    @com.baomidou.mybatisplus.annotation.TableLogic
    private String delFlag;

    /** 生成人 */
    @com.baomidou.mybatisplus.annotation.TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private String createBy;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
