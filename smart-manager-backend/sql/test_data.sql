-- =================================================================================
-- 模拟生产数据回填脚本 (2025-01 至 2026-02)
-- 用于验证同环比分析、趋势图及数据权限过滤
-- =================================================================================

USE smart_manager_db;

-- 1. 清理现有测试数据
DELETE FROM sm_indicator_value WHERE period_date LIKE '2025-%' OR period_date LIKE '2026-%';
DELETE FROM sm_indicator_lib WHERE code IN ('INCOME_TOTAL', 'INCOME_MED', 'INCOME_DRUG', 'INCOME_OTHER', 'OUTPATIENT_COUNT', 'INPATIENT_COUNT', 'DRUG_RATIO');

-- 2. 插入指标定义 (确保与逻辑对应)
INSERT INTO sm_indicator_lib (id, category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status) VALUES
(1, 1, '全院总收入', 'INCOME_TOTAL', '医院全口径收入', '元', 'M', 'System', 1, 1, '[INCOME_MED]', '0'),
(2, 1, '医疗收入', 'INCOME_MED', '业务医疗收入', '元', 'M', 'System', 1, 1, '[INCOME_DRUG] + [INCOME_OTHER]', '0'),
(3, 1, '药品收入', 'INCOME_DRUG', '各类药品销售收入', '元', 'M', 'System', 0, 0, NULL, '0'),
(4, 1, '其他收入', 'INCOME_OTHER', '非医疗类其他收入', '元', 'M', 'System', 0, 0, NULL, '0'),
(5, 2, '门诊总人次', 'OUTPATIENT_COUNT', '门急诊挂号人次', '人', 'D', 'System', 0, 0, NULL, '0'),
(6, 2, '住院人次', 'INPATIENT_COUNT', '入出院病人数量', '人', 'D', 'System', 0, 0, NULL, '0'),
(7, 3, '药占比', 'DRUG_RATIO', '药品收入占医疗收入比', '%', 'M', 'System', 0, 1, '[INCOME_DRUG] / [INCOME_MED] * 100', '0');

-- 3. 批量回填 2025 年数据 (基础指标)
-- 采用简单的规律性数值模拟真实波动
INSERT INTO sm_indicator_value (indicator_id, dept_id, dept_code, period_date, value) VALUES
-- 2025年 药品收入 (INCOME_DRUG, ID: 3)
(3, 102, 'YWK', '2025-01', 450000), (3, 102, 'YWK', '2025-02', 420000), (3, 102, 'YWK', '2025-03', 480000),
(3, 102, 'YWK', '2025-04', 460000), (3, 102, 'YWK', '2025-05', 470000), (3, 102, 'YWK', '2025-06', 490000),
(3, 102, 'YWK', '2025-07', 510000), (3, 102, 'YWK', '2025-08', 520000), (3, 102, 'YWK', '2025-09', 500000),
(3, 102, 'YWK', '2025-10', 530000), (3, 102, 'YWK', '2025-11', 540000), (3, 102, 'YWK', '2025-12', 560000),
-- 2025年 其他收入 (INCOME_OTHER, ID: 4)
(4, 101, 'XXK', '2025-01', 80000), (4, 101, 'XXK', '2025-02', 75000), (4, 101, 'XXK', '2025-03', 82000),
(4, 101, 'XXK', '2025-04', 85000), (4, 101, 'XXK', '2025-05', 88000), (4, 101, 'XXK', '2025-06', 90000),
(4, 101, 'XXK', '2025-07', 92000), (4, 101, 'XXK', '2025-08', 95000), (4, 101, 'XXK', '2025-09', 93000),
(4, 101, 'XXK', '2025-10', 98000), (4, 101, 'XXK', '2025-11', 105000), (4, 101, 'XXK', '2025-12', 110000),
-- 2025年 门诊总人次 (OUTPATIENT_COUNT, ID: 5)
(5, NULL, 'ALL', '2025-01', 10000), (5, NULL, 'ALL', '2025-02', 9500), (5, NULL, 'ALL', '2025-03', 11000),
(5, NULL, 'ALL', '2025-12', 12500);

-- 4. 插入 2026 年初对比数据 (验证同环比)
INSERT INTO sm_indicator_value (indicator_id, dept_id, dept_code, period_date, value) VALUES
-- 2026年 药品收入 (同比 2025-01 增长, 环比 2025-12 下降)
(3, 102, 'YWK', '2026-01', 500000), (3, 102, 'YWK', '2026-02', 550000),
-- 2026年 其他收入
(4, 101, 'XXK', '2026-01', 100000), (4, 101, 'XXK', '2026-02', 90000),
-- 2026年 门诊总人次
(5, NULL, 'ALL', '2026-01', 12000), (5, NULL, 'ALL', '2026-02', 13500);

-- 5. 为验证数据权限，插入一些特定科室的干扰数据 (测试 DataScope)
-- 例如：信息科 只能看 101 的数据，普通中层只能看到本科室数据
INSERT INTO sm_indicator_value (indicator_id, dept_id, dept_code, period_date, value) VALUES
(3, 101, 'XXK', '2026-02', 5000), -- 信息科偷偷录入的一笔药品费 (仅供测试过滤)
(5, 102, 'YWK', '2026-02', 800);  -- 医务科自己的门诊量
