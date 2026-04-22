-- =================================================================================
-- 指标合规性改造 初始化脚本
-- Phase 1: 指标分类体系重构（新增二级分类）
-- Phase 2: 35项医疗质量安全核心制度监测指标入库 + Mock指标正式化
-- 依据: 《医疗质量安全核心制度落实情况监测指标（2025年版）》
-- 日期: 2026-03-06
-- =================================================================================

USE smart_manager_db;

-- ─────────────────────────────────────────────────────────
-- Phase 1: sm_indicator_lib 表结构增强
-- ─────────────────────────────────────────────────────────

-- 新增政策来源字段
ALTER TABLE sm_indicator_lib ADD COLUMN policy_source VARCHAR(200) DEFAULT NULL COMMENT '政策依据(如: 2025版核心制度指标-指标一)';
-- 新增核心制度标识
ALTER TABLE sm_indicator_lib ADD COLUMN core_system VARCHAR(100) DEFAULT NULL COMMENT '核心制度归属(如: 首诊负责制、三级查房制度)';
-- 新增分子/分母指标编码
ALTER TABLE sm_indicator_lib ADD COLUMN numerator_code VARCHAR(50) DEFAULT NULL COMMENT '分子指标编码';
ALTER TABLE sm_indicator_lib ADD COLUMN denominator_code VARCHAR(50) DEFAULT NULL COMMENT '分母指标编码';

-- ─────────────────────────────────────────────────────────
-- Phase 1: 指标分类体系重构（新增二级分类）
-- 假设原有4条一级分类: 1=经济运行, 2=产出效率, 3=质量安全, 4=实时态势
-- ─────────────────────────────────────────────────────────

-- 新增一级分类: 服务评价
INSERT IGNORE INTO sm_indicator_category (id, parent_id, name, code, order_num, status, create_by, create_time) VALUES
(5, 0, '服务评价', 'SERVICE', 5, '0', 'admin', NOW());

-- 经济运行 (parent=1) 二级分类
INSERT IGNORE INTO sm_indicator_category (id, parent_id, name, code, order_num, status, create_by, create_time) VALUES
(101, 1, '总体收入', 'ECO_INCOME', 1, '0', 'admin', NOW()),
(102, 1, '门诊经济', 'ECO_OUTPATIENT', 2, '0', 'admin', NOW()),
(103, 1, '住院经济', 'ECO_INPATIENT', 3, '0', 'admin', NOW());

-- 产出效率 (parent=2) 二级分类
INSERT IGNORE INTO sm_indicator_category (id, parent_id, name, code, order_num, status, create_by, create_time) VALUES
(201, 2, '门急诊效率', 'EFF_OUTPATIENT', 1, '0', 'admin', NOW()),
(202, 2, '住院效率', 'EFF_INPATIENT', 2, '0', 'admin', NOW()),
(203, 2, '床位效率', 'EFF_BED', 3, '0', 'admin', NOW());

-- 质量安全 (parent=3) 二级分类（对应核心制度）
INSERT IGNORE INTO sm_indicator_category (id, parent_id, name, code, order_num, status, create_by, create_time) VALUES
(301, 3, '首诊负责', 'QUA_FIRST_DIAG', 1, '0', 'admin', NOW()),
(302, 3, '三级查房', 'QUA_WARD_ROUND', 2, '0', 'admin', NOW()),
(303, 3, '会诊管理', 'QUA_CONSULTATION', 3, '0', 'admin', NOW()),
(304, 3, '手术管理', 'QUA_SURGERY', 4, '0', 'admin', NOW()),
(305, 3, '分级护理', 'QUA_NURSING', 5, '0', 'admin', NOW()),
(306, 3, '值班交接班', 'QUA_HANDOVER', 6, '0', 'admin', NOW()),
(307, 3, '疑难病例讨论', 'QUA_DIFFICULT_CASE', 7, '0', 'admin', NOW()),
(308, 3, '急危重症抢救', 'QUA_EMERGENCY', 8, '0', 'admin', NOW()),
(309, 3, '术前讨论', 'QUA_PREOP_DISCUSS', 9, '0', 'admin', NOW()),
(310, 3, '死亡病例讨论', 'QUA_DEATH_DISCUSS', 10, '0', 'admin', NOW()),
(311, 3, '查对制度', 'QUA_CHECK', 11, '0', 'admin', NOW()),
(312, 3, '手术安全核查', 'QUA_SURGERY_CHECK', 12, '0', 'admin', NOW()),
(313, 3, '手术分级管理', 'QUA_SURGERY_GRADE', 13, '0', 'admin', NOW()),
(314, 3, '新技术准入', 'QUA_NEW_TECH', 14, '0', 'admin', NOW()),
(315, 3, '危急值报告', 'QUA_CRITICAL_VALUE', 15, '0', 'admin', NOW()),
(316, 3, '抗菌药物管理', 'QUA_ANTIBIOTICS', 16, '0', 'admin', NOW()),
(317, 3, '临床用血管理', 'QUA_BLOOD_USE', 17, '0', 'admin', NOW()),
(318, 3, '感染控制', 'QUA_INFECTION', 18, '0', 'admin', NOW()),
(319, 3, '合理用药', 'QUA_DRUG_USE', 19, '0', 'admin', NOW());

