-- =================================================================================
-- SMART MANAGER PLATFORM - V1.0 BASELINE DATABASE SCRIPT
-- DATE: 2026-02-27
-- DESCRIPTION: Integrates all modules into a single enterprise-ready schema.
-- =================================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS smart_manager_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_manager_db;

-- ----------------------------
-- 1. SYSTEM INFRASTRUCTURE (RBAC)
-- ----------------------------

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
  real_name VARCHAR(50) COMMENT '真实姓名',
  dept_id BIGINT COMMENT '部门ID',
  mobile VARCHAR(20) COMMENT '手机号',
  avatar VARCHAR(255) COMMENT '头像',
  status INT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
  del_flag INT DEFAULT 0 COMMENT '删除标志 0:正常 1:删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 初始管理员 (密码: password)
INSERT INTO sys_user (id, username, password, real_name, status) VALUES
(1, 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '系统管理员', 1);

-- 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
  role_key VARCHAR(50) NOT NULL UNIQUE COMMENT '角色权限字符',
  role_sort INT DEFAULT 0 COMMENT '显示顺序',
  data_scope CHAR(1) DEFAULT '1' COMMENT '数据范围（1:全部 2:自定义 3:本部门 4:部门及以下 5:仅本人）',
  status INT DEFAULT 1 COMMENT '状态',
  del_flag INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  remark VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

INSERT INTO sys_role (id, role_name, role_key, role_sort, data_scope) VALUES
(1, '超级管理员', 'admin', 1, '1'),
(2, '普通中层', 'common', 2, '2');

-- 部门表
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  parent_id BIGINT DEFAULT 0,
  ancestors VARCHAR(50) DEFAULT '',
  dept_name VARCHAR(50) NOT NULL,
  order_num INT DEFAULT 0,
  status CHAR(1) DEFAULT '0',
  del_flag CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

INSERT INTO sys_dept (id, parent_id, dept_name, order_num) VALUES
(100, 0, '智慧管理中心', 0),
(101, 100, '信息科', 1),
(102, 100, '医务处', 2);

-- ----------------------------
-- 2. INDICATOR ENGINE (CORE BUSINESS)
-- ----------------------------

-- 指标分类
DROP TABLE IF EXISTS sm_indicator_category;
CREATE TABLE sm_indicator_category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  parent_id BIGINT DEFAULT 0,
  name VARCHAR(100) NOT NULL,
  code VARCHAR(50),
  order_num INT DEFAULT 0,
  status VARCHAR(10) DEFAULT '0',
  del_flag VARCHAR(10) DEFAULT '0',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标分类';

INSERT INTO sm_indicator_category (id, parent_id, name, code, order_num) VALUES
(1, 0, '医疗收入分析', 'cat_income', 1),
(2, 0, '手术中心分析', 'cat_surgery', 2),
(3, 0, '资源消耗监测', 'cat_resource', 3);

-- 指标知识库 (核心库)
DROP TABLE IF EXISTS sm_indicator_lib;
CREATE TABLE sm_indicator_lib (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_id BIGINT,
  name VARCHAR(200) NOT NULL,
  code VARCHAR(50) NOT NULL UNIQUE,
  description TEXT,
  unit VARCHAR(20),
  frequency VARCHAR(20),
  data_source VARCHAR(100),
  is_composite INT DEFAULT 0 COMMENT '是否复合指标',
  calc_formula TEXT COMMENT '计算公式',
  is_sensitive INT DEFAULT 0,
  status VARCHAR(10) DEFAULT '0',
  del_flag VARCHAR(10) DEFAULT '0',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标知识库';

-- 指标数据值
DROP TABLE IF EXISTS sm_indicator_value;
CREATE TABLE sm_indicator_value (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  indicator_id BIGINT NOT NULL,
  dept_id BIGINT COMMENT '部门ID (用于权限过滤)',
  dept_code VARCHAR(50) DEFAULT 'ALL' COMMENT '科室编码 (兼容展示)',
  period_date VARCHAR(20) NOT NULL COMMENT 'yyyy-MM(月) 或 yyyy-ww(周)',
  value DECIMAL(18,4),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_indicator_period (indicator_id, period_date),
  INDEX idx_dept (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标数据值';

-- ----------------------------
-- 3. PERIPHERAL MODULES (REPORT, FILE, ALERT)
-- ----------------------------

-- 报表中心
DROP TABLE IF EXISTS sm_report;
CREATE TABLE sm_report (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  type VARCHAR(50),
  dept VARCHAR(50),
  status VARCHAR(50) DEFAULT '草稿',
  file_path VARCHAR(255),
  create_by VARCHAR(50),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag CHAR(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表中心';

-- 文件管理
DROP TABLE IF EXISTS sm_file;
CREATE TABLE sm_file (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  original_name VARCHAR(255),
  storage_name VARCHAR(100),
  path VARCHAR(255),
  size BIGINT,
  ext VARCHAR(20),
  content_type VARCHAR(100),
  create_by VARCHAR(50),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='附件资源表';

-- 预警规则
DROP TABLE IF EXISTS sm_alert_rule;
CREATE TABLE sm_alert_rule (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  indicator_id BIGINT NOT NULL,
  rule_name VARCHAR(100),
  operator VARCHAR(20),
  threshold DECIMAL(18,4),
  level INT DEFAULT 1,
  status INT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警规则';

-- 预警消息
DROP TABLE IF EXISTS sm_alert_message;
CREATE TABLE sm_alert_message (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  indicator_id BIGINT,
  rule_id BIGINT,
  level INT,
  content TEXT,
  status INT DEFAULT 0 COMMENT '0:未读 1:已读',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警消息';

-- 反馈中心
DROP TABLE IF EXISTS sm_feedback;
CREATE TABLE sm_feedback (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200),
  content TEXT,
  type VARCHAR(50),
  status VARCHAR(20) DEFAULT '待处理',
  create_by VARCHAR(50),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='意见反馈';

-- ----------------------------
-- 4. LOGS & AUDIT
-- ----------------------------

DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(50),
  business_type INT,
  method VARCHAR(100),
  request_method VARCHAR(10),
  oper_name VARCHAR(50),
  dept_name VARCHAR(50),
  oper_url VARCHAR(255),
  oper_ip VARCHAR(128),
  oper_param TEXT,
  json_result TEXT,
  status INT DEFAULT 0,
  error_msg TEXT,
  oper_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';

SET FOREIGN_KEY_CHECKS = 1;
