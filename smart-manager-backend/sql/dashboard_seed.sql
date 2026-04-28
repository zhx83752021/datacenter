-- 看板管理列表演示数据。表需已存在；需已含 url 等列（见 patch_sm_dashboard_columns.sql）。
USE smart_manager_db;
ALTER TABLE sm_dashboard MODIFY COLUMN status VARCHAR(32) DEFAULT 'draft';
-- 若表中已有数据请先手动清空（仅空表插入下一行）。
INSERT INTO sm_dashboard (name, category, url, status, del_flag, create_by, create_time, update_time) VALUES
('院长驾驶舱', 'cockpit', '/cockpit', 'online', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'),
('全景运营监控', 'monitor', '/monitor', 'online', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'),
('运营效率分析', 'theme', '/portal', 'online', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'),
('科室驾驶舱', 'cockpit', '/cockpit', 'online', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'),
('重点效率监控', 'monitor', '/monitor', 'offline', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'),
('月度运营报表', 'report', '/reports', 'offline', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00');
-- 已存在但缺 url 的看板可补：
UPDATE sm_dashboard SET url = CASE category
  WHEN 'cockpit' THEN '/cockpit'
  WHEN 'monitor' THEN '/monitor'
  WHEN 'theme' THEN '/portal'
  WHEN 'report' THEN '/reports'
  ELSE '/portal' END
WHERE (url IS NULL OR url = '') AND del_flag = '0';
