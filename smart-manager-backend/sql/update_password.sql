USE smart_manager_db;

-- 统一重置所有测试账号（包括 admin）的密码为 123456
UPDATE sys_user SET password = '$2a$10$VKFGYzOaecwPweFeyg8QPOJ6p9lTExlamYaM49a1kSo2L1yTBwkJK';
