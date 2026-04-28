-- 监控 / 指标模块所需最小表链（含 sm_indicator_lib → sm_alert_message）。
-- 适用于 Railway Query 网页不好跑完整 rebuild_tables.sql 时分段执行：可先执行本文件前半至 sm_indicator_value，再执行后半。
-- 执行后重启后端，以便 CommandLineRunner 补 ALTER 列。
USE smart_manager_db;

DROP TABLE IF EXISTS sm_indicator_category;
CREATE TABLE sm_indicator_category (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
  name VARCHAR(100) NOT NULL COMMENT '分类名称',
  code VARCHAR(50) DEFAULT NULL COMMENT '分类编码',
  order_num INT DEFAULT 0 COMMENT '显示顺序',
  status VARCHAR(10) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  del_flag VARCHAR(10) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标分类表';

DROP TABLE IF EXISTS sm_indicator_lib;
CREATE TABLE sm_indicator_lib (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  category_id BIGINT DEFAULT NULL COMMENT '分类ID',
  name VARCHAR(200) NOT NULL COMMENT '指标名称',
  code VARCHAR(50) NOT NULL COMMENT '指标编码',
  description TEXT COMMENT '指标描述/定义',
  unit VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
  frequency VARCHAR(20) DEFAULT NULL COMMENT '统计频率（D:日, W:周, M:月, Q:季, Y:年）',
  data_source VARCHAR(500) DEFAULT NULL COMMENT '数据来源（System:系统自动, Manual:人工填报）',
  is_sensitive INT DEFAULT 0 COMMENT '是否敏感指标（0:否, 1:是）',
  status VARCHAR(10) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  del_flag VARCHAR(10) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_code (code),
  INDEX idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标知识库表';

DROP TABLE IF EXISTS sm_indicator_value;
CREATE TABLE sm_indicator_value (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  indicator_id BIGINT NOT NULL COMMENT '指标ID',
  dept_id BIGINT DEFAULT NULL COMMENT '部门ID (用于权限过滤)',
  dept_code VARCHAR(50) DEFAULT NULL COMMENT '科室编码（全院级为ALL或空）',
  period_date VARCHAR(20) NOT NULL COMMENT '统计周期',
  value DECIMAL(18,4) DEFAULT NULL COMMENT '指标值',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_indicator (indicator_id),
  INDEX idx_period (period_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标数据值表';

DROP TABLE IF EXISTS sm_target_config;
CREATE TABLE sm_target_config (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  indicator_id BIGINT NOT NULL COMMENT '指标ID',
  period VARCHAR(20) DEFAULT NULL COMMENT '目标年度/期间',
  dept_code VARCHAR(50) DEFAULT NULL COMMENT '适用科室（空为全院）',
  target_value DECIMAL(18,4) DEFAULT NULL COMMENT '目标值',
  challenge_value DECIMAL(18,4) DEFAULT NULL COMMENT '挑战值',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  INDEX idx_indicator (indicator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标目标配置表';

DROP TABLE IF EXISTS sm_alert_rule;
CREATE TABLE sm_alert_rule (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  indicator_id BIGINT NOT NULL COMMENT '指标ID',
  rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
  operator VARCHAR(50) NOT NULL COMMENT '比较操作符:GT,LT,GE,LE,EQ',
  threshold DECIMAL(18,4) DEFAULT NULL COMMENT '阈值',
  level INT DEFAULT 1 COMMENT '预警等级:1普通,2严重,3紧急',
  status INT DEFAULT 1 COMMENT '状态:0停用,1启用',
  message_template VARCHAR(200) DEFAULT NULL COMMENT '消息模板',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  INDEX idx_indicator (indicator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警规则表';

DROP TABLE IF EXISTS sm_alert_message;
CREATE TABLE sm_alert_message (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  indicator_id BIGINT DEFAULT NULL COMMENT '指标ID',
  rule_id BIGINT DEFAULT NULL COMMENT '触发规则ID',
  level INT DEFAULT 1 COMMENT '预警等级',
  content TEXT COMMENT '消息内容',
  status INT DEFAULT 0 COMMENT '状态:0未读,1已读',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_indicator (indicator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警消息表';
