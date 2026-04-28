-- 根据实体类精确重建所有业务表（兼容 MySQL 5.7）
USE smart_manager_db;

-- ============================================
-- sm_indicator_category: 对应 SmIndicatorCategory
-- 字段: id, parentId, name, code, orderNum, status, delFlag, createBy, createTime, updateBy, updateTime
-- ============================================
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

-- ============================================
-- sm_indicator_lib: 对应 SmIndicatorLib
-- 字段: id, categoryId, name, code, description, unit, frequency, dataSource, isSensitive, status, delFlag, createBy, createTime, updateBy, updateTime
-- ============================================
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

-- ============================================
-- sm_indicator_value: 对应 SmIndicatorValue
-- 字段: id, indicatorId, deptCode, periodDate, value, createTime
-- ============================================
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

-- ============================================
-- sm_target_config: 对应 SmTargetConfig
-- 字段: id, indicatorId, period, deptCode, targetValue, challengeValue, createBy, createTime, updateTime
-- ============================================
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

-- ============================================
-- sm_alert_rule: 对应 SmAlertRule
-- 字段: id, indicatorId, ruleName, operator, threshold, level, status, messageTemplate, createTime, updateTime
-- ============================================
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

-- ============================================
-- sm_alert_message: 对应 SmAlertMessage
-- 字段: id, indicatorId, ruleId, level, content, status, createTime
-- ============================================
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

-- ============================================
-- sys_dict_type: 对应 SysDictType
-- 字段: id, dictName, dictType, status, remark, createBy, createTime, updateBy, updateTime
-- ============================================
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  dict_name VARCHAR(100) NOT NULL COMMENT '字典名称',
  dict_type VARCHAR(100) NOT NULL COMMENT '字典类型',
  status VARCHAR(10) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ============================================
-- sys_dict_data: 对应 SysDictData
-- 字段: id, dictType, dictLabel, dictValue, dictSort, cssClass, listClass, isDefault, status, remark, createBy, createTime, updateBy, updateTime
-- ============================================
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  dict_type VARCHAR(100) NOT NULL COMMENT '字典类型',
  dict_label VARCHAR(100) NOT NULL COMMENT '字典标签',
  dict_value VARCHAR(100) NOT NULL COMMENT '字典键值',
  dict_sort INT DEFAULT 0 COMMENT '字典排序',
  css_class VARCHAR(100) DEFAULT NULL COMMENT '样式属性',
  list_class VARCHAR(100) DEFAULT NULL COMMENT '表格回显样式',
  is_default CHAR(1) DEFAULT 'N' COMMENT '是否默认(Y是 N否)',
  status VARCHAR(10) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  INDEX idx_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- ============================================
-- sys_oper_log: 重建以匹配 SysOperLog 实体
-- 字段: id, title, businessType, method, requestMethod, operatorType, operName, deptName, operUrl, operIp, operLocation, operParam, jsonResult, status, errorMsg, operTime
-- ============================================
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  title VARCHAR(50) DEFAULT '' COMMENT '模块标题',
  business_type INT DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  method VARCHAR(100) DEFAULT '' COMMENT '方法名称',
  request_method VARCHAR(10) DEFAULT '' COMMENT '请求方式',
  operator_type INT DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  oper_name VARCHAR(50) DEFAULT '' COMMENT '操作人员',
  dept_name VARCHAR(50) DEFAULT '' COMMENT '部门名称',
  oper_url VARCHAR(255) DEFAULT '' COMMENT '请求URL',
  oper_ip VARCHAR(128) DEFAULT '' COMMENT '主机地址',
  oper_location VARCHAR(255) DEFAULT '' COMMENT '操作地点',
  oper_param TEXT COMMENT '请求参数',
  json_result TEXT COMMENT '返回参数',
  status INT DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  error_msg VARCHAR(2000) DEFAULT '' COMMENT '错误消息',
  oper_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录';

 -- ============================================
 -- sm_report: 对应 SmReport
 -- 字段: id, name, type, dept, dept_id, status, file_path, del_flag, create_by, create_time, update_time
 -- ============================================
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

 -- ============================================
 -- sm_target: 对应 SmTarget
 -- ============================================
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

 -- ============================================
 -- sm_file: 对应 SmFile
 -- ============================================
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

-- ============================================
-- sm_dashboard: 对应 SmDashboard
-- ============================================
DROP TABLE IF EXISTS sm_dashboard;
CREATE TABLE sm_dashboard (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) COMMENT '看板名称',
  category VARCHAR(50) COMMENT '看板分类',
  description VARCHAR(500) COMMENT '描述',
  theme_id BIGINT NULL COMMENT '指标主题分类ID',
  publish_type VARCHAR(32) NULL COMMENT '发布对象类型',
  publish_target VARCHAR(500) NULL COMMENT '发布对象ID',
  url VARCHAR(1000) NULL COMMENT '看板地址',
  layout_config TEXT COMMENT '布局配置',
  thumbnail VARCHAR(500) COMMENT '缩略图',
  status VARCHAR(32) DEFAULT 'draft' COMMENT '状态',
  is_template CHAR(1) DEFAULT '0' COMMENT '是否为模板',
  publish_by VARCHAR(64) NULL COMMENT '发布人',
  publish_time DATETIME NULL COMMENT '发布时间',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
  create_by VARCHAR(64) COMMENT '创建人',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64) COMMENT '更新人',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='看板配置表';
