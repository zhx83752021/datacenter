-- 角色乱码修复 + 日志/预警/目标演示数据（按需分段执行；导入时请确保客户端 UTF-8）。
SET NAMES utf8mb4;
USE smart_manager_db;

UPDATE sys_role SET role_name = '普通角色' WHERE role_key = 'common';

-- 仅在仍无登录记录时执行：
INSERT INTO sys_logininfor (user_name, ipaddr, status, msg, login_time) VALUES
('admin', '127.0.0.1', '0', '登录成功', NOW()),
('admin', '10.0.0.1', '0', '登录成功', DATE_SUB(NOW(), INTERVAL 1 DAY)),
('admin', '192.168.1.1', '0', '登录成功', DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO sys_oper_log (title, business_type, method, request_method, operator_type, oper_name, oper_url, status, oper_time) VALUES
('用户管理', 0, 'list', 'GET', 1, 'admin', '/api/system/user/list', 0, NOW()),
('角色管理', 0, 'list', 'GET', 1, 'admin', '/api/system/role/list', 0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('菜单查询', 0, 'list', 'GET', 1, 'admin', '/api/system/menu/list', 0, DATE_SUB(NOW(), INTERVAL 3 HOUR));

-- 需先存在 sm_indicator_lib 数据，将 1 换为 MIN(id)：
INSERT INTO sm_alert_rule (indicator_id, rule_name, operator, threshold, level, status, message_template) VALUES
(1, '默认阈值预警', 'GT', 80.0000, 1, 1, '指标超过阈值时请查看');
INSERT INTO sm_alert_message (indicator_id, rule_id, level, content, status, create_time) VALUES
(1, NULL, 1, '演示：请关注指标波动', 0, NOW());
INSERT INTO sm_target (indicator_id, target_value, unit, period_type, period_date, dept_code) VALUES
(1, 95.0000, '%', 'M', '2026-04', 'ALL');
INSERT INTO sm_target_config (indicator_id, period, dept_code, target_value, challenge_value, create_by) VALUES
(1, '2026', NULL, 90.0000, 95.0000, 'admin');
