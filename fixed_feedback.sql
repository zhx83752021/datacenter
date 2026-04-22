-- 指标反馈工单表
CREATE TABLE IF NOT EXISTS sm_feedback (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    indicator_id BIGINT          NULL     COMMENT '关联指标ID',
    content     VARCHAR(1000)   NOT NULL COMMENT '反馈内容/问题描述',
    status      INT DEFAULT 0   NOT NULL COMMENT '状态: 0=待处理, 1=处理中, 2=已打回, 3=已修复',
    create_by   VARCHAR(64)     NULL     COMMENT '上报人(系统级为SYSTEM)',
    processor   VARCHAR(64)     NULL     COMMENT '处理人',
    result_msg  VARCHAR(1000)   NULL     COMMENT '处理结果/回溯记录',
    create_time DATETIME        NULL     COMMENT '创建时间',
    update_time DATETIME        NULL     COMMENT '更新时间',
    INDEX idx_indicator_id (indicator_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标反馈工单';

-- 插入测试数据
TRUNCATE TABLE sm_feedback;
INSERT INTO sm_feedback (indicator_id, content, status, create_by, processor, result_msg, create_time, update_time) VALUES
(1, '5月19日门急诊收入数据显示为0，请核实是否ETL同步失败', 0, '张医生', NULL, NULL, '2026-02-25 14:30:00', '2026-02-25 14:30:00'),
(3, '药占比分母似乎未包含中药收入，导致计算结果偏高', 1, '李主任', 'admin', '已确认中药收入未纳入，正在修正公式', '2026-02-24 09:15:00', '2026-02-25 10:00:00'),
(5, '四级手术率定义是否包含微创手术？与卫健委口径不一致', 3, '王护士长', 'admin', '已对齐卫健委DRG 2.0标准口径，微创手术不纳入', '2026-02-23 16:45:00', '2026-02-26 11:30:00'),
(2, '住院人次2月份数据异常偏低，疑似缺少东院区数据', 0, '赵主任', NULL, NULL, '2026-02-26 08:20:00', '2026-02-26 08:20:00'),
(1, '门急诊总人次与HIS系统统计不一致，差异约500人次', 1, '钱护士', 'admin', '正在排查ETL抽取逻辑', '2026-02-22 11:00:00', '2026-02-25 15:00:00');
