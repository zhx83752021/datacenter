-- 仅在 sys_oper_log / sys_logininfor 为空时执行（否则会产生重复演示行）。
USE smart_manager_db;

UPDATE sys_role SET role_name = '普通角色' WHERE role_key = 'common';
UPDATE sys_user SET dept_id = 100 WHERE username = 'admin' AND dept_id IS NULL;

INSERT INTO sys_oper_log (title, business_type, request_method, operator_type, oper_name, oper_url, oper_ip, status, oper_time) VALUES
('用户管理', 2, 'PUT', 1, 'admin', '/api/system/user', '127.0.0.1', 0, NOW()),
('角色管理', 1, 'POST', 1, 'admin', '/api/system/role', '127.0.0.1', 0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('指标知识库', 5, 'GET', 1, 'admin', '/api/indicator/lib/list', '127.0.0.1', 0, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('登录认证', 0, 'POST', 1, 'admin', '/api/auth/login', '127.0.0.1', 0, DATE_SUB(NOW(), INTERVAL 3 HOUR));

INSERT INTO sys_logininfor (user_name, ipaddr, login_location, browser, os, status, msg, login_time) VALUES
('admin', '127.0.0.1', '内网', 'Chrome', 'Windows', '0', '登录成功', NOW()),
('admin', '127.0.0.1', '内网', 'Chrome', 'Windows', '0', '登录成功', DATE_SUB(NOW(), INTERVAL 1 DAY)),
('admin', '127.0.0.1', '内网', 'Chrome', 'Windows', '1', '密码错误', DATE_SUB(NOW(), INTERVAL 2 DAY));
