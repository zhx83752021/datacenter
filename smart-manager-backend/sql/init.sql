-- Database initialization script
CREATE DATABASE IF NOT EXISTS smart_manager_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_manager_db;

-- 1. 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
  real_name VARCHAR(50) COMMENT '真实姓名',
  emp_no VARCHAR(20) COMMENT '工号',
  dept_id BIGINT COMMENT '部门ID',
  mobile VARCHAR(20) COMMENT '手机号',
  email VARCHAR(50) COMMENT '邮箱',
  avatar VARCHAR(255) COMMENT '头像',
  status INT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
  del_flag INT DEFAULT 0 COMMENT '删除标志 0:正常 1:删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- password: '123456'（与前端登录页提示、update_password.sql 一致）
INSERT INTO sys_user (id, username, password, real_name, status) VALUES
(1, 'admin', '$2a$10$VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK', '系统管理员', 1);


-- 2. 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
  role_key VARCHAR(50) NOT NULL UNIQUE COMMENT '角色权限字符',
  role_sort INT DEFAULT 0 COMMENT '显示顺序',
  data_scope CHAR(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限）',
  indicator_sensitive INT DEFAULT 0 COMMENT '敏感指标查看权限 (0:否, 1:是)',
  status INT DEFAULT 1 COMMENT '角色状态（0停用 1正常）',
  del_flag INT DEFAULT 0 COMMENT '删除标志（0代表存在 2代表删除）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

INSERT INTO sys_role (id, role_name, role_key, role_sort, data_scope, indicator_sensitive, status) VALUES
(1, '超级管理员', 'admin', 1, '1', 0, 1),
(2, '普通角色', 'common', 2, '2', 0, 1),
(3, '院长', 'president', 3, '1', 1, 1),
(4, '科室主任', 'director', 4, '3', 0, 1);


-- 3. 菜单权限表
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',
  menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
  parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
  order_num INT DEFAULT 0 COMMENT '显示顺序',
  path VARCHAR(200) DEFAULT '' COMMENT '路由地址',
  component VARCHAR(255) DEFAULT '' COMMENT '组件路径',
  query VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
  is_frame INT DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  is_cache INT DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  menu_type CHAR(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  visible CHAR(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  status CHAR(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  perms VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
  icon VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark VARCHAR(500) DEFAULT '' COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 初始菜单数据 (示例)
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon) VALUES
(1, '系统管理', 0, 1, 'system', NULL, 'M', '', 'system'),
(2, '用户管理', 1, 1, 'user', 'system/user/index', 'C', 'system:user:list', 'user'),
(3, '角色管理', 1, 2, 'role', 'system/role/index', 'C', 'system:role:list', 'peoples'),
(4, '菜单管理', 1, 3, 'menu', 'system/menu/index', 'C', 'system:menu:list', 'tree-table');


-- 4. 部门表
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '部门id',
  parent_id BIGINT DEFAULT 0 COMMENT '父部门id',
  ancestors VARCHAR(50) DEFAULT '' COMMENT '祖级列表',
  dept_name VARCHAR(30) DEFAULT '' COMMENT '部门名称',
  order_num INT DEFAULT 0 COMMENT '显示顺序',
  leader VARCHAR(20) DEFAULT NULL COMMENT '负责人',
  phone VARCHAR(11) DEFAULT NULL COMMENT '联系电话',
  email VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
  status CHAR(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

INSERT INTO sys_dept (id, parent_id, dept_name, order_num) VALUES
(100, 0, '智慧管理中心', 0),
(101, 100, '信息科', 1),
(102, 100, '医务处', 2);

-- 登录页展示的角色演示账号（密码均为 123456，BCrypt 与 admin 相同）
INSERT INTO sys_user (id, username, password, real_name, dept_id, status) VALUES
(2, 'president', '$2a$10$VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK', '王院长', 100, 1),
(3, 'director_li', '$2a$10$VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK', '李主任', 101, 1),
(4, 'wangwu', '$2a$10$VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK', '王五(普通中层)', 102, 1);

-- 5. 用户和角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 3),
(3, 4),
(4, 2);


-- 6. 角色和菜单关联表
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
  role_id BIGINT NOT NULL COMMENT '角色ID',
  menu_id BIGINT NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4);


-- 7. 操作日志表
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志主键',
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
  oper_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录';


-- 8. 登录日志表
DROP TABLE IF EXISTS sys_logininfor;
CREATE TABLE sys_logininfor (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '访问ID',
  user_name VARCHAR(50) DEFAULT '' COMMENT '用户账号',
  ipaddr VARCHAR(128) DEFAULT '' COMMENT '登录IP地址',
  login_location VARCHAR(255) DEFAULT '' COMMENT '登录地点',
  browser VARCHAR(50) DEFAULT '' COMMENT '浏览器类型',
  os VARCHAR(50) DEFAULT '' COMMENT '操作系统',
  status CHAR(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  msg VARCHAR(255) DEFAULT '' COMMENT '提示消息',
  login_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统访问记录';
