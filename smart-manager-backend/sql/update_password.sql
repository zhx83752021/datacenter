-- 先选中与后端 DB_NAME 一致的库再执行。

-- =============================================================================
-- ① 必跑：列太短会导致 LENGTH(password)=2（只剩「$2」），所有人登录失败
-- =============================================================================
ALTER TABLE sys_user MODIFY COLUMN password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)';

-- =============================================================================
-- ② 写密码：Railway 网页 Query 有的会吞 `$`，优先用下面「无 $ 字符串」的写法
--    明文密码仍为 123456；与方式三里整条字面量等价
-- =============================================================================
UPDATE sys_user
SET password = CONCAT(
        UNHEX('24326124313024'),
        'VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK');

-- =============================================================================
-- ③ 仅当你用本机 mysql 命令行且确认 `$` 不会被改时，可与②二选一
-- =============================================================================
-- UPDATE sys_user SET password = '$2a$10$VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK';

-- 演示账号状态
UPDATE sys_user SET del_flag = 0, status = 1
WHERE username IN ('admin', 'president', 'director_li', 'wangwu');

UPDATE sys_user SET del_flag = 0 WHERE del_flag IS NULL;
