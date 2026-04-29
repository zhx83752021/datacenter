-- =============================================================================
-- 修复：president / director_li / wangwu 在 sys_user_role 无记录 → 登录后全是 common 菜单
-- 在 Railway「与应用同一库」执行。先执行：SELECT DATABASE();
-- =============================================================================

-- 0) 若 sys_role 缺 indicator_sensitive，先取消下面注释执行一次
-- ALTER TABLE sys_role ADD COLUMN indicator_sensitive INT DEFAULT 0 COMMENT '敏感指标查看权限';

-- 1) 确认三个用户存在（0 行则先执行 add_test_accounts.sql 的用户插入段）
-- SELECT id, username FROM sys_user WHERE username IN ('president','director_li','wangwu');

-- 2) 按 role_key 幂等写入角色（不依赖固定 id；利用 role_key UNIQUE）
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, indicator_sensitive, status, del_flag)
VALUES
  ('院长', 'president', 3, '1', 1, 1, 0),
  ('科室主任', 'director', 4, '3', 0, 1, 0),
  ('普通角色', 'common', 2, '2', 0, 1, 0)
ON DUPLICATE KEY UPDATE
  role_name = VALUES(role_name),
  role_sort = VALUES(role_sort),
  data_scope = VALUES(data_scope),
  indicator_sensitive = VALUES(indicator_sensitive),
  status = VALUES(status),
  del_flag = VALUES(del_flag);

-- 3) 清掉这三人的旧绑定
DELETE ur FROM sys_user_role ur
INNER JOIN sys_user u ON u.id = ur.user_id
WHERE u.username IN ('president', 'director_li', 'wangwu');

-- 4) 重新绑定（按 username + role_key 找 id）
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u
JOIN sys_role r ON r.role_key = 'president'
WHERE u.username = 'president';

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u
JOIN sys_role r ON r.role_key = 'director'
WHERE u.username = 'director_li';

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u
JOIN sys_role r ON r.role_key = 'common'
WHERE u.username = 'wangwu';

-- 5) 校验：应返回 3 行，且 role_key 分别为 president / director / common
SELECT u.username, r.role_key, r.del_flag, r.status
FROM sys_user_role ur
JOIN sys_user u ON u.id = ur.user_id
JOIN sys_role r ON r.id = ur.role_id
WHERE u.username IN ('president', 'director_li', 'wangwu');
