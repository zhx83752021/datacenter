-- 创建针对前端角色的测试账号补充脚本（旧库或未合并 init 时手工执行）。
-- 新库请直接导入 init.sql（已包含 president / director_li / wangwu 及 indicator_sensitive）。
-- 密码均为 123456（与下方 BCrypt 一致）。
--
-- 【必看】执行前必须先选中「与应用相同的库」，本文件不再写死 USE。
-- 后端 application.yml 使用 DB_NAME（默认 smart_manager_db）；Railway MySQL 模板常见库名为 railway。
-- 若 admin 在库 A，你却 USE smart_manager_db / 连错库，三人会被插进库 B，查询库 A 仍只有 admin。
-- 在客户端先执行 SELECT DATABASE(); 或与 Railway Variables 里 MYSQLDATABASE / DB_NAME 对齐后再跑本脚本。

-- 插入角色定义
INSERT IGNORE INTO sys_role (id, role_name, role_key, role_sort, data_scope, indicator_sensitive, status) VALUES
(3, '院长', 'president', 3, '1', 1, 1),
(4, '科室主任', 'director', 4, '3', 0, 1);

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

-- 建立用户-角色绑定关系 (需要先找到对应生成的ID)
REPLACE INTO sys_user_role (user_id, role_id)
SELECT id, 3 FROM sys_user WHERE username = 'president';

REPLACE INTO sys_user_role (user_id, role_id)
SELECT id, 4 FROM sys_user WHERE username = 'director_li';

REPLACE INTO sys_user_role (user_id, role_id)
SELECT id, 2 FROM sys_user WHERE username = 'wangwu';
