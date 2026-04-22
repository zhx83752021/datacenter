-- 清理并重新插入准确的看板演示数据
USE smart_manager_db;

TRUNCATE TABLE sm_dashboard;

INSERT INTO sm_dashboard
(name, category, description, status, url, create_by, publish_by, create_time, update_time, publish_time)
VALUES
('院长驾驶舱', 'cockpit', '全院核心指标、决策支持实时分析看板', 'online', '/cockpit', 'admin', 'admin', NOW(), NOW(), NOW()),
('全景运营监控', 'monitor', '实时监控全院运营状态，包含业务流量、预警提醒', 'online', '/monitor', 'admin', 'admin', NOW(), NOW(), NOW()),
('运营效率分析', 'theme', '针对医疗、服务、资源等维度的效率深度挖掘分析', 'online', '/analysis/theme', 'admin', 'admin', NOW(), NOW(), NOW()),
('月度运营报表', 'report', '系统自动生成的月度绩效与业务总结报表', 'draft', '/reports', 'admin', NULL, NOW(), NOW(), NULL),
('科室驾驶舱', 'cockpit', '多维度展示科室业务量、效率、质量与消耗情况', 'online', '/cockpit/dept', 'admin', 'admin', NOW(), NOW(), NOW()),
('重点效率监控', 'monitor', '专项监控重点环节时间轴及损耗分析', 'offline', '/analysis/theme', 'admin', NULL, NOW(), NOW(), NULL);
