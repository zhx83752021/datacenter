package com.smart.manager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.smart.manager.mapper")
@EnableAsync
@EnableScheduling
public class SmartManagerBackendApplication {

        public static void main(String[] args) {
                SpringApplication.run(SmartManagerBackendApplication.class, args);
        }

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE + 1)
        public CommandLineRunner migrateDatabase(JdbcTemplate jdbcTemplate) {
                return args -> {
                        // --- 1. 结构变更与基础表 ---
                        try {
                                jdbcTemplate.execute(
                                                "ALTER TABLE sm_indicator_lib ADD COLUMN is_composite TINYINT(1) DEFAULT 0 COMMENT '是否复合指标'");
                        } catch (Exception ignored) {
                        }
                        try {
                                jdbcTemplate.execute(
                                                "ALTER TABLE sm_indicator_value ADD COLUMN dept_id BIGINT COMMENT '部门ID'");
                        } catch (Exception ignored) {
                        }
                        try {
                                jdbcTemplate.execute(
                                                "ALTER TABLE sm_indicator_lib ADD COLUMN calc_formula VARCHAR(255) DEFAULT NULL COMMENT '计算公式'");
                        } catch (Exception ignored) {
                        }
                        try {
                                jdbcTemplate.execute(
                                                "ALTER TABLE sys_role ADD COLUMN indicator_sensitive TINYINT(1) DEFAULT 0 COMMENT '敏感数据权限'");
                        } catch (Exception ignored) {
                        }
                        try {
                                jdbcTemplate.execute(
                                                "ALTER TABLE sys_user ADD COLUMN emp_no VARCHAR(50) DEFAULT NULL COMMENT '工号'");
                                jdbcTemplate.execute(
                                                "ALTER TABLE sys_user ADD COLUMN email VARCHAR(100) DEFAULT NULL COMMENT '邮箱'");
                        } catch (Exception ignored) {
                        }
                        try {
                                jdbcTemplate.execute(
                                                "CREATE TABLE IF NOT EXISTS sm_feedback (" +
                                                                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                                                                "indicator_id BIGINT, " +
                                                                "content TEXT, " +
                                                                "status INT DEFAULT 0, " +
                                                                "create_by VARCHAR(64), " +
                                                                "processor VARCHAR(64), " +
                                                                "result_msg VARCHAR(255), " +
                                                                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                                                "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                                                                +
                                                                ") COMMENT='指标反馈业务工单表'");
                        } catch (Exception ignored) {
                        }
                        try {
                                jdbcTemplate.execute(
                                                "CREATE TABLE IF NOT EXISTS sm_report (" +
                                                                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                                                                "name VARCHAR(200) NOT NULL, " +
                                                                "type VARCHAR(50), " +
                                                                "dept VARCHAR(100), " +
                                                                "dept_id BIGINT, " +
                                                                "status VARCHAR(50) DEFAULT '草稿', " +
                                                                "file_path VARCHAR(500), " +
                                                                "del_flag CHAR(1) DEFAULT '0', " +
                                                                "create_by VARCHAR(64), " +
                                                                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                                                "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                                                                +
                                                                ") COMMENT='报表中心记录表'");
                                jdbcTemplate.execute(
                                                "CREATE TABLE IF NOT EXISTS sm_target (" +
                                                                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                                                                "indicator_id BIGINT, " +
                                                                "target_value DECIMAL(18,4), " +
                                                                "unit VARCHAR(20), " +
                                                                "period_type VARCHAR(20), " +
                                                                "period_date VARCHAR(20), " +
                                                                "dept_code VARCHAR(50), " +
                                                                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                                                "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                                                                +
                                                                ") COMMENT='指标看板目标设定表'");
                        } catch (Exception ignored) {
                        }
                        // 线上库仅有空表时专项报表中心无数据；本地常有导入/report_init。表为空则写入演示行（与常见前端演示一致）。
                        try {
                                Integer reportCnt = jdbcTemplate.queryForObject(
                                                "SELECT COUNT(*) FROM sm_report", Integer.class);
                                if (reportCnt != null && reportCnt == 0) {
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_report (name, type, dept, status, del_flag, create_by, create_time, update_time) VALUES "
                                                                        + "('2024年5月运营月报', '月报', '信息科', '已发布', '0', 'admin', NOW(), NOW()), "
                                                                        + "('2024年第一季度质量监控报告', '季报', '医务处', '待审核', '0', 'admin', NOW(), NOW()), "
                                                                        + "('2023年度全院业绩考评', '年报', '智慧管理中心', '已归档', '0', 'admin', NOW(), NOW()), "
                                                                        + "('DRG支付改革专项分析', '专项', '医务处', '草稿', '0', 'admin', NOW(), NOW())");
                                }
                        } catch (Exception ignored) {
                        }
                        try {
                                jdbcTemplate.execute(
                                                "CREATE TABLE IF NOT EXISTS sm_dashboard (" +
                                                                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                                                                "name VARCHAR(100), " +
                                                                "category VARCHAR(50), " +
                                                                "description VARCHAR(500), " +
                                                                "theme_id BIGINT NULL COMMENT '指标主题分类ID', " +
                                                                "publish_type VARCHAR(32) NULL COMMENT '发布对象类型', " +
                                                                "publish_target VARCHAR(500) NULL COMMENT '发布对象ID', " +
                                                                "url VARCHAR(1000) NULL COMMENT '看板地址', " +
                                                                "layout_config TEXT, " +
                                                                "thumbnail VARCHAR(500), " +
                                                                "status VARCHAR(32) DEFAULT 'draft', " +
                                                                "is_template CHAR(1) DEFAULT '0', " +
                                                                "publish_by VARCHAR(64) NULL COMMENT '发布人', " +
                                                                "publish_time DATETIME NULL COMMENT '发布时间', " +
                                                                "del_flag CHAR(1) DEFAULT '0', " +
                                                                "create_by VARCHAR(64), " +
                                                                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                                                "update_by VARCHAR(64), " +
                                                                "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                                                                +
                                                                ") COMMENT='驾驶舱看板配置表'");
                        } catch (Exception ignored) {
                        }
                        for (String sql : new String[] {
                                        "ALTER TABLE sm_dashboard ADD COLUMN theme_id BIGINT NULL COMMENT '指标主题分类ID'",
                                        "ALTER TABLE sm_dashboard ADD COLUMN publish_type VARCHAR(32) NULL COMMENT '发布对象类型'",
                                        "ALTER TABLE sm_dashboard ADD COLUMN publish_target VARCHAR(500) NULL COMMENT '发布对象ID'",
                                        "ALTER TABLE sm_dashboard ADD COLUMN url VARCHAR(1000) NULL COMMENT '看板地址'",
                                        "ALTER TABLE sm_dashboard ADD COLUMN publish_by VARCHAR(64) NULL COMMENT '发布人'",
                                        "ALTER TABLE sm_dashboard ADD COLUMN publish_time DATETIME NULL COMMENT '发布时间'" }) {
                                try {
                                        jdbcTemplate.execute(sql);
                                } catch (Exception ignored) {
                                }
                        }
                        try {
                                jdbcTemplate.execute(
                                                "ALTER TABLE sm_dashboard MODIFY COLUMN status VARCHAR(32) DEFAULT 'draft'");
                        } catch (Exception ignored) {
                        }
                        try {
                                Integer dashCnt = jdbcTemplate.queryForObject(
                                                "SELECT COUNT(*) FROM sm_dashboard", Integer.class);
                                if (dashCnt != null && dashCnt == 0) {
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_dashboard (name, category, status, del_flag, create_by, create_time, update_time) VALUES "
                                                                        + "('院长驾驶舱', 'cockpit', 'online', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'), "
                                                                        + "('全景运营监控', 'monitor', 'online', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'), "
                                                                        + "('运营效率分析', 'theme', 'online', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'), "
                                                                        + "('科室驾驶舱', 'cockpit', 'online', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'), "
                                                                        + "('重点效率监控', 'monitor', 'offline', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00'), "
                                                                        + "('月度运营报表', 'report', 'offline', '0', 'admin', '2024-03-04 10:00:00', '2024-03-04 10:00:00')");
                                }
                        } catch (Exception ignored) {
                        }

                        // --- 2. 核心指标数据初始化 (分段 try-catch, 防止外键约束中断) ---
                        try {
                                Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sm_indicator_lib",
                                                Integer.class);
                                if (count != null && count == 0) {
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (id, category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status) VALUES "
                                                                        +
                                                                        "(1, 1, '全院总收入', 'INCOME_TOTAL', '医院全口径收入', '元', 'M', 'System', 1, 1, '[INCOME_MED]', '0'), "
                                                                        +
                                                                        "(2, 1, '医疗收入', 'INCOME_MED', '业务医疗收入', '元', 'M', 'System', 1, 1, '[INCOME_DRUG] + [INCOME_OTHER]', '0'), "
                                                                        +
                                                                        "(3, 1, '药品收入', 'INCOME_DRUG', '各类药品销售收入', '元', 'M', 'System', 0, 0, NULL, '0'), "
                                                                        +
                                                                        "(4, 1, '其他收入', 'INCOME_OTHER', '非医疗类其他收入', '元', 'M', 'System', 0, 0, NULL, '0'), "
                                                                        +
                                                                        "(5, 2, '门诊总人次', 'OUTPATIENT_COUNT', '门急诊挂号人次', '人', 'D', 'System', 0, 0, NULL, '0'), "
                                                                        +
                                                                        "(6, 2, '住院人次', 'INPATIENT_COUNT', '入出院病人数量', '人', 'D', 'System', 0, 0, NULL, '0'), "
                                                                        +
                                                                        "(7, 3, '药占比', 'DRUG_RATIO', '药品收入占医疗收入比', '%', 'M', 'System', 0, 1, '[INCOME_DRUG] / [INCOME_MED] * 100', '0')");
                                }
                        } catch (Exception ignored) {
                        }

                        try {
                                jdbcTemplate.execute(
                                                "DELETE FROM sm_indicator_value WHERE period_date IN ('2026-02', '2026-01', '2025-02')");
                                jdbcTemplate.execute(
                                                "INSERT INTO sm_indicator_value (indicator_id, dept_id, dept_code, period_date, value) VALUES "
                                                                +
                                                                "(3, 102, 'YWK', '2026-02', 550000), " +
                                                                "(4, 101, 'XXK', '2026-02', 90000), " +
                                                                "(5, NULL, 'ALL', '2026-02', 13500), " +
                                                                "(6, NULL, 'ALL', '2026-02', 450), " +
                                                                "(3, 102, 'YWK', '2026-01', 500000), " +
                                                                "(5, NULL, 'ALL', '2026-01', 12000), " +
                                                                "(3, 102, 'YWK', '2025-02', 420000), " +
                                                                "(5, NULL, 'ALL', '2025-02', 9500)");
                        } catch (Exception ignored) {
                        }

                        // --- 3. 核心菜单系统初始化 (独立 try-catch, 确保运行) ---
                        try {
                                System.out.println("开始强制同步最新的菜单体系...");
                                jdbcTemplate.execute("DELETE FROM sys_role_menu");
                                jdbcTemplate.execute("DELETE FROM sys_menu");

                                // 插入最新的菜单
                                jdbcTemplate.execute(
                                                "INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon) VALUES "
                                                                + "(1, '首页门户', 0, 1, '/portal', 'portal/index', 'C', 'portal:view', 'HomeFilled'), "
                                                                + "(2, '全院指标监控', 0, 2, 'monitor', NULL, 'M', '', 'TrendCharts'), "
                                                                + "(3, '数据分析报表', 0, 3, 'analysis', NULL, 'M', '', 'Document'), "
                                                                + "(4, '医疗业务', 0, 4, 'medical', NULL, 'M', '', 'FirstAidKit'), "
                                                                + "(5, '看板管理', 0, 5, 'dashboard', NULL, 'M', '', 'Monitor'), "
                                                                + "(6, '系统管理', 0, 6, 'system', NULL, 'M', '', 'Setting')");

                                jdbcTemplate.execute(
                                                "INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon) VALUES "
                                                                + "(21, '指标监测总览', 2, 1, 'monitor', 'monitor/index', 'C', 'monitor:view', 'DataLine'), "
                                                                + "(22, '指标知识库', 2, 2, 'lib', 'monitor/lib', 'C', 'monitor:lib:view', 'Collection'), "
                                                                + "(23, '指标图谱', 2, 3, 'graph', 'monitor/graph', 'C', 'monitor:graph:view', 'Share'), "
                                                                + "(24, '目标与预警', 2, 4, 'target', 'monitor/target', 'C', 'monitor:target:view', 'Aim'), "
                                                                + "(25, '数据反馈管理', 2, 5, 'feedback', 'monitor/feedback', 'C', 'monitor:feedback:view', 'ChatLineSquare'), "
                                                                + "(26, '智能报告分发', 2, 6, 'report', 'monitor/report', 'C', 'monitor:report:view', 'Share')");

                                jdbcTemplate.execute(
                                                "INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon) VALUES "
                                                                + "(31, '基础报表中心', 3, 1, '/reports', 'reports/index', 'C', 'report:basic:view', 'Tickets'), "
                                                                + "(32, '自助BI设计', 3, 2, 'advanced', 'reports/advanced', 'C', 'report:bi:view', 'DataAnalysis'), "
                                                                + "(33, '运营主题分析', 3, 3, '/analysis/theme', 'analysis/theme', 'C', 'analysis:theme:view', 'DataBoard')");

                                jdbcTemplate.execute(
                                                "INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon) VALUES "
                                                                + "(41, '患者360视图', 4, 1, 'patient360', 'medical/patient360', 'C', 'medical:patient:view', 'User'), "
                                                                + "(42, '智能辅助诊疗', 4, 2, 'ai-diagnosis', 'medical/ai-diagnosis', 'C', 'medical:ai:view', 'Monitor')");

                                jdbcTemplate.execute(
                                                "INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon) VALUES "
                                                                + "(51, '看板列表', 5, 1, 'list', 'dashboard/list', 'C', 'dashboard:list:view', 'Grid')");

                                jdbcTemplate.execute(
                                                "INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon) VALUES "
                                                                + "(61, '用户管理', 6, 1, 'user', 'system/user', 'C', 'system:user:list', 'User'), "
                                                                + "(62, '角色管理', 6, 2, 'role', 'system/role', 'C', 'system:role:list', 'Stamp'), "
                                                                + "(63, '菜单管理', 6, 3, 'menu', 'system/menu', 'C', 'system:menu:list', 'Menu'), "
                                                                + "(64, '日志管理', 6, 4, 'log', 'system/log', 'C', 'system:log:list', 'Tickets'), "
                                                                + "(65, '参数配置', 6, 5, 'config', 'system/config', 'C', 'system:config:list', 'Setting'), "
                                                                + "(66, '字典管理', 6, 6, 'dict', 'system/dict', 'C', 'system:dict:list', 'Notebook'), "
                                                                + "(67, '个人中心', 6, 7, 'profile', 'system/profile', 'C', 'system:profile:view', 'UserFilled')");

                                // 重新赋予 admin 角色所有权限
                                jdbcTemplate.execute(
                                                "INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, id FROM sys_menu");
                                System.out.println("菜单体系强制初始化成功！已彻底移除“大屏设计器”。");
                        } catch (Exception e) {
                                System.out.println("菜单体系同步失败: " + e.getMessage());
                        }

                        // --- 4. 指标合规性改造 Phase 1: 表结构增强 ---
                        String[] alterSqls = {
                                        "ALTER TABLE sm_indicator_lib ADD COLUMN policy_source VARCHAR(200) DEFAULT NULL COMMENT '政策依据'",
                                        "ALTER TABLE sm_indicator_lib ADD COLUMN core_system VARCHAR(100) DEFAULT NULL COMMENT '核心制度归属'",
                                        "ALTER TABLE sm_indicator_lib ADD COLUMN numerator_code VARCHAR(50) DEFAULT NULL COMMENT '分子指标编码'",
                                        "ALTER TABLE sm_indicator_lib ADD COLUMN denominator_code VARCHAR(50) DEFAULT NULL COMMENT '分母指标编码'"
                        };
                        for (String sql : alterSqls) {
                                try {
                                        jdbcTemplate.execute(sql);
                                } catch (Exception ignored) {
                                }
                        }

                        // --- 5. 指标合规性改造 Phase 1: 指标分类体系重构 ---
                        try {
                                Integer catCount = jdbcTemplate.queryForObject(
                                                "SELECT COUNT(*) FROM sm_indicator_category WHERE id >= 100",
                                                Integer.class);
                                if (catCount != null && catCount == 0) {
                                        System.out.println("开始指标分类体系重构（新增二级分类）...");
                                        // 新增一级分类: 服务评价
                                        jdbcTemplate.execute(
                                                        "INSERT IGNORE INTO sm_indicator_category (id, parent_id, name, code, order_num, status, create_by, create_time) VALUES "
                                                                        + "(5, 0, '服务评价', 'SERVICE', 5, '0', 'admin', NOW())");
                                        // 经济运行(1) 二级分类
                                        jdbcTemplate.execute(
                                                        "INSERT IGNORE INTO sm_indicator_category (id, parent_id, name, code, order_num, status, create_by, create_time) VALUES "
                                                                        + "(101, 1, '总体收入', 'ECO_INCOME', 1, '0', 'admin', NOW()), "
                                                                        + "(102, 1, '门诊经济', 'ECO_OUTPATIENT', 2, '0', 'admin', NOW()), "
                                                                        + "(103, 1, '住院经济', 'ECO_INPATIENT', 3, '0', 'admin', NOW())");
                                        // 产出效率(2) 二级分类
                                        jdbcTemplate.execute(
                                                        "INSERT IGNORE INTO sm_indicator_category (id, parent_id, name, code, order_num, status, create_by, create_time) VALUES "
                                                                        + "(201, 2, '门急诊效率', 'EFF_OUTPATIENT', 1, '0', 'admin', NOW()), "
                                                                        + "(202, 2, '住院效率', 'EFF_INPATIENT', 2, '0', 'admin', NOW()), "
                                                                        + "(203, 2, '床位效率', 'EFF_BED', 3, '0', 'admin', NOW())");
                                        // 质量安全(3) 二级分类 - 对应核心制度
                                        jdbcTemplate.execute(
                                                        "INSERT IGNORE INTO sm_indicator_category (id, parent_id, name, code, order_num, status, create_by, create_time) VALUES "
                                                                        + "(301, 3, '首诊负责', 'QUA_FIRST_DIAG', 1, '0', 'admin', NOW()), "
                                                                        + "(302, 3, '三级查房', 'QUA_WARD_ROUND', 2, '0', 'admin', NOW()), "
                                                                        + "(303, 3, '会诊管理', 'QUA_CONSULTATION', 3, '0', 'admin', NOW()), "
                                                                        + "(304, 3, '手术管理', 'QUA_SURGERY', 4, '0', 'admin', NOW()), "
                                                                        + "(305, 3, '分级护理', 'QUA_NURSING', 5, '0', 'admin', NOW()), "
                                                                        + "(306, 3, '值班交接班', 'QUA_HANDOVER', 6, '0', 'admin', NOW()), "
                                                                        + "(307, 3, '疑难病例讨论', 'QUA_DIFFICULT_CASE', 7, '0', 'admin', NOW()), "
                                                                        + "(308, 3, '急危重症抢救', 'QUA_EMERGENCY', 8, '0', 'admin', NOW()), "
                                                                        + "(309, 3, '术前讨论', 'QUA_PREOP_DISCUSS', 9, '0', 'admin', NOW()), "
                                                                        + "(310, 3, '死亡病例讨论', 'QUA_DEATH_DISCUSS', 10, '0', 'admin', NOW()), "
                                                                        + "(311, 3, '查对制度', 'QUA_CHECK', 11, '0', 'admin', NOW()), "
                                                                        + "(312, 3, '手术安全核查', 'QUA_SURGERY_CHECK', 12, '0', 'admin', NOW()), "
                                                                        + "(313, 3, '手术分级管理', 'QUA_SURGERY_GRADE', 13, '0', 'admin', NOW()), "
                                                                        + "(314, 3, '新技术准入', 'QUA_NEW_TECH', 14, '0', 'admin', NOW()), "
                                                                        + "(315, 3, '危急值报告', 'QUA_CRITICAL_VALUE', 15, '0', 'admin', NOW()), "
                                                                        + "(316, 3, '抗菌药物管理', 'QUA_ANTIBIOTICS', 16, '0', 'admin', NOW()), "
                                                                        + "(317, 3, '临床用血管理', 'QUA_BLOOD_USE', 17, '0', 'admin', NOW()), "
                                                                        + "(318, 3, '感染控制', 'QUA_INFECTION', 18, '0', 'admin', NOW()), "
                                                                        + "(319, 3, '合理用药', 'QUA_DRUG_USE', 19, '0', 'admin', NOW())");
                                        // 实时态势(4) 二级分类
                                        jdbcTemplate.execute(
                                                        "INSERT IGNORE INTO sm_indicator_category (id, parent_id, name, code, order_num, status, create_by, create_time) VALUES "
                                                                        + "(401, 4, '实时监控', 'RT_MONITOR', 1, '0', 'admin', NOW())");
                                        // 服务评价(5) 二级分类
                                        jdbcTemplate.execute(
                                                        "INSERT IGNORE INTO sm_indicator_category (id, parent_id, name, code, order_num, status, create_by, create_time) VALUES "
                                                                        + "(501, 5, '患者满意度', 'SVC_SATISFACTION', 1, '0', 'admin', NOW()), "
                                                                        + "(502, 5, '投诉管理', 'SVC_COMPLAINT', 2, '0', 'admin', NOW())");
                                        System.out.println("指标分类体系重构完成！新增1个一级分类 + 24个二级分类。");
                                }
                        } catch (Exception e) {
                                System.out.println("分类体系重构失败: " + e.getMessage());
                        }

                        // --- 6. 指标合规性改造 Phase 2: 35项核心制度监测指标入库 ---
                        try {
                                Integer quaCount = jdbcTemplate.queryForObject(
                                                "SELECT COUNT(*) FROM sm_indicator_lib WHERE code LIKE 'QUA0%'",
                                                Integer.class);
                                if (quaCount != null && quaCount == 0) {
                                        System.out.println("开始录入35项医疗质量安全核心制度监测指标...");

                                        // 指标一: 首诊负责制
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(301, '患者入院48h内转科比例', 'QUA001', '入院48小时内转科患者人次/同期入院患者总人次×100%。不含转入/转出ICU。', '%', 'M', 'HIS', 0, 1, '[QUA001_N] / [QUA001_D] * 100', '0', '2025版核心制度指标-指标一', '首诊负责制', 'QUA001_N', 'QUA001_D')");

                                        // 指标二~四: 三级查房制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(302, '患者入院8h内查房率', 'QUA002', '入院8小时内开具检查或治疗医嘱的患者人次/同期入院患者总人次×100%。', '%', 'M', 'EMR', 0, 1, '[QUA002_N] / [QUA002_D] * 100', '0', '2025版核心制度指标-指标二', '三级查房制度', 'QUA002_N', 'QUA002_D'), "
                                                                        + "(302, '上级医师查房记录规范率', 'QUA003', '病历中上级医师查房记录规范完整的病例数/同期住院患者病例总数×100%。', '%', 'M', 'EMR', 0, 1, '[QUA003_N] / [QUA003_D] * 100', '0', '2025版核心制度指标-指标三', '三级查房制度', 'QUA003_N', 'QUA003_D'), "
                                                                        + "(302, '住院患者非计划手术率', 'QUA004', '行非计划手术的住院患者人次/同期住院患者总人次×100%。', '%', 'M', 'HIS', 0, 1, '[QUA004_N] / [QUA004_D] * 100', '0', '2025版核心制度指标-指标四', '三级查房制度', 'QUA004_N', 'QUA004_D')");

                                        // 指标五~八: 会诊制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(303, '急会诊及时到位率', 'QUA005', '急会诊请求发出后10分钟内到达现场的次数/同期急会诊总次数×100%。', '%', 'M', 'EMR', 0, 1, '[QUA005_N] / [QUA005_D] * 100', '0', '2025版核心制度指标-指标五', '会诊制度', 'QUA005_N', 'QUA005_D'), "
                                                                        + "(303, '急会诊有效率', 'QUA006', '急会诊后40分钟内开具相关医嘱的次数/同期急会诊总次数×100%。', '%', 'M', 'EMR', 0, 1, '[QUA006_N] / [QUA006_D] * 100', '0', '2025版核心制度指标-指标六', '会诊制度', 'QUA006_N', 'QUA006_D'), "
                                                                        + "(303, '普通会诊及时完成率', 'QUA007', '普通会诊24小时内完成次数/同期普通会诊总次数×100%。', '%', 'M', 'EMR', 0, 1, '[QUA007_N] / [QUA007_D] * 100', '0', '2025版核心制度指标-指标七', '会诊制度', 'QUA007_N', 'QUA007_D'), "
                                                                        + "(303, '普通会诊有效率', 'QUA008', '普通会诊结束后24小时内开具相关医嘱的次数/同期普通会诊总次数×100%。', '%', 'M', 'EMR', 0, 1, '[QUA008_N] / [QUA008_D] * 100', '0', '2025版核心制度指标-指标八', '会诊制度', 'QUA008_N', 'QUA008_D')");

                                        // 指标九: 分级护理制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(305, '手术患者特级/一级护理出院率', 'QUA009', '手术患者出院时为特级/一级护理级别的患者数/同期手术患者总数×100%。', '%', 'M', '护理系统', 0, 1, '[QUA009_N] / [QUA009_D] * 100', '0', '2025版核心制度指标-指标九', '分级护理制度', 'QUA009_N', 'QUA009_D')");

                                        // 指标十: 值班与交接班制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(306, '四级手术患者手术当日床旁交接班占比', 'QUA010', '四级手术患者当日进行床旁交接班的患者数/同期四级手术患者总数×100%。', '%', 'M', 'HIS', 0, 1, '[QUA010_N] / [QUA010_D] * 100', '0', '2025版核心制度指标-指标十', '值班与交接班制度', 'QUA010_N', 'QUA010_D')");

                                        // 指标十一~十三: 疑难病例讨论制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(307, '非计划再次住院/手术疑难病例讨论完成率', 'QUA011', '对非计划再次住院/手术患者进行疑难病例讨论的数量/同期非计划再次住院/手术数量×100%。', '%', 'M', 'EMR', 0, 1, '[QUA011_N] / [QUA011_D] * 100', '0', '2025版核心制度指标-指标十一', '疑难病例讨论制度', 'QUA011_N', 'QUA011_D'), "
                                                                        + "(307, '非计划再次住院/手术疑难病例讨论记录完整率', 'QUA012', '讨论结论记入病历的数量/同期疑难病例讨论的数量×100%。', '%', 'M', 'EMR', 0, 1, '[QUA012_N] / [QUA012_D] * 100', '0', '2025版核心制度指标-指标十二', '疑难病例讨论制度', 'QUA012_N', 'QUA012_D'), "
                                                                        + "(307, '高额异常费用患者疑难病例讨论占比', 'QUA013', '对产生高额异常费用(≥20万)患者进行疑难讨论的数量/同期高额异常费用患者数×100%。', '%', 'Q', 'EMR', 0, 1, '[QUA013_N] / [QUA013_D] * 100', '0', '2025版核心制度指标-指标十三', '疑难病例讨论制度', 'QUA013_N', 'QUA013_D')");

                                        // 指标十四: 急危重症抢救制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(308, '急危重症患者抢救成功率', 'QUA014', '抢救成功(存活超24h)的例次数/同期急危重症抢救总例次数×100%。', '%', 'M', 'EMR', 0, 1, '[QUA014_N] / [QUA014_D] * 100', '0', '2025版核心制度指标-指标十四', '急危重症抢救制度', 'QUA014_N', 'QUA014_D')");

                                        // 指标十五~十八: 术前讨论制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(309, '术前讨论完成率', 'QUA015', '完成术前讨论的手术例数/同期手术总例数×100%。除急诊手术外。', '%', 'M', '手术系统', 0, 1, '[QUA015_N] / [QUA015_D] * 100', '0', '2025版核心制度指标-指标十五', '术前讨论制度', 'QUA015_N', 'QUA015_D'), "
                                                                        + "(309, '术者参加术前讨论率', 'QUA016', '术者参加术前讨论的手术例数/同期进行术前讨论手术总例数×100%。', '%', 'M', '手术系统', 0, 1, '[QUA016_N] / [QUA016_D] * 100', '0', '2025版核心制度指标-指标十六', '术前讨论制度', 'QUA016_N', 'QUA016_D'), "
                                                                        + "(309, '术前讨论计划手术一致率', 'QUA017', '实际手术与术前讨论计划一致的手术例数/同期手术总例数×100%。', '%', 'M', '手术系统', 0, 1, '[QUA017_N] / [QUA017_D] * 100', '0', '2025版核心制度指标-指标十七', '术前讨论制度', 'QUA017_N', 'QUA017_D'), "
                                                                        + "(309, '实际手术术者与计划术者一致率', 'QUA018', '实际术者与计划术者一致的手术例数/同期手术总例数×100%。', '%', 'M', '手术系统', 0, 1, '[QUA018_N] / [QUA018_D] * 100', '0', '2025版核心制度指标-指标十八', '术前讨论制度', 'QUA018_N', 'QUA018_D')");

                                        // 指标十九~二十二: 死亡病例讨论制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(310, '死亡病例讨论5日完成率', 'QUA019', '患者死亡5个工作日内完成讨论的病例数/同期死亡病例总数×100%。', '%', 'M', 'EMR', 0, 1, '[QUA019_N] / [QUA019_D] * 100', '0', '2025版核心制度指标-指标十九', '死亡病例讨论制度', 'QUA019_N', 'QUA019_D'), "
                                                                        + "(310, '医务部门组织讨论死亡病例与纠纷死亡病例比值', 'QUA020', '医务部门组织死亡病例讨论数量与同期发生医疗纠纷的死亡病例数量的比值。', '比值', 'Q', 'EMR', 0, 1, '[QUA020_N] / [QUA020_D]', '0', '2025版核心制度指标-指标二十', '死亡病例讨论制度', 'QUA020_N', 'QUA020_D'), "
                                                                        + "(310, '科主任主持死亡病例讨论率', 'QUA021', '科主任主持讨论的病例数/同期死亡病例总数×100%。', '%', 'M', 'EMR', 0, 1, '[QUA021_N] / [QUA021_D] * 100', '0', '2025版核心制度指标-指标二十一', '死亡病例讨论制度', 'QUA021_N', 'QUA021_D'), "
                                                                        + "(310, '死亡患者病案上传率', 'QUA022', '完整上传死亡患者病案的数量/同期应上传数量(HQMS清单)×100%。', '%', 'M', 'EMR', 0, 1, '[QUA022_N] / [QUA022_D] * 100', '0', '2025版核心制度指标-指标二十二', '死亡病例讨论制度', 'QUA022_N', 'QUA022_D')");

                                        // 指标二十三: 查对制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(311, '长期医嘱当日终止率', 'QUA023', '开具长期医嘱后当日终止执行的医嘱数/同期开具长期医嘱总数×100%。', '%', 'M', 'HIS', 0, 1, '[QUA023_N] / [QUA023_D] * 100', '0', '2025版核心制度指标-指标二十三', '查对制度', 'QUA023_N', 'QUA023_D')");

                                        // 指标二十四~二十五: 手术安全核查制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(312, '手术医师手术时间重合率', 'QUA024', '同一时间内手术医师为同一人的手术例数/同期住院手术总例数×100%。', '%', 'M', '手术系统', 0, 1, '[QUA024_N] / [QUA024_D] * 100', '0', '2025版核心制度指标-指标二十四', '手术安全核查制度', 'QUA024_N', 'QUA024_D'), "
                                                                        + "(312, '麻醉医师手术时间重合率', 'QUA025', '同一时间内麻醉医师为同一人的手术例数/同期住院手术总例数×100%。', '%', 'M', '手术系统', 0, 1, '[QUA025_N] / [QUA025_D] * 100', '0', '2025版核心制度指标-指标二十五', '手术安全核查制度', 'QUA025_N', 'QUA025_D')");

                                        // 指标二十六~二十九: 手术分级管理制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(313, '四级与三级手术并发症发生率比', 'QUA026', '四级手术并发症发生率/三级手术并发症发生率。', '比值', 'Q', '手术系统', 0, 1, '[QUA026_N] / [QUA026_D]', '0', '2025版核心制度指标-指标二十六', '手术分级管理制度', 'QUA026_N', 'QUA026_D'), "
                                                                        + "(313, '四级与三级手术患者死亡率比', 'QUA027', '四级手术患者死亡率/三级手术患者死亡率。', '比值', 'Q', '手术系统', 0, 1, '[QUA027_N] / [QUA027_D]', '0', '2025版核心制度指标-指标二十七', '手术分级管理制度', 'QUA027_N', 'QUA027_D'), "
                                                                        + "(313, '四级手术术前多学科讨论完成率', 'QUA028', '术前完成多学科讨论的四级手术例数/同期四级手术总例数×100%。含限制类技术。', '%', 'M', '手术系统', 0, 1, '[QUA028_N] / [QUA028_D] * 100', '0', '2025版核心制度指标-指标二十八', '手术分级管理制度', 'QUA028_N', 'QUA028_D'), "
                                                                        + "(313, '三四级手术实际开展率', 'QUA029', '实际开展的三四级手术术种数/同期备案术种数×100%。', '%', 'Q', '手术系统', 0, 1, '[QUA029_N] / [QUA029_D] * 100', '0', '2025版核心制度指标-指标二十九', '手术分级管理制度', 'QUA029_N', 'QUA029_D')");

                                        // 指标三十: 新技术新项目准入制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(314, '新技术新项目留存转化率', 'QUA030', '四年评估周期内新增技术在第四年继续开展的比例。(A+C+E)/(A+B+C+D+E+F)×100%。', '%', 'Y', '医务管理', 0, 1, '[QUA030_N] / [QUA030_D] * 100', '0', '2025版核心制度指标-指标三十', '新技术新项目准入制度', 'QUA030_N', 'QUA030_D')");

                                        // 指标三十一~三十二: 危急值报告制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(315, '危急值报告时间(中位数)', 'QUA031', '出现危急值到临床科室获取的时间中位数。分住院/门诊/急诊统计。', '分钟', 'M', 'LIS', 0, 0, NULL, '0', '2025版核心制度指标-指标三十一', '危急值报告制度', NULL, NULL), "
                                                                        + "(315, '住院患者危急值当日及时处置率', 'QUA032', '当日处置的住院患者危急值项目数/同期接获危急值项目数×100%。不含门急诊。', '%', 'M', 'LIS', 0, 1, '[QUA032_N] / [QUA032_D] * 100', '0', '2025版核心制度指标-指标三十二', '危急值报告制度', 'QUA032_N', 'QUA032_D')");

                                        // 指标三十三: 抗菌药物分级管理制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(316, '特殊使用级抗菌药物使用会诊率', 'QUA033', '特殊使用级抗菌药物使用医嘱与会诊记录对应的数量/同期特殊使用级抗菌药物使用医嘱总数×100%。', '%', 'M', '药品管理', 0, 1, '[QUA033_N] / [QUA033_D] * 100', '0', '2025版核心制度指标-指标三十三', '抗菌药物分级管理制度', 'QUA033_N', 'QUA033_D')");

                                        // 指标三十四~三十五: 临床用血审核制度
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source, core_system, numerator_code, denominator_code) VALUES "
                                                                        + "(317, '临床用血后评估记录率', 'QUA034', '输血后规范书写评估记录的例次数/同期临床输血治疗病例总例次数×100%。', '%', 'M', '输血管理', 0, 1, '[QUA034_N] / [QUA034_D] * 100', '0', '2025版核心制度指标-指标三十四', '临床用血审核制度', 'QUA034_N', 'QUA034_D'), "
                                                                        + "(317, '术中自体血回输率', 'QUA035', '术中使用自体血回输的患者数/同期术中进行输血患者总数×100%。', '%', 'M', '输血管理', 0, 1, '[QUA035_N] / [QUA035_D] * 100', '0', '2025版核心制度指标-指标三十五', '临床用血审核制度', 'QUA035_N', 'QUA035_D')");

                                        // Mock指标正式化
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source) VALUES "
                                                                        + "(203, '床位使用率', 'EFF003', '实际占用总床日数/同期实际开放总床日数×100%', '%', 'M', 'HIS', 0, 1, '[EFF003_N] / [EFF003_D] * 100', '0', '运营管理指标'), "
                                                                        + "(203, '病床周转次数', 'EFF004', '出院患者总数/同期平均开放床位数', '次', 'M', 'HIS', 0, 1, '[EFF004_N] / [EFF004_D]', '0', '运营管理指标'), "
                                                                        + "(319, '耗占比', 'QUA_D02', '卫生材料收入/医疗收入×100%', '%', 'M', 'System', 0, 1, '[QUA_D02_N] / [QUA_D02_D] * 100', '0', '运营管理指标'), "
                                                                        + "(501, '患者满意度', 'SVC001', '满意度调查综合评分', '分', 'M', '满意度调查', 0, 0, NULL, '0', '服务评价指标'), "
                                                                        + "(102, '门诊收入', 'ECO005', '门诊业务收入合计', '万元', 'M', 'System', 1, 0, NULL, '0', '运营管理指标')");

                                        System.out.println("35项核心制度监测指标 + 5项正式化指标录入完成！共计新增40个指标。");
                                }
                        } catch (Exception e) {
                                System.out.println("核心制度指标入库失败: " + e.getMessage());
                        }

                        // --- 7. 旧指标编码迁移 + 分类ID更新 + 补充缺失指标 ---
                        try {
                                // 检查是否已迁移（通过新编码 ECO001 是否存在判断）
                                Integer ecoCount = jdbcTemplate.queryForObject(
                                                "SELECT COUNT(*) FROM sm_indicator_lib WHERE code = 'ECO001'",
                                                Integer.class);
                                if (ecoCount != null && ecoCount == 0) {
                                        System.out.println("开始旧指标编码迁移与分类更新...");

                                        // 7.1 编码迁移: 旧编码 → 新编码（符合 {领域3字母}{3位序号} 规范）
                                        String[] codeUpdates = {
                                                        "UPDATE sm_indicator_lib SET code='ECO001', policy_source='运营管理指标' WHERE code='INCOME_TOTAL'",
                                                        "UPDATE sm_indicator_lib SET code='ECO002', policy_source='运营管理指标' WHERE code='INCOME_MED'",
                                                        "UPDATE sm_indicator_lib SET code='ECO003', policy_source='运营管理指标' WHERE code='INCOME_DRUG'",
                                                        "UPDATE sm_indicator_lib SET code='ECO004', policy_source='运营管理指标' WHERE code='INCOME_OTHER'",
                                                        "UPDATE sm_indicator_lib SET code='EFF001', policy_source='运营管理指标' WHERE code='OUTPATIENT_COUNT'",
                                                        "UPDATE sm_indicator_lib SET code='EFF002', policy_source='运营管理指标' WHERE code='INPATIENT_COUNT'",
                                                        "UPDATE sm_indicator_lib SET code='QUA_D01', policy_source='运营管理指标' WHERE code='DRUG_RATIO'"
                                        };
                                        for (String sql : codeUpdates) {
                                                try {
                                                        jdbcTemplate.execute(sql);
                                                } catch (Exception ignored) {
                                                }
                                        }

                                        // 7.2 分类ID迁移: 旧一级分类 → 新二级分类
                                        String[] categoryUpdates = {
                                                        // 经济运行: ECO001~ECO004 → 总体收入(101)
                                                        "UPDATE sm_indicator_lib SET category_id=101 WHERE code IN ('ECO001','ECO002','ECO003','ECO004')",
                                                        // 产出效率: EFF001~EFF002 → 门急诊效率(201)
                                                        "UPDATE sm_indicator_lib SET category_id=201 WHERE code IN ('EFF001','EFF002')",
                                                        // 药占比 → 合理用药(319)
                                                        "UPDATE sm_indicator_lib SET category_id=319 WHERE code='QUA_D01'"
                                        };
                                        for (String sql : categoryUpdates) {
                                                try {
                                                        jdbcTemplate.execute(sql);
                                                } catch (Exception ignored) {
                                                }
                                        }

                                        // 7.3 更新旧指标的计算公式中的引用编码
                                        String[] formulaUpdates = {
                                                        "UPDATE sm_indicator_lib SET calc_formula='[ECO002]' WHERE code='ECO001'",
                                                        "UPDATE sm_indicator_lib SET calc_formula='[ECO003] + [ECO004]' WHERE code='ECO002'",
                                                        "UPDATE sm_indicator_lib SET calc_formula='[ECO003] / [ECO002] * 100' WHERE code='QUA_D01'"
                                        };
                                        for (String sql : formulaUpdates) {
                                                try {
                                                        jdbcTemplate.execute(sql);
                                                } catch (Exception ignored) {
                                                }
                                        }

                                        // 7.4 补充缺失指标: RT_001(当前在院人数)
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sm_indicator_lib (category_id, name, code, description, unit, frequency, data_source, is_sensitive, is_composite, calc_formula, status, policy_source) VALUES "
                                                                        + "(401, '当前在院人数', 'RT_001', '当前时刻在院住院患者总人数', '人', 'D', 'HIS', 0, 0, NULL, '0', '运营管理指标')");

                                        System.out.println("旧指标编码迁移完成！7项编码更新 + 3项分类迁移 + 1项新增(RT_001)。");
                                }
                        } catch (Exception e) {
                                System.out.println("旧指标迁移失败: " + e.getMessage());
                        }
                        // 8. 自动化执行专家级真实医院驾驶舱指标 SQL (修复乱码版本)
                        try {
                                // 强制清理所有与 REAL_ 相关的乱码死数据
                                jdbcTemplate.execute(
                                                "DELETE FROM sm_indicator_value WHERE indicator_id IN (SELECT id FROM sm_indicator_lib WHERE code LIKE 'REAL_%')");
                                jdbcTemplate.execute("DELETE FROM sm_indicator_lib WHERE code LIKE 'REAL_%'");

                                Integer cockpitCount = jdbcTemplate.queryForObject(
                                                "SELECT COUNT(*) FROM sm_indicator_lib WHERE policy_source = '真实医院驾驶舱指标集'",
                                                Integer.class);
                                if (cockpitCount != null && cockpitCount == 0) {
                                        System.out.println("检测到《真实医院驾驶舱指标集》尚未入库，开始自动执行 SQL 录入(UTF-8编码)...");
                                        org.springframework.core.io.FileSystemResource resource = new org.springframework.core.io.FileSystemResource(
                                                        "e:/datacenter2/smart-manager-backend/sql/cockpit_indicators_update.sql");
                                        org.springframework.core.io.support.EncodedResource encodedResource = new org.springframework.core.io.support.EncodedResource(
                                                        resource, "UTF-8");
                                        try (java.sql.Connection conn = jdbcTemplate.getDataSource().getConnection()) {
                                                org.springframework.jdbc.datasource.init.ScriptUtils
                                                                .executeSqlScript(conn, encodedResource);
                                                System.out.println(">> 专家级驾驶舱指标写入并 Mock 完成！与前端大屏完美融合。");
                                        }
                                } else {
                                        System.out.println("检测到《真实医院驾驶舱指标集》已入库，跳过执行。");
                                }
                        } catch (Exception e) {
                                System.out.println("专家级驾驶舱指标写入失败: " + e.getMessage());
                        }
                        // --- 9. 角色名乱码修复、登录/操作日志与预警/目标演示行（空表时；避免管理端图表全空、??? 显示） ---
                        try {
                                jdbcTemplate.execute("SET NAMES utf8mb4");
                        } catch (Exception ignored) {
                        }
                        try {
                                jdbcTemplate.execute(
                                                "UPDATE sys_role SET role_name = '普通角色' WHERE role_key = 'common'");
                        } catch (Exception ignored) {
                        }
                        try {
                                Integer loginCnt = jdbcTemplate.queryForObject(
                                                "SELECT COUNT(*) FROM sys_logininfor", Integer.class);
                                if (loginCnt != null && loginCnt == 0) {
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sys_logininfor (user_name, ipaddr, status, msg, login_time) VALUES "
                                                                        + "('admin', '127.0.0.1', '0', '登录成功', NOW()), "
                                                                        + "('admin', '10.0.0.1', '0', '登录成功', DATE_SUB(NOW(), INTERVAL 1 DAY)), "
                                                                        + "('admin', '192.168.1.1', '0', '登录成功', DATE_SUB(NOW(), INTERVAL 2 DAY))");
                                }
                        } catch (Exception ignored) {
                        }
                        try {
                                Integer operCnt = jdbcTemplate.queryForObject(
                                                "SELECT COUNT(*) FROM sys_oper_log", Integer.class);
                                if (operCnt != null && operCnt == 0) {
                                        jdbcTemplate.execute(
                                                        "INSERT INTO sys_oper_log (title, business_type, method, request_method, operator_type, oper_name, oper_url, status, oper_time) VALUES "
                                                                        + "('用户管理', 0, 'list', 'GET', 1, 'admin', '/api/system/user/list', 0, NOW()), "
                                                                        + "('角色管理', 0, 'list', 'GET', 1, 'admin', '/api/system/role/list', 0, DATE_SUB(NOW(), INTERVAL 1 HOUR)), "
                                                                        + "('菜单查询', 0, 'list', 'GET', 1, 'admin', '/api/system/menu/list', 0, DATE_SUB(NOW(), INTERVAL 3 HOUR))");
                                }
                        } catch (Exception ignored) {
                        }
                        try {
                                Integer libCnt = jdbcTemplate.queryForObject(
                                                "SELECT COUNT(*) FROM sm_indicator_lib", Integer.class);
                                if (libCnt != null && libCnt > 0) {
                                        Long firstIndId = jdbcTemplate.queryForObject(
                                                        "SELECT MIN(id) FROM sm_indicator_lib", Long.class);
                                        if (firstIndId != null) {
                                                Integer arCnt = jdbcTemplate.queryForObject(
                                                                "SELECT COUNT(*) FROM sm_alert_rule", Integer.class);
                                                if (arCnt != null && arCnt == 0) {
                                                        jdbcTemplate.execute(String.format(
                                                                        "INSERT INTO sm_alert_rule (indicator_id, rule_name, operator, threshold, level, status, message_template) "
                                                                                        + "VALUES (%d, '默认阈值预警', 'GT', 80.0000, 1, 1, '指标超过阈值时请查看')",
                                                                        firstIndId));
                                                }
                                                Integer amCnt = jdbcTemplate.queryForObject(
                                                                "SELECT COUNT(*) FROM sm_alert_message",
                                                                Integer.class);
                                                if (amCnt != null && amCnt == 0) {
                                                        jdbcTemplate.execute(String.format(
                                                                        "INSERT INTO sm_alert_message (indicator_id, rule_id, level, content, status, create_time) "
                                                                                        + "VALUES (%d, NULL, 1, '演示：请关注指标波动', 0, NOW())",
                                                                        firstIndId));
                                                }
                                                Integer tgCnt = jdbcTemplate.queryForObject(
                                                                "SELECT COUNT(*) FROM sm_target", Integer.class);
                                                if (tgCnt != null && tgCnt == 0) {
                                                        jdbcTemplate.execute(String.format(
                                                                        "INSERT INTO sm_target (indicator_id, target_value, unit, period_type, period_date, dept_code) "
                                                                                        + "VALUES (%d, 95.0000, '%s', 'M', '2026-04', 'ALL')",
                                                                        firstIndId, "%"));
                                                }
                                                Integer tcCnt = jdbcTemplate.queryForObject(
                                                                "SELECT COUNT(*) FROM sm_target_config",
                                                                Integer.class);
                                                if (tcCnt != null && tcCnt == 0) {
                                                        jdbcTemplate.execute(String.format(
                                                                        "INSERT INTO sm_target_config (indicator_id, period, dept_code, target_value, challenge_value, create_by) "
                                                                                        + "VALUES (%d, '2026', NULL, 90.0000, 95.0000, 'admin')",
                                                                        firstIndId));
                                                }
                                        }
                                }
                        } catch (Exception ignored) {
                        }
                };
        }
}
