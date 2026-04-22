-- 文件存储记录表
CREATE TABLE IF NOT EXISTS sm_file (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    file_name   VARCHAR(255)    NOT NULL COMMENT '文件名',
    file_path   VARCHAR(512)    NOT NULL COMMENT '存储路径',
    file_size   BIGINT          NULL     COMMENT '文件大小(字节)',
    file_type   VARCHAR(64)     NULL     COMMENT '文件类型 (MIME)',
    module      VARCHAR(64)     NULL     COMMENT '所属模块',
    create_by   VARCHAR(64)     NULL     COMMENT '上传人',
    create_time DATETIME        NULL     COMMENT '上传时间',
    update_time DATETIME        NULL     COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件存储表';
