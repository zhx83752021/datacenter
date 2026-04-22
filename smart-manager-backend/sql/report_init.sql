-- 专项报表记录表
CREATE TABLE IF NOT EXISTS sm_report (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name        VARCHAR(255)    NOT NULL COMMENT '报表名称',
    type        VARCHAR(64)     NOT NULL COMMENT '报表类型 (月报/季报/年报/专项)',
    dept        VARCHAR(128)    NULL     COMMENT '所属科室',
    status      VARCHAR(32)     DEFAULT '草稿' COMMENT '状态 (草稿/已发布/待审核)',
    file_path   VARCHAR(512)    NULL     COMMENT '报表文件存储路径',
    create_by   VARCHAR(64)     NULL     COMMENT '生成人',
    create_time DATETIME        NULL     COMMENT '生成时间',
    update_time DATETIME        NULL     COMMENT '更新时间',
    INDEX idx_type (type),
    INDEX idx_dept (dept),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专项报表中心记录';

-- 插入测试数据
INSERT INTO sm_report (name, type, dept, status, create_by, create_time, update_time) VALUES
('2026年2月全院医疗质量月报', '月报', '医务科', '已发布', 'admin', '2026-02-25 14:30:00', '2026-02-25 14:30:00'),
('2026年2月财务收支明细表', '月报', '财务科', '待审核', 'admin', '2026-02-25 09:15:00', '2026-02-25 09:15:00'),
('2026年第一季度运营分析报告', '季报', '运营部', '已发布', 'admin', '2026-02-24 16:45:00', '2026-02-24 16:45:00'),
('2025年度全院运营总结报告', '年报', '院办', '已发布', 'admin', '2026-01-15 10:00:00', '2026-01-15 10:00:00'),
('2026年1月门诊流量专题分析', '月报', '门诊部', '已发布', 'admin', '2026-02-05 11:20:00', '2026-02-05 11:20:00'),
('2026年护理质控专项检查报告', '专项', '护理部', '已发布', 'admin', '2026-02-10 08:30:00', '2026-02-10 08:30:00');
