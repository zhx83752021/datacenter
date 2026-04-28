package com.smart.manager.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smart.manager.entity.SmIndicatorLib;
import com.smart.manager.entity.SysRole;
import com.smart.manager.entity.SysUser;
import com.smart.manager.service.ISmIndicatorLibService;
import com.smart.manager.service.ISysRoleService;
import com.smart.manager.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 确保测试所需的关键指标存在于知识库中，并配置正确的数据源
 * 2026-03-10: 按照《系统对接需求清单》及《2025版核心指标监测标准》进行全面补全
 */
@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class IndicatorDataInitializer implements CommandLineRunner {

    private final ISmIndicatorLibService indicatorService;
    private final ISysUserService userService;
    private final ISysRoleService roleService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // 先修复用户/角色乱码（仅依赖 init.sql 中的 sys_* 表）
        fixUserAndRoleMangledData();

        if (!smIndicatorLibTableExists()) {
            log.warn("表 sm_indicator_lib 不存在，已跳过指标库初始化（仅执行了用户/角色乱码修复）。"
                    + "请在库中导入 smart-manager-backend/sql/rebuild_tables.sql 或含 sm_indicator_lib 的建表脚本后重启。");
            return;
        }

        fixIndicatorMangledData();

        log.info(">>>> [系统初始化] 正在同步核心指标体系 (对齐 2025 监测标准)... <<<<");

        // --- 1. 财务经济监控 (100+) ---
        checkAndCreate("ECO0101", "门诊部人均医疗收入", "HIS", 1, "[ECO005] / [EFF001]", 101);
        checkAndCreate("ECO0102", "住院部人均医疗收入", "HIS", 1, "[ECO002] / [EFF004_N]", 101);
        checkAndCreate("ECO001", "全院总收入", "HIS", 0, null, 101);
        checkAndCreate("ECO002", "全院医疗收入", "HIS", 0, null, 101);
        checkAndCreate("ECO003", "药品总收入", "HIS", 0, null, 101);
        checkAndCreate("ECO005", "门诊部医疗收入", "HIS", 0, null, 102);

        // --- 2. 运营效率监控 (200+) ---
        checkAndCreate("EFF001", "门急诊总人次", "HIS", 0, null, 201);
        checkAndCreate("EFF004_N", "出院患者总人数", "HIS", 0, null, 202);
        checkAndCreate("EFF0201", "床位使用率(运营)", "HIS", 1, "[EFF003_N] / [EFF003_D] * 100", 203);
        checkAndCreate("EFF003_N", "实际占用总床日数", "HIS", 0, null, 203);
        checkAndCreate("EFF003_D", "实际开放总床日数", "HIS", 0, null, 203);

        // --- 3. 核心质量监控 - 指标落实(一) (301+) ---
        checkAndCreate("QUA001", "48h内转科比例", "HIS", 1, "[QUA001_N] / [QUA001_D] * 100", 301);
        checkAndCreate("QUA001_N", "48h内非计划转科人次数", "HIS", 0, null, 301);
        checkAndCreate("QUA001_D", "同期入院患者总人数", "HIS", 0, null, 301);

        checkAndCreate("QUA002", "入院8h内查房记录率", "EMR", 1, "[QUA002_N] / [QUA002_D] * 100", 302);
        checkAndCreate("QUA002_N", "8h内完成查房记录人数", "EMR", 0, null, 302);
        checkAndCreate("QUA002_D", "同期入院患者总数(查房)", "EMR", 0, null, 302);

        checkAndCreate("QUA004", "住院患者非计划手术率", "HIS", 1, "[QUA004_N] / [QUA004_D] * 100", 304);
        checkAndCreate("QUA004_N", "非计划离室手术例数", "HIS", 0, null, 304);
        checkAndCreate("QUA004_D", "同期住院患者手术总例数", "HIS", 0, null, 304);

        // --- 4. 核心质量监控 - 指标落实(二) (305+) ---
        checkAndCreate("QUA009", "手术患者特级/一级护理出院率", "EMR", 1, "[QUA009_N] / [QUA009_D] * 100", 305);
        checkAndCreate("QUA009_N", "特级/一级护理出院人数", "EMR", 0, null, 305);
        checkAndCreate("QUA009_D", "同期出院手术患者总数", "EMR", 0, null, 305);

        checkAndCreate("QUA010", "四级手术当日床旁交接班占比", "EMR", 1, "[QUA010_N] / [QUA010_D] * 100", 306);
        checkAndCreate("QUA010_N", "完成床旁交接人次数", "EMR", 0, null, 306);
        checkAndCreate("QUA010_D", "同期四级手术总例数", "EMR", 0, null, 306);

        checkAndCreate("QUA011", "疑难病例讨论率", "EMR", 1, "[QUA011_N] / [QUA011_D] * 100", 307);
        checkAndCreate("QUA011_N", "已执行疑难讨论病例数", "EMR", 0, null, 307);
        checkAndCreate("QUA011_D", "同期非计划再次住院/手术总数", "EMR", 0, null, 307);

        checkAndCreate("QUA015", "术前讨论完成率", "ASIS", 1, "[QUA015_N] / [QUA015_D] * 100", 309);
        checkAndCreate("QUA015_N", "规范完成术前讨论例数", "ASIS", 0, null, 309);
        checkAndCreate("QUA015_D", "同期择期手术总例数(不含急诊)", "ASIS", 0, null, 309);

        // --- 5. 核心质量监控 - 安全与死亡 (308/310) ---
        checkAndCreate("QUA014", "急危重症抢救成功率", "EMR", 1, "[QUA014_N] / [QUA014_D] * 100", 308);
        checkAndCreate("QUA014_N", "抢救存活且超过24h人数", "EMR", 0, null, 308);
        checkAndCreate("QUA014_D", "同期实施抢救总人数", "EMR", 0, null, 308);

        checkAndCreate("QUA019", "死亡病例讨论及时率(5日内)", "EMR", 1, "[QUA019_N] / [QUA019_D] * 100", 310);
        checkAndCreate("QUA019_N", "5日内完成死亡讨论数", "EMR", 0, null, 310);
        checkAndCreate("QUA019_D", "同期死亡总人次数", "EMR", 0, null, 310);

        checkAndCreate("QUA020", "医务组讨论与纠纷死亡比值", "EMR", 1, "[QUA020_N] / [QUA020_D]", 310);
        checkAndCreate("QUA020_N", "医务组介入组织讨论次数", "EMR", 0, null, 310);
        checkAndCreate("QUA020_D", "同期发生医疗纠纷的死亡例数", "EMR", 0, null, 310);

        checkAndCreate("QUA021", "科主任主持病例讨论率", "EMR", 1, "[QUA021_N] / [QUA021_D] * 100", 310);
        checkAndCreate("QUA021_N", "科主任亲自主导讨论数", "EMR", 0, null, 310);
        checkAndCreate("QUA021_D", "同期死亡病例讨论总数", "EMR", 0, null, 310);

        // --- 6. 其它服务评价 (501) ---
        checkAndCreate("SVC001", "全院门诊/住院满意度均分", "SAT", 0, null, 501);
        checkAndCreate("HRP_RT_02", "全院配比床护比(实有)", "HRP", 0, null, 401);

        log.info(">>>> [系统初始化] 核心指标体系重构完成 (规范化命名与精细分类)。 <<<<");

    }

    /** 当前库是否已创建 sm_indicator_lib（仅 init.sql 时不会有此表） */
    private boolean smIndicatorLibTableExists() {
        try {
            List<String> names = jdbcTemplate.queryForList("SHOW TABLES LIKE 'sm_indicator_lib'", String.class);
            if (!names.isEmpty()) {
                return true;
            }
        } catch (Exception ignored) {
            // fall through
        }
        try {
            Integer n = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'sm_indicator_lib'",
                    Integer.class);
            return n != null && n > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void fixUserAndRoleMangledData() {
        log.info(">>>> [系统初始化] 正在排查并修复用户/角色中文乱码... <<<<");
        updateUserRealName("president", "王院长");
        updateUserRealName("director_li", "李主任");
        updateUserRealName("wangwu", "王五");
        updateUserRealName("admin", "系统管理员");
        updateRoleName("president", "院长");
        updateRoleName("director", "科室主任");
        updateRoleName("admin", "超级管理员");
    }

    private void fixIndicatorMangledData() {
        log.info(">>>> [系统初始化] 正在排查指标库中文乱码... <<<<");
        updateIndicatorName("ECO001", "全院总收入");
        updateIndicatorName("EFF001", "门急诊总人次");
        updateIndicatorName("QUA014_N", "急危重症患者抢救成功次数");
        updateIndicatorName("QUA014_D", "同期急危重症抢救总人次");
        updateIndicatorName("QUA002_N", "入院8小时内查房及时项");
    }

    private void updateIndicatorName(String code, String name) {
        indicatorService.update(new LambdaUpdateWrapper<SmIndicatorLib>()
                .eq(SmIndicatorLib::getCode, code)
                .set(SmIndicatorLib::getName, name));
    }

    private void updateUserRealName(String username, String realName) {

        userService.update(new LambdaUpdateWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .set(SysUser::getRealName, realName));
    }

    private void updateRoleName(String roleKey, String roleName) {
        roleService.update(new LambdaUpdateWrapper<SysRole>()
                .eq(SysRole::getRoleKey, roleKey)
                .set(SysRole::getRoleName, roleName));
    }

    private void checkAndCreate(String code, String name, String source, int isComposite) {
        checkAndCreate(code, name, source, isComposite, null, 1L);
    }

    private void checkAndCreate(String code, String name, String source, int isComposite, String formula) {
        checkAndCreate(code, name, source, isComposite, formula, 1L);
    }

    private void checkAndCreate(String code, String name, String source, int isComposite, String formula,
            long categoryId) {
        SmIndicatorLib existing = indicatorService.getOne(new LambdaQueryWrapper<SmIndicatorLib>()
                .eq(SmIndicatorLib::getCode, code));

        if (existing == null) {
            log.info(">>>> [指标初始化] 创建指标: {} ({}) <<<<", name, code);
            SmIndicatorLib lib = new SmIndicatorLib();
            lib.setCode(code);
            lib.setName(name);
            lib.setDataSource(source);
            lib.setIsComposite(isComposite);
            lib.setCalcFormula(formula);
            lib.setStatus("0");
            lib.setCategoryId(categoryId);
            indicatorService.save(lib);
        } else {
            // 确保数据源/名称正确，防止名称乱码
            boolean needUpdate = false;
            if (!source.equals(existing.getDataSource())) {
                existing.setDataSource(source);
                needUpdate = true;
            }
            if (!name.equals(existing.getName())) {
                existing.setName(name);
                needUpdate = true;
            }
            if (isComposite != (existing.getIsComposite() == null ? 0 : existing.getIsComposite())) {
                existing.setIsComposite(isComposite);
                needUpdate = true;
            }
            if (formula != null && !formula.equals(existing.getCalcFormula())) {
                existing.setCalcFormula(formula);
                needUpdate = true;
            }
            if (categoryId != (existing.getCategoryId() == null ? 0L : existing.getCategoryId())) {
                existing.setCategoryId(categoryId);
                needUpdate = true;
            }
            if (needUpdate) {
                indicatorService.updateById(existing);
            }
        }
    }
}