-- 服务评价 (parent=5) 二级分类
INSERT IGNORE INTO sm_indicator_category (id, parent_id, name, code, order_num, status, create_by, create_time) VALUES
(501, 5, '患者满意度', 'SVC_SATISFACTION', 1, '0', 'admin', NOW()),
(502, 5, '投诉管理', 'SVC_COMPLAINT', 2, '0', 'admin', NOW());

-- 实时态势 (parent=4) 二级分类
INSERT IGNORE INTO sm_indicator_category (id, parent_id, name, code, order_num, status, create_by, create_time) VALUES
(401, 4, '实时监控', 'RT_MONITOR', 1, '0', 'admin', NOW());

-- ─────────────────────────────────────────────────────────
-- Phase 2: 35项医疗质量安全核心制度监测指标入库
-- 编码规范: {领域3字母}{3位序号}, 如 QUA001
-- ─────────────────────────────────────────────────────────

-- 先更新已有指标的编码为规范编码 (仅更新 code, 不动 id)
UPDATE sm_indicator_lib SET code='ECO001', policy_source='运营管理指标' WHERE code='INCOME_TOTAL';
UPDATE sm_indicator_lib SET code='ECO002', policy_source='运营管理指标' WHERE code='INCOME_MED';
UPDATE sm_indicator_lib SET code='ECO003', policy_source='运营管理指标' WHERE code='INCOME_DRUG';
UPDATE sm_indicator_lib SET code='ECO004', policy_source='运营管理指标' WHERE code='INCOME_OTHER';
UPDATE sm_indicator_lib SET code='EFF001', policy_source='运营管理指标' WHERE code='OUTPATIENT_COUNT';
UPDATE sm_indicator_lib SET code='EFF002', policy_source='运营管理指标' WHERE code='INPATIENT_COUNT';
UPDATE sm_indicator_lib SET code='QUA_D01', policy_source='运营管理指标', core_system='合理用药' WHERE code='DRUG_RATIO';

-- ─────────────────────────────────────────────────────────
-- 35项国家核心制度监测指标
-- ─────────────────────────────────────────────────────────

-- === 指标一: 首诊负责制 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(301, '患者入院48h内转科比例', 'QUA001',
 '入院48小时内转科患者人次数占同期入院患者总人次数的比例。不包括转入/转出ICU。',
 '%', 'M', 'HIS', 0, 1, '[QUA001_N] / [QUA001_D] * 100', '0',
 '2025版核心制度指标-指标一', '首诊负责制', 'QUA001_N', 'QUA001_D');

-- === 指标二~四: 三级查房制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(302, '患者入院8h内查房率', 'QUA002',
 '入院8小时内开具检查或治疗医嘱的患者人次数占同期入院患者总人次数的比例。',
 '%', 'M', 'EMR', 0, 1, '[QUA002_N] / [QUA002_D] * 100', '0',
 '2025版核心制度指标-指标二', '三级查房制度', 'QUA002_N', 'QUA002_D'),

(302, '上级医师查房记录规范率', 'QUA003',
 '住院患者病历中上级医师查房记录规范、完整的病例数量占同期住院患者病例总数量的比例。',
 '%', 'M', 'EMR', 0, 1, '[QUA003_N] / [QUA003_D] * 100', '0',
 '2025版核心制度指标-指标三', '三级查房制度', 'QUA003_N', 'QUA003_D'),

