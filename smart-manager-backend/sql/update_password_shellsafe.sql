-- 与 update_password.sql 等价，明文密码仍为 123456。
-- 不含字符 `$`，避免在部分平台（经 bash 执行 SQL）时 `$2a` 被吞掉导致库里只剩 "a0"。
USE smart_manager_db;

UPDATE sys_user
SET password = CONCAT(
        UNHEX('24326124313024'),
        'VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK');
