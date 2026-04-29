-- =============================================================================
-- 修复演示账号角色绑定（Railway / 任意 MySQL）
-- 【重要】部分 Web 控制台一次只执行「第一条 SQL」。若粘贴整文件只看到一条
-- 结果，请按下面「=== 块」分段复制执行，每块执行完看输出/报错。
-- =============================================================================

-- === 块 D0：诊断（前三句可一起执行）===
-- 注意：Railway 部分界面会给 SQL 自动追加 LIMIT；MySQL 的 SHOW INDEX 不支持 LIMIT，
-- 若出现 ERROR 1064 near 'LIMIT 100'，请不要再执行 SHOW INDEX，改看下面 statistics 查询或跳过。
SELECT DATABASE() AS current_db;
SELECT COUNT(*) AS user_cnt FROM sys_user WHERE username IN ('president','director_li','wangwu');
SELECT COUNT(*) AS role_cnt FROM sys_role WHERE role_key IN ('president','director','common');
SELECT INDEX_NAME, COLUMN_NAME, SEQ_IN_INDEX
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_role' AND COLUMN_NAME = 'role_key'
ORDER BY INDEX_NAME, SEQ_IN_INDEX;

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

-- === 块 D4：写入三行绑定（必须用 INSERT…SELECT，勿用 VALUES：Railway 会给整句自动加
--     LIMIT 100；MySQL 不允许 INSERT…VALUES…LIMIT，会报 1064）===
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u JOIN sys_role r ON r.role_key = 'president' WHERE u.username = 'president';

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u JOIN sys_role r ON r.role_key = 'director' WHERE u.username = 'director_li';

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u JOIN sys_role r ON r.role_key = 'common' WHERE u.username = 'wangwu';

-- === 块 D5：校验（应 3 行）===
SELECT u.username, r.role_key, r.del_flag, r.status
FROM sys_user_role ur
JOIN sys_user u ON u.id = ur.user_id
JOIN sys_role r ON r.id = ur.role_id
WHERE u.username IN ('president', 'director_li', 'wangwu');
