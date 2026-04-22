-- 重建核心系统表以匹配 Java 实体类
USE smart_manager_db;

-- ============================================
-- sys_user: 对应 SysUser
-- ============================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
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

-- 初始管理员账号 (密码是 password 的 BCrypt 加密值)
INSERT INTO sys_user (id, username, password, real_name, status) VALUES
(1, 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '系统管理员', 1);

-- ============================================
-- sys_role: 对应 SysRole
-- ============================================
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
  role_key VARCHAR(50) NOT NULL UNIQUE COMMENT '角色权限字符',
  role_sort INT DEFAULT 0 COMMENT '显示顺序',
  data_scope CHAR(1) DEFAULT '1' COMMENT '数据范围',
  indicator_sensitive INT DEFAULT 0 COMMENT '敏感指标查看权限 (0:否, 1:是)',
  status INT DEFAULT 1 COMMENT '角色状态（0停用 1正常）',
  del_flag INT DEFAULT 0 COMMENT '删除标志（0代表存在 2代表删除）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

INSERT INTO sys_role (id, role_name, role_key, role_sort, data_scope, status) VALUES
(1, '超级管理员', 'admin', 1, '1', 1),
(2, '普通角色', 'common', 2, '2', 1);

-- ============================================
-- sys_menu: 对应 SysMenu
-- ============================================
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',
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

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon) VALUES
(1, '系统管理', 0, 1, 'system', NULL, 'M', '', 'system'),
(2, '用户管理', 1, 1, 'user', 'system/user/index', 'C', 'system:user:list', 'user'),
(3, '角色管理', 1, 2, 'role', 'system/role/index', 'C', 'system:role:list', 'peoples'),
(4, '菜单管理', 1, 3, 'menu', 'system/menu/index', 'C', 'system:menu:list', 'tree-table');

-- ============================================
-- sys_dept: 对应 SysDept
-- ============================================
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '部门id',
  parent_id BIGINT DEFAULT 0 COMMENT '父部门id',
  ancestors VARCHAR(50) DEFAULT '' COMMENT '祖级列表',
  dept_name VARCHAR(30) DEFAULT '' COMMENT '部门名称',
  order_num INT DEFAULT 0 COMMENT '显示顺序',
  leader VARCHAR(20) DEFAULT NULL COMMENT '负责人',
  phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  email VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
  status VARCHAR(10) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  del_flag VARCHAR(10) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

INSERT INTO sys_dept (id, parent_id, dept_name, order_num) VALUES
(100, 0, '智慧管理中心', 0),
(101, 100, '信息科', 1),
(102, 100, '医务处', 2);

-- ============================================
-- sys_user_role & sys_role_menu (关联表)
-- ============================================
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4);
