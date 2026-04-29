-- =============================================================================
-- 修复：president / director_li / wangwu 在 sys_user_role 无记录 → 登录后全是 common 菜单
-- 在 Railway「与应用同一库」执行。先执行：SELECT DATABASE();
--
-- 若末尾校验仍为 0 行：常见原因是「写 sys_role 的 INSERT 失败」（例如旧表无
-- indicator_sensitive 列）。本脚本角色 INSERT 已改为不写该列。
-- =============================================================================

-- 0) 可选：补列（列已存在会报错，可忽略）
-- ALTER TABLE sys_role ADD COLUMN indicator_sensitive INT DEFAULT 0 COMMENT '敏感指标查看权限';

-- 1) 确认三个用户存在
-- SELECT id, username FROM sys_user WHERE username IN ('president','director_li','wangwu');

-- 2) 幂等写入三个角色（仅用常见列；有 indicator_sensitive 时用表默认值）
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag)
VALUES
  ('院长', 'president', 3, '1', 1, 0),
  ('科室主任', 'director', 4, '3', 0, 1, 0),
  ('普通角色', 'common', 2, '2', 0, 1, 0)
ON DUPLICATE KEY UPDATE
  role_name = VALUES(role_name),
  role_sort = VALUES(role_sort),
  data_scope = VALUES(data_scope),
  status = VALUES(status),
  del_flag = VALUES(del_flag);

-- 2b) 必须先看到 3 行；若为 0 行，看上一步是否报错
SELECT id, role_key, status, del_flag FROM sys_role WHERE role_key IN ('president', 'director', 'common');

-- 3) 清掉这三人的旧绑定
DELETE ur FROM sys_user_role ur
INNER JOIN sys_user u ON u.id = ur.user_id
WHERE u.username IN ('president', 'director_li', 'wangwu');

-- 4) 重新绑定
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u JOIN sys_role r ON r.role_key = 'president' WHERE u.username = 'president';

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u JOIN sys_role r ON r.role_key = 'director' WHERE u.username = 'director_li';

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u JOIN sys_role r ON r.role_key = 'common' WHERE u.username = 'wangwu';

-- 5) 校验：应 3 行
SELECT u.username, r.role_key, r.del_flag, r.status
FROM sys_user_role ur
JOIN sys_user u ON u.id = ur.user_id
JOIN sys_role r ON r.id = ur.role_id
WHERE u.username IN ('president', 'director_li', 'wangwu');
