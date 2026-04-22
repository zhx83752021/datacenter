package com.smart.manager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class SysRole {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String roleName;
    private String roleKey;
    private Integer roleSort;
    private String dataScope;
    private Integer indicatorSensitive; // 敏感指标查看权限 (0:否, 1:是)
    private Integer status;
    @TableLogic
    private Integer delFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
}