(302, '住院患者非计划手术率', 'QUA004',
 '行非计划手术的住院患者人次数占同期住院患者总人次数的比例。',
 '%', 'M', 'HIS', 0, 1, '[QUA004_N] / [QUA004_D] * 100', '0',
 '2025版核心制度指标-指标四', '三级查房制度', 'QUA004_N', 'QUA004_D');

-- === 指标五~八: 会诊制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(303, '急会诊及时到位率', 'QUA005',
 '急会诊请求发出后10分钟内到达现场的次数占同期急会诊总次数的比例。',
 '%', 'M', 'EMR', 0, 1, '[QUA005_N] / [QUA005_D] * 100', '0',
 '2025版核心制度指标-指标五', '会诊制度', 'QUA005_N', 'QUA005_D'),

(303, '急会诊有效率', 'QUA006',
 '急会诊后开具相关医嘱的次数占同期急会诊总次数的比例（40分钟内开具医嘱）。',
 '%', 'M', 'EMR', 0, 1, '[QUA006_N] / [QUA006_D] * 100', '0',
 '2025版核心制度指标-指标六', '会诊制度', 'QUA006_N', 'QUA006_D'),

(303, '普通会诊及时完成率', 'QUA007',
 '普通会诊24小时内完成次数占同期普通会诊总次数的比例。',
 '%', 'M', 'EMR', 0, 1, '[QUA007_N] / [QUA007_D] * 100', '0',
 '2025版核心制度指标-指标七', '会诊制度', 'QUA007_N', 'QUA007_D'),

(303, '普通会诊有效率', 'QUA008',
 '普通会诊结束后开具相关医嘱的次数占同期普通会诊总次数的比例（24小时内开具医嘱）。',
 '%', 'M', 'EMR', 0, 1, '[QUA008_N] / [QUA008_D] * 100', '0',
 '2025版核心制度指标-指标八', '会诊制度', 'QUA008_N', 'QUA008_D');

-- === 指标九: 分级护理制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(305, '手术患者特级/一级护理出院率', 'QUA009',
 '手术患者出院时为特级护理/一级护理级别的患者数量占同期手术患者总数量的比例。',
 '%', 'M', '护理系统', 0, 1, '[QUA009_N] / [QUA009_D] * 100', '0',
 '2025版核心制度指标-指标九', '分级护理制度', 'QUA009_N', 'QUA009_D');

-- === 指标十: 值班与交接班制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(306, '四级手术患者手术当日床旁交接班占比', 'QUA010',
 '四级手术患者手术当日进行床旁交接班的患者数量占同期四级手术患者总数量的比例。',
 '%', 'M', 'HIS', 0, 1, '[QUA010_N] / [QUA010_D] * 100', '0',
 '2025版核心制度指标-指标十', '值班与交接班制度', 'QUA010_N', 'QUA010_D');

-- === 指标十一~十三: 疑难病例讨论制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(307, '非计划再次住院/手术患者疑难病例讨论完成率', 'QUA011',
 '对非计划再次住院/手术患者进行疑难病例讨论的数量占同期非计划再次住院/手术的数量的比例。',
 '%', 'M', 'EMR', 0, 1, '[QUA011_N] / [QUA011_D] * 100', '0',
 '2025版核心制度指标-指标十一', '疑难病例讨论制度', 'QUA011_N', 'QUA011_D'),

(307, '非计划再次住院/手术患者疑难病例讨论记录完整率', 'QUA012',
 '讨论结论记入病历的数量占同期疑难病例讨论的数量的比例。',
 '%', 'M', 'EMR', 0, 1, '[QUA012_N] / [QUA012_D] * 100', '0',
 '2025版核心制度指标-指标十二', '疑难病例讨论制度', 'QUA012_N', 'QUA012_D'),

(307, '高额异常费用患者进行疑难病例讨论的占比', 'QUA013',
 '对产生高额异常费用(≥20万)患者进行疑难病例讨论的数量占同期高额异常费用患者数量的比例。',
 '%', 'Q', 'EMR', 0, 1, '[QUA013_N] / [QUA013_D] * 100', '0',
 '2025版核心制度指标-指标十三', '疑难病例讨论制度', 'QUA013_N', 'QUA013_D');

