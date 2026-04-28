-- =================================================================================
-- SMART MANAGER PLATFORM - V1.0 正式版数据库脚本
-- 生成日期: 2026-02-27
-- 说明: 整合所有核心模块（权限、指标引擎、报表、预警、日志、字典）
-- =================================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS smart_manager_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_manager_db;

-- ----------------------------
-- 1. 系统基础模块 (RBAC)
-- ----------------------------

-- 部门表
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
  id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '部门id',
  parent_id   BIGINT DEFAULT 0 COMMENT '父部门id',
  ancestors   VARCHAR(50) DEFAULT '' COMMENT '祖级列表',
  dept_name   VARCHAR(30) DEFAULT '' COMMENT '部门名称',
  order_num   INT DEFAULT 0 COMMENT '显示顺序',
  leader      VARCHAR(20) DEFAULT NULL COMMENT '负责人',
  phone       VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  email       VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
  status      VARCHAR(10) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  del_flag    VARCHAR(10) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  create_by   VARCHAR(50) DEFAULT NULL COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by   VARCHAR(50) DEFAULT NULL COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  username    VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
  real_name   VARCHAR(50) COMMENT '真实姓名',
  emp_no      VARCHAR(20) COMMENT '工号',
  dept_id     BIGINT COMMENT '部门ID',
  mobile      VARCHAR(20) COMMENT '手机号',
  email       VARCHAR(50) COMMENT '邮箱',
  avatar      VARCHAR(255) COMMENT '头像',
  status      INT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
  del_flag    INT DEFAULT 0 COMMENT '删除标志 0:正常 1:删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  role_name   VARCHAR(50) NOT NULL COMMENT '角色名称',
  role_key    VARCHAR(50) NOT NULL UNIQUE COMMENT '角色权限字符',
  role_sort   INT DEFAULT 0 COMMENT '显示顺序',
  data_scope  CHAR(1) DEFAULT '1' COMMENT '数据范围（1:全部 2:自定义 3:本部门 4:部门及以下 5:仅本人）',
  indicator_sensitive INT DEFAULT 0 COMMENT '敏感指标查看权限 (0:否, 1:是)',
  status      INT DEFAULT 1 COMMENT '角色状态（0停用 1正常）',
  del_flag    INT DEFAULT 0 COMMENT '删除标志（0代表存在 2代表删除）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark      VARCHAR(500) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 菜单权限表
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
  id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',
  menu_name   VARCHAR(50) NOT NULL COMMENT '菜单名称',
  parent_id   BIGINT DEFAULT 0 COMMENT '父菜单ID',
  order_num   INT DEFAULT 0 COMMENT '显示顺序',
  path        VARCHAR(200) DEFAULT '' COMMENT '路由地址',
  component   VARCHAR(255) DEFAULT '' COMMENT '组件路径',
  query       VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
  is_frame    INT DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  is_cache    INT DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  menu_type   CHAR(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  visible     CHAR(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  status      CHAR(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  perms       VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
  icon        VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark      VARCHAR(500) DEFAULT '' COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 用户和角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色和菜单关联表
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 2. 指标引擎模块
-- ----------------------------

-- 指标分类表
DROP TABLE IF EXISTS sm_indicator_category;
CREATE TABLE sm_indicator_category (
  id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  parent_id   BIGINT DEFAULT 0 COMMENT '父级ID',
  name        VARCHAR(100) NOT NULL COMMENT '分类名称',
  code        VARCHAR(50) DEFAULT NULL COMMENT '分类编码',
  order_num   INT DEFAULT 0 COMMENT '显示顺序',
  status      VARCHAR(10) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  del_flag    VARCHAR(10) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  create_by   VARCHAR(50) DEFAULT NULL COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by   VARCHAR(50) DEFAULT NULL COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标分类表';

-- 指标知识库表
DROP TABLE IF EXISTS sm_indicator_lib;
CREATE TABLE sm_indicator_lib (
  id           BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  category_id  BIGINT DEFAULT NULL COMMENT '分类ID',
  name         VARCHAR(200) NOT NULL COMMENT '指标名称',
  code         VARCHAR(50) NOT NULL UNIQUE COMMENT '指标编码',
  description  TEXT COMMENT '指标描述/定义',
  unit         VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
  frequency    VARCHAR(20) DEFAULT NULL COMMENT '统计频率（D:日, W:周, M:月, Q:季, Y:年）',
  data_source  VARCHAR(500) DEFAULT NULL COMMENT '数据来源（System:系统自动, Manual:人工填报）',
  is_composite INT DEFAULT 0 COMMENT '是否复合指标(0:否, 1:是)',
  calc_formula TEXT COMMENT '计算公式',
  is_sensitive INT DEFAULT 0 COMMENT '是否敏感指标（0:否, 1:是）',
  status       VARCHAR(10) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  del_flag     VARCHAR(10) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  create_by    VARCHAR(50) DEFAULT NULL COMMENT '创建者',
  create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by    VARCHAR(50) DEFAULT NULL COMMENT '更新者',
  update_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标知识库表';

-- 指标数据值表
DROP TABLE IF EXISTS sm_indicator_value;
CREATE TABLE sm_indicator_value (
  id           BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  indicator_id BIGINT NOT NULL COMMENT '指标ID',
  dept_id      BIGINT COMMENT '部门ID (用于数据权限过滤)',
  dept_code    VARCHAR(50) DEFAULT 'ALL' COMMENT '科室编码',
  period_date  VARCHAR(20) NOT NULL COMMENT '统计周期 (yyyy-MM 或 yyyy-ww)',
  value        DECIMAL(18,4) DEFAULT NULL COMMENT '指标值',
  create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_indicator (indicator_id),
  INDEX idx_period (period_date),
  INDEX idx_dept (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标数据值表';

-- 指标目标配置表
DROP TABLE IF EXISTS sm_target_config;
CREATE TABLE sm_target_config (
  id              BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  indicator_id    BIGINT NOT NULL COMMENT '指标ID',
  period          VARCHAR(20) DEFAULT NULL COMMENT '目标年度/期间',
  dept_id         BIGINT COMMENT '适用科室ID',
  dept_code       VARCHAR(50) DEFAULT NULL COMMENT '科室编码',
  target_value    DECIMAL(18,4) DEFAULT NULL COMMENT '目标值',
  challenge_value DECIMAL(18,4) DEFAULT NULL COMMENT '挑战值',
  create_by       VARCHAR(50) DEFAULT NULL COMMENT '创建者',
  create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标目标配置表';

-- ----------------------------
-- 3. 业务功能模块 (报表、预警、反馈、文件)
-- ----------------------------

-- 报表记录表
DROP TABLE IF EXISTS sm_report;
CREATE TABLE sm_report (
  id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  name        VARCHAR(255) NOT NULL COMMENT '报表名称',
  type        VARCHAR(64) NOT NULL COMMENT '报表类型',
  dept_id     BIGINT COMMENT '所属科室ID(用于数据权限)',
  dept        VARCHAR(128) NULL COMMENT '所属科室名称',
  status      VARCHAR(32) DEFAULT '草稿' COMMENT '状态',
  file_path   VARCHAR(512) NULL COMMENT '文件路径',
  del_flag    CHAR(1) DEFAULT '0' COMMENT '删除标志',
  create_by   VARCHAR(64) NULL COMMENT '生成人',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专项报表中心记录';

-- 预警规则表
DROP TABLE IF EXISTS sm_alert_rule;
CREATE TABLE sm_alert_rule (
  id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  indicator_id     BIGINT NOT NULL COMMENT '指标ID',
  rule_name        VARCHAR(100) NOT NULL COMMENT '规则名称',
  operator         VARCHAR(50) NOT NULL COMMENT '比较符',
  threshold        DECIMAL(18,4) DEFAULT NULL COMMENT '阈值',
  level            INT DEFAULT 1 COMMENT '预警等级:1-3',
  status           INT DEFAULT 1 COMMENT '状态:0停用,1启用',
  message_template VARCHAR(200) DEFAULT NULL COMMENT '消息模板',
  create_time      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警规则表';

-- 预警消息表
DROP TABLE IF EXISTS sm_alert_message;
CREATE TABLE sm_alert_message (
  id           BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  indicator_id BIGINT DEFAULT NULL COMMENT '指标ID',
  rule_id      BIGINT DEFAULT NULL COMMENT '触发规则ID',
  level        INT DEFAULT 1 COMMENT '预警等级',
  content      TEXT COMMENT '消息内容',
  status       INT DEFAULT 0 COMMENT '状态:0未读,1已读',
  create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警消息表';

-- 反馈中心
DROP TABLE IF EXISTS sm_feedback;
CREATE TABLE sm_feedback (
  id           BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  indicator_id BIGINT NULL COMMENT '关联指标ID',
  title        VARCHAR(200) COMMENT '反馈标题',
  content      TEXT NOT NULL COMMENT '反馈内容',
  type         VARCHAR(50) COMMENT '反馈类型',
  status       INT DEFAULT 0 COMMENT '0=待处理, 1=处理中, 3=已修复',
  create_by    VARCHAR(64) NULL COMMENT '上报人',
  processor    VARCHAR(64) NULL COMMENT '处理人',
  create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='意见反馈工单';

-- 文件存储表
DROP TABLE IF EXISTS sm_file;
CREATE TABLE sm_file (
  id            BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  original_name VARCHAR(255) NOT NULL COMMENT '文件名',
  storage_name  VARCHAR(100) COMMENT '存储名',
  path          VARCHAR(512) NOT NULL COMMENT '存储路径',
  size          BIGINT NULL COMMENT '文件大小',
  ext           VARCHAR(20) COMMENT '后缀',
  content_type  VARCHAR(100) COMMENT 'MIME类型',
  create_by     VARCHAR(64) NULL COMMENT '上传人',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件资源表';

-- ----------------------------
-- 4. 系统支撑模块 (字典、配置、日志)
-- ----------------------------

-- 字典类型表
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
  id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  dict_name   VARCHAR(100) NOT NULL COMMENT '字典名称',
  dict_type   VARCHAR(100) NOT NULL UNIQUE COMMENT '字典类型',
  status      VARCHAR(10) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_by   VARCHAR(50) DEFAULT NULL COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by   VARCHAR(50) DEFAULT NULL COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 字典数据表
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
  id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  dict_type   VARCHAR(100) NOT NULL COMMENT '字典类型',
  dict_label  VARCHAR(100) NOT NULL COMMENT '字典标签',
  dict_value  VARCHAR(100) NOT NULL COMMENT '字典键值',
  dict_sort   INT DEFAULT 0 COMMENT '排序',
  is_default  CHAR(1) DEFAULT 'N' COMMENT '是否默认',
  status      VARCHAR(10) DEFAULT '0' COMMENT '状态',
  remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- 操作日志表
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
  id             BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '日志主键',
  title          VARCHAR(50) DEFAULT '' COMMENT '模块标题',
  business_type  INT DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  method         VARCHAR(100) DEFAULT '' COMMENT '方法名称',
  request_method VARCHAR(10) DEFAULT '' COMMENT '请求方式',
  oper_name      VARCHAR(50) DEFAULT '' COMMENT '操作人员',
  dept_name      VARCHAR(50) DEFAULT '' COMMENT '部门名称',
  oper_url       VARCHAR(255) DEFAULT '' COMMENT '请求URL',
  oper_ip        VARCHAR(128) DEFAULT '' COMMENT '主机地址',
  oper_param     TEXT COMMENT '请求参数',
  json_result    TEXT COMMENT '返回参数',
  status         INT DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  error_msg      TEXT COMMENT '错误消息',
  oper_time      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录';

-- ----------------------------
-- 5. 初始基础数据
-- ----------------------------

-- 初始管理员账号（密码明文 123456）
INSERT INTO sys_user (id, username, password, real_name, status) VALUES
(1, 'admin', '$2a$10$VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK', '系统管理员', 1);

-- 初始角色
INSERT INTO sys_role (id, role_name, role_key, role_sort, data_scope, indicator_sensitive, status) VALUES
(1, '超级管理员', 'admin', 1, '1', 1, 1),
(2, '普通中层', 'common', 2, '2', 0, 1);

-- 初始关联
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 初始部门
INSERT INTO sys_dept (id, parent_id, dept_name, order_num) VALUES
(100, 0, '智慧管理中心', 0),
(101, 100, '信息科', 1),
(102, 100, '医务处', 2);

-- 初始指标分类
INSERT INTO sm_indicator_category (id, parent_id, name, code, order_num) VALUES
(1, 0, '医疗收入分析', 'cat_income', 1),
(2, 0, '手术中心分析', 'cat_surgery', 2);

SET FOREIGN_KEY_CHECKS = 1;
