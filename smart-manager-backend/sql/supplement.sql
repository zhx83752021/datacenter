-- 补充缺失的业务表 (兼容 MySQL 5.7)
USE smart_manager_db;

-- 指标分类表
DROP TABLE IF EXISTS sm_indicator_category;
CREATE TABLE sm_indicator_category (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
  category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
  category_code VARCHAR(50) DEFAULT NULL COMMENT '分类编码',
  level INT DEFAULT 1 COMMENT '层级',
  sort INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态:0停用,1启用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标分类表';

-- 指标知识库表
DROP TABLE IF EXISTS sm_indicator_lib;
CREATE TABLE sm_indicator_lib (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  code VARCHAR(50) NOT NULL COMMENT '指标编码',
  name_cn VARCHAR(200) NOT NULL COMMENT '中文名',
  name_en VARCHAR(200) DEFAULT NULL COMMENT '英文名',
  category_id BIGINT DEFAULT NULL COMMENT '分类ID',
  dept_id BIGINT DEFAULT NULL COMMENT '牵头科室ID',
  calc_formula TEXT COMMENT '计算公式',
  data_source VARCHAR(500) DEFAULT NULL COMMENT '数据来源',
  collect_cycle VARCHAR(20) DEFAULT NULL COMMENT '采集周期:DAY,MONTH,YEAR',
  target_type TINYINT DEFAULT NULL COMMENT '目标值类型:0越大越好,1越小越好,2区间',
  is_composite TINYINT DEFAULT 0 COMMENT '是否复合指标',
  unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
  description TEXT COMMENT '指标说明',
  status TINYINT DEFAULT 1 COMMENT '状态:0停用,1启用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_code (code),
  INDEX idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标知识库表';

-- 指标值表
DROP TABLE IF EXISTS sm_indicator_value;
CREATE TABLE sm_indicator_value (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  indicator_id BIGINT NOT NULL COMMENT '指标ID',
  dept_id BIGINT DEFAULT NULL COMMENT '科室ID,NULL表示全院',
  period_type VARCHAR(20) NOT NULL COMMENT '周期类型:DAY,MONTH,YEAR',
  period_value VARCHAR(20) NOT NULL COMMENT '周期值',
  measure_value DECIMAL(18,4) DEFAULT NULL COMMENT '指标值',
  yoy_rate DECIMAL(10,4) DEFAULT NULL COMMENT '同比',
  mom_rate DECIMAL(10,4) DEFAULT NULL COMMENT '环比',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_indicator_period (indicator_id, dept_id, period_type, period_value),
  INDEX idx_period (period_type, period_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标值表';

-- 目标与预警配置表
DROP TABLE IF EXISTS sm_target_config;
CREATE TABLE sm_target_config (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  indicator_id BIGINT NOT NULL COMMENT '指标ID',
  dept_id BIGINT DEFAULT NULL COMMENT '科室ID',
  period_type VARCHAR(20) DEFAULT NULL COMMENT '周期类型',
  target_val DECIMAL(18,4) DEFAULT NULL COMMENT '目标值',
  warn_min DECIMAL(18,4) DEFAULT NULL COMMENT '预警下限',
  warn_max DECIMAL(18,4) DEFAULT NULL COMMENT '预警上限',
  critical_min DECIMAL(18,4) DEFAULT NULL COMMENT '危急下限',
  critical_max DECIMAL(18,4) DEFAULT NULL COMMENT '危急上限',
  effective_date DATE DEFAULT NULL COMMENT '生效日期',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_target (indicator_id, dept_id, period_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='目标与预警配置表';

-- 预警规则表
DROP TABLE IF EXISTS sm_alert_rule;
CREATE TABLE sm_alert_rule (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  indicator_id BIGINT NOT NULL COMMENT '指标ID',
  rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
  rule_condition VARCHAR(50) NOT NULL COMMENT '条件:GT,LT,EQ,BETWEEN等',
  threshold_value VARCHAR(100) NOT NULL COMMENT '阈值',
  alert_level TINYINT DEFAULT 1 COMMENT '告警级别:1提醒,2警告,3严重',
  message_template VARCHAR(200) DEFAULT NULL COMMENT '消息模板',
  is_enabled TINYINT DEFAULT 1 COMMENT '是否启用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_indicator (indicator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警规则表';

-- 预警消息表
DROP TABLE IF EXISTS sm_alert_message;
CREATE TABLE sm_alert_message (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  rule_id BIGINT DEFAULT NULL COMMENT '规则ID',
  indicator_id BIGINT DEFAULT NULL COMMENT '指标ID',
  dept_id BIGINT DEFAULT NULL COMMENT '科室ID',
  alert_level TINYINT DEFAULT 1 COMMENT '告警级别',
  title VARCHAR(200) DEFAULT NULL COMMENT '告警标题',
  content TEXT COMMENT '告警内容',
  status TINYINT DEFAULT 0 COMMENT '状态:0未处理,1已处理,2已忽略',
  handler_id BIGINT DEFAULT NULL COMMENT '处理人ID',
  handle_time DATETIME DEFAULT NULL COMMENT '处理时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_status (status),
  INDEX idx_indicator (indicator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警消息表';

-- 字典类型表
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  dict_name VARCHAR(100) NOT NULL COMMENT '字典名称',
  dict_type VARCHAR(100) NOT NULL COMMENT '字典类型',
  status TINYINT DEFAULT 1 COMMENT '状态:0停用,1启用',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 字典数据表
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  dict_sort INT DEFAULT 0 COMMENT '字典排序',
  dict_label VARCHAR(100) NOT NULL COMMENT '字典标签',
  dict_value VARCHAR(100) NOT NULL COMMENT '字典键值',
  dict_type VARCHAR(100) NOT NULL COMMENT '字典类型',
  css_class VARCHAR(100) DEFAULT NULL COMMENT '样式属性',
  list_class VARCHAR(100) DEFAULT NULL COMMENT '表格回显样式',
  is_default CHAR(1) DEFAULT 'N' COMMENT '是否默认(Y是 N否)',
  status TINYINT DEFAULT 1 COMMENT '状态:0停用,1启用',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  INDEX idx_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';
