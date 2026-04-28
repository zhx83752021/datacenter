-- 看板管理列表演示数据（与前端 /dashboard/list 常见演示一致）。表需已存在；先修 status 列长度以存 online/offline。
USE smart_manager_db;
ALTER TABLE sm_dashboard MODIFY COLUMN status VARCHAR(32) DEFAULT 'draft';
-- 若表中已有数据请先手动清空或使用迁移脚本（仅空表插入）。
INSERT INTO sm_dashboard (name, category, status, del_flag, create_by, create_time, update_time) VALUES
('院长驾驶舱', 'cockpit', 'online', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'),
('全景运营监控', 'monitor', 'online', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'),
('运营效率分析', 'theme', 'online', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'),
('科室驾驶舱', 'cockpit', 'online', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'),
('重点效率监控', 'monitor', 'offline', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'),
('月度运营报表', 'report', 'offline', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00');
