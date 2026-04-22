-- 指标目标表
CREATE TABLE IF NOT EXISTS sm_target (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    indicator_id BIGINT          NOT NULL COMMENT '指标ID',
    target_value DECIMAL(18,4)   NOT NULL COMMENT '目标值',
    unit        VARCHAR(32)     NULL     COMMENT '单位',
    period_type VARCHAR(32)     NOT NULL COMMENT '周期类型 (月/季/年)',
    period_date VARCHAR(32)     NOT NULL COMMENT '期指日期 (如 2024-05)',
    dept_code   VARCHAR(64)     NULL     COMMENT '适用科室 (null为全院)',
    create_time DATETIME        NULL     COMMENT '创建时间',
    update_time DATETIME        NULL     COMMENT '修改时间',
    INDEX idx_indicator_period (indicator_id, period_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标目标管理表';

-- 预警规则表
CREATE TABLE IF NOT EXISTS sm_alert_rule (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    indicator_id BIGINT          NOT NULL COMMENT '指标ID',
    rule_name   VARCHAR(128)    NOT NULL COMMENT '规则名称',
    operator    VARCHAR(16)     NOT NULL COMMENT '比较符 (>, <, >=, <=, !=)',
    threshold   DECIMAL(18,4)   NOT NULL COMMENT '预警阈值',
    level       VARCHAR(32)     NOT NULL COMMENT '预警等级 (提示/警告/核心严重)',
    enabled     TINYINT(1)      DEFAULT 1 COMMENT '是否启用 (1启用 0禁用)',
    create_time DATETIME        NULL     COMMENT '创建时间',
    update_time DATETIME        NULL     COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标预警规则表';

-- 预警消息记录表
CREATE TABLE IF NOT EXISTS sm_alert_message (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    indicator_id BIGINT          NOT NULL COMMENT '指标ID',
    rule_id     BIGINT          NOT NULL COMMENT '触发规则ID',
    content     TEXT            NOT NULL COMMENT '预警内容',
    val         DECIMAL(18,4)   NULL     COMMENT '触发时的实际值',
    level       VARCHAR(32)     NULL     COMMENT '预警级别',
    status      TINYINT(1)      DEFAULT 0 COMMENT '处理状态 (0未读 1已读 2已处理)',
    create_time DATETIME        NULL     COMMENT '产生时间',
    update_time DATETIME        NULL     COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警消息中心';
