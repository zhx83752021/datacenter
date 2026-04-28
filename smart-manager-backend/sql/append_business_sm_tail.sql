-- 当整段执行 rebuild_tables.sql 在平台里被截断/只跑了一半时，用本文件补建剩余 sm_* 表（不碰 sys_*）。
-- 与 rebuild_tables.sql 中自 sm_alert_message 起至 sm_dashboard 的片段一致。
USE smart_manager_db;

-- sm_alert_message
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

-- sm_report
DROP TABLE IF EXISTS sm_report;
CREATE TABLE sm_report (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  name VARCHAR(200) NOT NULL COMMENT '报表名称',
  type VARCHAR(50) DEFAULT NULL COMMENT '报表类型',
  dept VARCHAR(100) DEFAULT NULL COMMENT '所属科室名称',
  dept_id BIGINT DEFAULT NULL COMMENT '部门ID (用于权限过滤)',
  status VARCHAR(50) DEFAULT '草稿' COMMENT '状态',
  file_path VARCHAR(500) DEFAULT NULL COMMENT '文件路径',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志 (0正常 2删除)',
  create_by VARCHAR(64) DEFAULT NULL COMMENT '生成人',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专项报表中心记录表';

-- sm_target
DROP TABLE IF EXISTS sm_target;
CREATE TABLE sm_target (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  indicator_id BIGINT COMMENT '指标ID',
  target_value DECIMAL(18,4) COMMENT '目标值',
  unit VARCHAR(20) COMMENT '单位',
  period_type VARCHAR(20) COMMENT '周期类型',
  period_date VARCHAR(20) COMMENT '周期日期',
  dept_code VARCHAR(50) COMMENT '科室编码',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='看板指标目标设定表';

-- sm_file
DROP TABLE IF EXISTS sm_file;
CREATE TABLE sm_file (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  file_name VARCHAR(255) COMMENT '文件名',
  file_path VARCHAR(500) COMMENT '文件路径',
  file_size BIGINT COMMENT '文件大小',
  file_type VARCHAR(100) COMMENT '文件类型',
  module VARCHAR(100) COMMENT '所属模块',
  create_by VARCHAR(64) COMMENT '上传人',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统文件存储表';

-- sm_dashboard
DROP TABLE IF EXISTS sm_dashboard;
CREATE TABLE sm_dashboard (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) COMMENT '看板名称',
  category VARCHAR(50) COMMENT '看板分类',
  description VARCHAR(500) COMMENT '描述',
  layout_config TEXT COMMENT '布局配置',
  thumbnail VARCHAR(500) COMMENT '缩略图',
  status CHAR(1) DEFAULT '0' COMMENT '状态',
  is_template CHAR(1) DEFAULT '0' COMMENT '是否为模板',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
  create_by VARCHAR(64) COMMENT '创建人',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64) COMMENT '更新人',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='看板配置表';
