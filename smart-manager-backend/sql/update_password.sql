USE smart_manager_db;

-- ---------------------------------------------------------------------------
-- 方式一：直接 BCrypt 哈希（推荐在 mysql 客户端、IDE 中执行）
-- 统一重置所有测试账号（包括 admin）的密码为 123456
-- ---------------------------------------------------------------------------
UPDATE sys_user SET password = '$2a$10$VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK';

-- ---------------------------------------------------------------------------
-- 方式二：经 bash 等传参时，若 `$2a` 被错误展开，可改用 CONCAT+UNHEX
-- 效果与方式一相同，明文密码仍为 123456
-- ---------------------------------------------------------------------------
-- UPDATE sys_user
-- SET password = CONCAT(
--         UNHEX('24326124313024'),
--         'VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK');