-- === 指标十四: 急危重症抢救制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(308, '急危重症患者抢救成功率', 'QUA014',
 '急危重症患者抢救成功的例次数占同期急危重症患者抢救的总例次数的比例。抢救成功指存活超过24小时或存活至下一次抢救开始。',
 '%', 'M', 'EMR', 0, 1, '[QUA014_N] / [QUA014_D] * 100', '0',
 '2025版核心制度指标-指标十四', '急危重症抢救制度', 'QUA014_N', 'QUA014_D');

-- === 指标十五~十八: 术前讨论制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(309, '术前讨论完成率', 'QUA015',
 '完成术前讨论的手术例数占同期手术总例数的比例。除急诊手术外，讨论完成时间应早于手术医嘱开具时间和手术同意书签署时间。',
 '%', 'M', '手术系统', 0, 1, '[QUA015_N] / [QUA015_D] * 100', '0',
 '2025版核心制度指标-指标十五', '术前讨论制度', 'QUA015_N', 'QUA015_D'),

(309, '术者参加术前讨论率', 'QUA016',
 '术者参加术前讨论的手术例数占同期进行术前讨论手术总例数的比例。',
 '%', 'M', '手术系统', 0, 1, '[QUA016_N] / [QUA016_D] * 100', '0',
 '2025版核心制度指标-指标十六', '术前讨论制度', 'QUA016_N', 'QUA016_D'),

(309, '术前讨论计划手术一致率', 'QUA017',
 '实际开展手术与术前讨论计划手术一致的手术例数占同期手术总例数的比例。',
 '%', 'M', '手术系统', 0, 1, '[QUA017_N] / [QUA017_D] * 100', '0',
 '2025版核心制度指标-指标十七', '术前讨论制度', 'QUA017_N', 'QUA017_D'),

(309, '实际手术术者与计划手术术者一致率', 'QUA018',
 '实际开展手术术者与计划手术术者一致的手术例数占同期手术总例数的比例。',
 '%', 'M', '手术系统', 0, 1, '[QUA018_N] / [QUA018_D] * 100', '0',
 '2025版核心制度指标-指标十八', '术前讨论制度', 'QUA018_N', 'QUA018_D');

-- === 指标十九~二十二: 死亡病例讨论制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(310, '死亡病例讨论5日完成率', 'QUA019',
 '患者死亡5个工作日内完成死亡病例讨论的病例数量占同期死亡病例总数量的比例。',
 '%', 'M', 'EMR', 0, 1, '[QUA019_N] / [QUA019_D] * 100', '0',
 '2025版核心制度指标-指标十九', '死亡病例讨论制度', 'QUA019_N', 'QUA019_D'),

(310, '医务部门组织讨论的死亡病例与发生纠纷的死亡病例比值', 'QUA020',
 '医务部门组织进行死亡病例讨论的病例数量与同期发生医疗纠纷的死亡病例数量的比值。',
 '比值', 'Q', 'EMR', 0, 1, '[QUA020_N] / [QUA020_D]', '0',
 '2025版核心制度指标-指标二十', '死亡病例讨论制度', 'QUA020_N', 'QUA020_D'),

(310, '科主任主持死亡病例讨论率', 'QUA021',
 '死亡病例讨论由科主任主持的病例数量占同期死亡病例总数量的比例。',
 '%', 'M', 'EMR', 0, 1, '[QUA021_N] / [QUA021_D] * 100', '0',
 '2025版核心制度指标-指标二十一', '死亡病例讨论制度', 'QUA021_N', 'QUA021_D'),

(310, '死亡患者病案上传率', 'QUA022',
 '按要求完整上传本机构死亡患者病案的数量占同期应上传数量的比例（HQMS反馈清单）。',
 '%', 'M', 'EMR', 0, 1, '[QUA022_N] / [QUA022_D] * 100', '0',
 '2025版核心制度指标-指标二十二', '死亡病例讨论制度', 'QUA022_N', 'QUA022_D');

