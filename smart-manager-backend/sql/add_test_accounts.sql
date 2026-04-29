-- 创建针对前端角色的测试账号补充脚本（旧库或未合并 init 时手工执行）。
-- 新库请直接导入 init.sql（已包含 president / director_li / wangwu 及 indicator_sensitive）。
-- 密码均为 123456（与下方 BCrypt 一致）。
--
-- 【必看】执行前必须先选中「与应用相同的库」，本文件不再写死 USE。
-- 后端 application.yml 使用 DB_NAME（默认 smart_manager_db）；Railway MySQL 模板常见库名为 railway。
-- 若 admin 在库 A，你却 USE smart_manager_db / 连错库，三人会被插进库 B，查询库 A 仍只有 admin。
-- 在客户端先执行 SELECT DATABASE(); 或与 Railway Variables 里 MYSQLDATABASE / DB_NAME 对齐后再跑本脚本。

-- 插入角色定义（按 role_key 幂等；不写 indicator_sensitive 以兼容旧表结构）
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag)
VALUES
  ('院长', 'president', 3, '1', 1, 0),
  ('科室主任', 'director', 4, '3', 0, 1, 0)
ON DUPLICATE KEY UPDATE
  role_name = VALUES(role_name),
  role_sort = VALUES(role_sort),
  data_scope = VALUES(data_scope),
  status = VALUES(status),
  del_flag = VALUES(del_flag);

-- 如果不存在则插入用户, 若已存在则重置密码
INSERT INTO sys_user (username, password, real_name, dept_id, status)
VALUES
('president', '$2a$10$VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK', '王院长', 100, 1),
('director_li', '$2a$10$VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK', '李主任', 101, 1),
('wangwu', '$2a$10$VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK', '王五(普通中层)', 102, 1)
ON DUPLICATE KEY UPDATE
  password = VALUES(password),
  del_flag = 0,
  status = 1;

-- 建立用户-角色绑定（按 role_key 取真实 role_id，避免线上 role 表 id 与本地不一致）
DELETE ur FROM sys_user_role ur
INNER JOIN sys_user u ON u.id = ur.user_id
WHERE u.username IN ('president', 'director_li', 'wangwu');

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u JOIN sys_role r ON r.role_key = 'president' WHERE u.username = 'president';

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u JOIN sys_role r ON r.role_key = 'director' WHERE u.username = 'director_li';

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u JOIN sys_role r ON r.role_key = 'common' WHERE u.username = 'wangwu';
