-- =============================================================================
-- 修复演示账号角色绑定（Railway / 任意 MySQL）
-- 【重要】部分 Web 控制台一次只执行「第一条 SQL」。若粘贴整文件只看到一条
-- 结果，请按下面「=== 块」分段复制执行，每块执行完看输出/报错。
-- =============================================================================

-- === 块 D0：诊断（先跑这个）===
SELECT DATABASE() AS current_db;
SELECT COUNT(*) AS user_cnt FROM sys_user WHERE username IN ('president','director_li','wangwu');
SELECT COUNT(*) AS role_cnt FROM sys_role WHERE role_key IN ('president','director','common');
SHOW INDEX FROM sys_role;

-- === 块 D1：若 role_cnt=0，执行下面三句（不存在才插入）===
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag)
SELECT '院长', 'president', 3, '1', 1, 0 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'president' LIMIT 1);

INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag)
SELECT '科室主任', 'director', 4, '3', 0, 1, 0 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'director' LIMIT 1);

INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag)
SELECT '普通角色', 'common', 2, '2', 0, 1, 0 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'common' LIMIT 1);

-- === 块 D2：必须为 3 行 ===
SELECT id, role_key, status, del_flag FROM sys_role WHERE role_key IN ('president','director','common');

-- === 块 D3：删旧绑定 ===
DELETE ur FROM sys_user_role ur
INNER JOIN sys_user u ON u.id = ur.user_id
WHERE u.username IN ('president', 'director_li', 'wangwu');

-- === 块 D4：一条 INSERT 写三行（子查询为 NULL 会报错，便于发现角色/用户缺失）===
INSERT INTO sys_user_role (user_id, role_id) VALUES
((SELECT id FROM sys_user WHERE username = 'president' LIMIT 1), (SELECT id FROM sys_role WHERE role_key = 'president' LIMIT 1)),
((SELECT id FROM sys_user WHERE username = 'director_li' LIMIT 1), (SELECT id FROM sys_role WHERE role_key = 'director' LIMIT 1)),
((SELECT id FROM sys_user WHERE username = 'wangwu' LIMIT 1), (SELECT id FROM sys_role WHERE role_key = 'common' LIMIT 1));

-- === 块 D5：校验（应 3 行）===
SELECT u.username, r.role_key, r.del_flag, r.status
FROM sys_user_role ur
JOIN sys_user u ON u.id = ur.user_id
JOIN sys_role r ON r.id = ur.role_id
WHERE u.username IN ('president', 'director_li', 'wangwu');
