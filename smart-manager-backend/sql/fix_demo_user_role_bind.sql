-- 修复演示账号未出现在 sys_user_role 或 role_id 与当前 sys_role 不一致的问题。
-- 在与应用相同的库执行；可先 SELECT DATABASE(); 确认。
--
-- 绑定规则：按 username + role_key 解析真实 id，不依赖固定 2/3/4。

-- 确保角色行存在（与 add_test_accounts 一致）
INSERT IGNORE INTO sys_role (id, role_name, role_key, role_sort, data_scope, indicator_sensitive, status) VALUES
(2, '普通角色', 'common', 2, '2', 0, 1),
(3, '院长', 'president', 3, '1', 1, 1),
(4, '科室主任', 'director', 4, '3', 0, 1);

-- 去掉这三个用户旧绑定（避免重复主键）
DELETE ur FROM sys_user_role ur
INNER JOIN sys_user u ON u.id = ur.user_id
WHERE u.username IN ('president', 'director_li', 'wangwu');

-- 按角色关键字重新绑定（president / director / common）
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

-- 校验（应各 1 行）
-- SELECT u.username, r.role_key FROM sys_user_role ur
-- JOIN sys_user u ON u.id = ur.user_id JOIN sys_role r ON r.id = ur.role_id
-- WHERE u.username IN ('president','director_li','wangwu');