-- === 指标二十三: 查对制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(311, '长期医嘱当日终止率', 'QUA023',
 '开具长期医嘱后当日终止执行的医嘱数量占同期开具长期医嘱总数量的比例。',
 '%', 'M', 'HIS', 0, 1, '[QUA023_N] / [QUA023_D] * 100', '0',
 '2025版核心制度指标-指标二十三', '查对制度', 'QUA023_N', 'QUA023_D');

-- === 指标二十四~二十五: 手术安全核查制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(312, '手术医师手术时间重合率', 'QUA024',
 '同一时间内手术医师为同一人的手术例数占同期住院患者手术总例数的比例。',
 '%', 'M', '手术系统', 0, 1, '[QUA024_N] / [QUA024_D] * 100', '0',
 '2025版核心制度指标-指标二十四', '手术安全核查制度', 'QUA024_N', 'QUA024_D'),

(312, '麻醉医师手术时间重合率', 'QUA025',
 '同一时间内手术麻醉医师为同一人的手术例数占同期住院患者手术总例数的比例。',
 '%', 'M', '手术系统', 0, 1, '[QUA025_N] / [QUA025_D] * 100', '0',
 '2025版核心制度指标-指标二十五', '手术安全核查制度', 'QUA025_N', 'QUA025_D');

-- === 指标二十六~二十九: 手术分级管理制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(313, '四级手术与三级手术并发症发生率比', 'QUA026',
 '四级手术并发症发生率与三级手术并发症发生率的比值。',
 '比值', 'Q', '手术系统', 0, 1, '[QUA026_N] / [QUA026_D]', '0',
 '2025版核心制度指标-指标二十六', '手术分级管理制度', 'QUA026_N', 'QUA026_D'),

(313, '四级手术与三级手术患者死亡率比', 'QUA027',
 '四级手术患者死亡率与三级手术患者死亡率的比值。',
 '比值', 'Q', '手术系统', 0, 1, '[QUA027_N] / [QUA027_D]', '0',
 '2025版核心制度指标-指标二十七', '手术分级管理制度', 'QUA027_N', 'QUA027_D'),

(313, '四级手术术前多学科讨论完成率', 'QUA028',
 '术前完成多学科讨论的四级手术例数占同期四级手术总例数的比例。含限制类技术。',
 '%', 'M', '手术系统', 0, 1, '[QUA028_N] / [QUA028_D] * 100', '0',
 '2025版核心制度指标-指标二十八', '手术分级管理制度', 'QUA028_N', 'QUA028_D'),

(313, '三四级手术实际开展率', 'QUA029',
 '实际开展的三、四级手术术种数占同期备案的三、四级手术术种数的比例。',
 '%', 'Q', '手术系统', 0, 1, '[QUA029_N] / [QUA029_D] * 100', '0',
 '2025版核心制度指标-指标二十九', '手术分级管理制度', 'QUA029_N', 'QUA029_D');

-- === 指标三十: 新技术新项目准入制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(314, '新技术新项目留存转化率', 'QUA030',
 '四年评估周期内，新增技术项目在第四年继续开展的比例。公式=(A+C+E)/(A+B+C+D+E+F)×100%。',
 '%', 'Y', '医务管理', 0, 1, '[QUA030_N] / [QUA030_D] * 100', '0',
 '2025版核心制度指标-指标三十', '新技术新项目准入制度', 'QUA030_N', 'QUA030_D');

-- === 指标三十一~三十二: 危急值报告制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(315, '危急值报告时间(中位数)', 'QUA031',
 '将出现危急值到临床科室获取危急值的时间，由长到短排序后取其中位数。分住院/门诊/急诊统计。',
 '分钟', 'M', 'LIS', 0, 0, NULL, '0',
 '2025版核心制度指标-指标三十一', '危急值报告制度', NULL, NULL),

(315, '住院患者危急值当日及时处置率', 'QUA032',
 '当日处置的住院患者危急值项目数占同期临床科室接获住院患者危急值项目数的比例。不含门急诊。',
 '%', 'M', 'LIS', 0, 1, '[QUA032_N] / [QUA032_D] * 100', '0',
 '2025版核心制度指标-指标三十二', '危急值报告制度', 'QUA032_N', 'QUA032_D');

