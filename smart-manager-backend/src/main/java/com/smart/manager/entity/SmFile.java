package com.smart.manager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文件存储实体
 */
@Data
@TableName("sm_file")
public class SmFile {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文件名 */
    private String fileName;

    /** 存储路径 */
    private String filePath;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 文件类型 (MIME) */
    private String fileType;

    /** 所属模块 */
    private String module;

    /** 上传人 */
    private String createBy;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