-- === 指标三十三: 抗菌药物分级管理制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(316, '特殊使用级抗菌药物使用会诊率', 'QUA033',
 '特殊使用级抗菌药物使用医嘱与会诊记录相对应的医嘱数量占同期特殊使用级抗菌药物使用医嘱总数量的比例。',
 '%', 'M', '药品管理', 0, 1, '[QUA033_N] / [QUA033_D] * 100', '0',
 '2025版核心制度指标-指标三十三', '抗菌药物分级管理制度', 'QUA033_N', 'QUA033_D');

-- === 指标三十四~三十五: 临床用血审核制度 ===
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES
(317, '临床用血后评估记录率', 'QUA034',
 '输血治疗后规范书写评估输血记录例次数占同期临床输血治疗病例总例次数的比例。',
 '%', 'M', '输血管理', 0, 1, '[QUA034_N] / [QUA034_D] * 100', '0',
 '2025版核心制度指标-指标三十四', '临床用血审核制度', 'QUA034_N', 'QUA034_D'),

(317, '术中自体血回输率', 'QUA035',
 '术中使用自体血回输的患者数量占同期术中进行输血患者总数量的比例。',
 '%', 'M', '输血管理', 0, 1, '[QUA035_N] / [QUA035_D] * 100', '0',
 '2025版核心制度指标-指标三十五', '临床用血审核制度', 'QUA035_N', 'QUA035_D');

-- ─────────────────────────────────────────────────────────
-- Phase 2 附加: Mock指标正式化入库
-- ─────────────────────────────────────────────────────────

INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source) VALUES
(203, '床位使用率', 'EFF003', '实际占用总床日数/同期实际开放总床日数×100%', '%', 'M', 'HIS', 0, 1, '[EFF003_N] / [EFF003_D] * 100', '0', '运营管理指标'),
(203, '病床周转次数', 'EFF004', '出院患者总数/同期平均开放床位数', '次', 'M', 'HIS', 0, 1, '[EFF004_N] / [EFF004_D]', '0', '运营管理指标'),
(319, '耗占比', 'QUA_D02', '卫生材料收入/医疗收入×100%', '%', 'M', 'System', 0, 1, '[QUA_D02_N] / [QUA_D02_D] * 100', '0', '运营管理指标'),
(501, '患者满意度', 'SVC001', '满意度调查综合评分', '分', 'M', '满意度调查', 0, 0, NULL, '0', '服务评价指标'),
(102, '门诊收入', 'ECO005', '门诊业务收入合计', '万元', 'M', 'System', 1, 0, NULL, '0', '运营管理指标');
-- ─────────────────────────────────────────────────────────
-- Phase 2.5: 旧指标分类迁移 + 公式更新 + 补充RT_001
-- ─────────────────────────────────────────────────────────

-- 分类ID迁移: 旧一级分类 → 新二级分类
UPDATE sm_indicator_lib SET category_id=101 WHERE code IN ('ECO001','ECO002','ECO003','ECO004');
UPDATE sm_indicator_lib SET category_id=201 WHERE code IN ('EFF001','EFF002');
UPDATE sm_indicator_lib SET category_id=319 WHERE code='QUA_D01';

-- 更新旧指标的计算公式引用编码
UPDATE sm_indicator_lib SET calc_formula='[ECO002]' WHERE code='ECO001';
UPDATE sm_indicator_lib SET calc_formula='[ECO003] + [ECO004]' WHERE code='ECO002';
UPDATE sm_indicator_lib SET calc_formula='[ECO003] / [ECO002] * 100' WHERE code='QUA_D01';

-- 补充缺失指标: RT_001(当前在院人数)
INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source) VALUES
(401, '当前在院人数', 'RT_001', '当前时刻在院住院患者总人数', '人', 'D', 'HIS', 0, 0, NULL, '0', '运营管理指标');

-- ─────────────────────────────────────────────────────────
-- 完成提示
-- ─────────────────────────────────────────────────────────
-- 共计:
--   分类: 1个一级 + 24个二级 = 25条
--   指标: 35项核心制度 + 5项正式化 + 1项补充(RT_001) = 41条
--   字段: 4个新增字段 (policy_source, core_system, numerator_code, denominator_code)
--   编码迁移: 7项旧指标编码更新 + 3项分类迁移 + 3项公式更新
