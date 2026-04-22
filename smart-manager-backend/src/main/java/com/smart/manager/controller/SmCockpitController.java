package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.manager.common.Result;
import com.smart.manager.dto.MonitorDataVO;
import com.smart.manager.entity.SmIndicatorCategory;
import com.smart.manager.entity.SmIndicatorLib;
import com.smart.manager.entity.SmIndicatorValue;
import com.smart.manager.service.ISmIndicatorCategoryService;
import com.smart.manager.service.ISmIndicatorLibService;
import com.smart.manager.service.ISmIndicatorValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cockpit")
@RequiredArgsConstructor
public class SmCockpitController {

    private final ISmIndicatorCategoryService categoryService;
    private final ISmIndicatorLibService indicatorService;
    private final ISmIndicatorValueService valueService;

    /**
     * 获取院级KPI指标
     */
    @GetMapping("/president/kpis")
    public Result<List<Map<String, Object>>> getPresidentKPIs() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(createKPI("门急诊人次", "12,450", "人", "8.4%", "up", 85, "kpi-blue", "#3B82F6",
                "M0 20 L20 15 L40 18 L60 10 L80 12 L100 5",
                "M0 20 L20 15 L40 18 L60 10 L80 12 L100 5 L100 24 L0 24 Z"));
        list.add(createKPI("出院人次", "842", "人", "2.1%", "up", 72, "kpi-teal", "#0dbda8",
                "M0 15 L25 18 L50 12 L75 16 L100 8", "M0 15 L25 18 L50 12 L75 16 L100 8 L100 24 L0 24 Z"));
        list.add(createKPI("手术台数", "124", "台", "1.5%", "down", 65, "kpi-orange", "#F59E0B",
                "M0 8 L30 12 L60 18 L100 22", "M0 8 L30 12 L60 18 L100 22 L100 24 L0 24 Z"));
        list.add(createKPI("医疗收入", "2,450", "万", "5.6%", "up", 90, "kpi-coral", "#FF6B6B",
                "M0 22 L20 18 L40 20 L60 14 L80 16 L100 10",
                "M0 22 L20 18 L40 20 L60 14 L80 16 L100 10 L100 24 L0 24 Z"));
        return Result.success(list);
    }

    /**
     * 获取全院收支趋势
     */
    @GetMapping("/president/trend")
    public Result<Map<String, Object>> getPresidentTrend(@RequestParam(required = false) String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("xAxis", java.util.Arrays.asList("09月", "10月", "11月", "12月", "01月", "02月"));
        if ("income".equals(type)) {
            data.put("current", java.util.Arrays.asList(820, 932, 901, 934, 1290, 1330));
            data.put("last", java.util.Arrays.asList(720, 832, 801, 834, 1190, 1230));
        } else {
            data.put("current", java.util.Arrays.asList(420, 532, 601, 534, 790, 830));
            data.put("last", java.util.Arrays.asList(320, 432, 501, 434, 690, 730));
        }
        return Result.success(data);
    }

    /**
     * 获取收入构成数据
     */
    @GetMapping("/president/income-composition")
    public Result<List<Map<String, Object>>> getIncomeComposition() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(createPieData("药品收入", 35, "#0dbda8"));
        list.add(createPieData("耗材收入", 20, "#3B82F6"));
        list.add(createPieData("检查收入", 25, "#F59E0B"));
        list.add(createPieData("治疗收入", 15, "#FF6B6B"));
        list.add(createPieData("其他", 5, "#94a3b8"));
        return Result.success(list);
    }

    /**
     * 获取关键效率指标
     */
    @GetMapping("/president/efficiency")
    public Result<List<Map<String, Object>>> getEfficiencyStats() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(createEfficiency("平均住院日", "7.2", 82, "Timer", "blue", "#3B82F6"));
        list.add(createEfficiency("床位周转次数", "4.2", 65, "Aim", "teal", "#0dbda8"));
        list.add(createEfficiency("术前平均住院日", "1.8", 45, "FirstAidKit", "orange", "#F59E0B"));
        list.add(createEfficiency("药占比", "28.5%", 78, "PieChart", "coral", "#FF6B6B"));
        return Result.success(list);
    }

    /**
     * 获取门诊人次排名
     */
    @GetMapping("/president/rankings")
    public Result<List<Map<String, Object>>> getPresidentRankings() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(createRanking("内科", "5,200", 5.4));
        list.add(createRanking("外科", "4,800", 3.2));
        list.add(createRanking("儿科", "4,100", -1.5));
        list.add(createRanking("妇产科", "3,800", 2.1));
        list.add(createRanking("急诊科", "3,200", -0.8));
        return Result.success(list);
    }

    private Map<String, Object> createKPI(String label, String value, String unit, String rate, String status,
            int percentage, String type, String color, String path, String areaPath) {
        Map<String, Object> map = new HashMap<>();
        map.put("label", label);
        map.put("value", value);
        map.put("unit", unit);
        map.put("rate", rate);
        map.put("status", status);
        map.put("percentage", percentage);
        map.put("type", type);
        map.put("chartColor", color);
        map.put("path", path);
        map.put("areaPath", areaPath);
        return map;
    }

    private Map<String, Object> createPieData(String name, double value, String color) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("value", value);
        map.put("color", color);
        return map;
    }

    private Map<String, Object> createEfficiency(String label, String value, int percent, String icon, String colorType,
            String colorCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("label", label);
        map.put("value", value);
        map.put("percentValue", percent);
        map.put("icon", icon);
        map.put("colorType", colorType);
        map.put("colorCode", colorCode);
        return map;
    }

    private Map<String, Object> createRanking(String dept, String value, double yoy) {
        Map<String, Object> map = new HashMap<>();
        map.put("dept", dept);
        map.put("value", value);
        map.put("yoy", yoy);
        return map;
    }

    // ==================== 科室级驾驶舱接口 ====================

    /**
     * 获取科室运营统计卡片
     */
    @GetMapping("/dept/metrics/{deptId}")
    public Result<List<Map<String, Object>>> getDeptMetrics(@PathVariable String deptId) {
        List<Map<String, Object>> list = new ArrayList<>();
        // 根据科室不同返回不同的模拟数据
        boolean isCardio = "cardio".equals(deptId);
        list.add(createDeptCard("门诊人次", isCardio ? "1,280" : "960", "人", "blue", "User",
                "M0 20 L20 18 L40 15 L60 12 L80 16 L100 10"));
        list.add(createDeptCard("入院人次", isCardio ? "86" : "62", "人", "green", "House",
                "M0 15 L25 18 L50 14 L75 12 L100 8"));
        list.add(createDeptCard("手术台数", isCardio ? "32" : "18", "台", "orange", "Operation",
                "M0 18 L30 15 L60 20 L100 12"));
        list.add(createDeptCard("科室创收", isCardio ? "285.6" : "180.3", "万", "red", "DataLine",
                "M0 22 L20 18 L40 20 L60 14 L80 16 L100 8"));
        return Result.success(list);
    }

    /**
     * 获取科室服务量趋势
     */
    @GetMapping("/dept/trend/{deptId}")
    public Result<Map<String, Object>> getDeptTrend(@PathVariable String deptId,
            @RequestParam(defaultValue = "outpatient") String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("days", java.util.Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日"));
        if ("outpatient".equals(type)) {
            data.put("values", java.util.Arrays.asList(120, 132, 101, 134, 190, 230, 210));
        } else {
            data.put("values", java.util.Arrays.asList(12, 15, 10, 18, 14, 8, 6));
        }
        return Result.success(data);
    }

    /**
     * 获取科室医师绩效
     */
    @GetMapping("/dept/doctors/{deptId}")
    public Result<List<Map<String, Object>>> getDoctorPerformance(@PathVariable String deptId) {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(createDoctor("张建国", "主任医师", 520, 18, 86.5));
        list.add(createDoctor("王丽华", "副主任医师", 480, 15, 72.3));
        list.add(createDoctor("李明远", "副主任医师", 420, 12, 68.1));
        list.add(createDoctor("陈晓燕", "主治医师", 380, 8, 52.4));
        list.add(createDoctor("赵志强", "主治医师", 350, 6, 48.2));
        return Result.success(list);
    }

    /**
     * 获取全院科室排名对比
     */
    @GetMapping("/dept/rankings")
    public Result<Map<String, Object>> getDeptRankings(
            @RequestParam(defaultValue = "income") String metric) {
        Map<String, Object> data = new HashMap<>();
        data.put("categories", java.util.Arrays.asList("心血管内科", "呼吸内科", "骨科", "普外科", "儿科"));
        if ("income".equals(metric)) {
            data.put("values", java.util.Arrays.asList(140, 125, 110, 92, 85));
        } else if ("cmi".equals(metric)) {
            data.put("values", java.util.Arrays.asList(2.8, 2.3, 1.9, 1.5, 1.2));
        } else {
            data.put("values", java.util.Arrays.asList(92, 88, 85, 78, 72));
        }
        return Result.success(data);
    }

    /** 科室卡片构造 */
    private Map<String, Object> createDeptCard(String title, String value, String unit,
            String colorType, String icon, String trendPath) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("value", value);
        map.put("unit", unit);
        map.put("colorType", colorType);
        map.put("icon", icon);
        map.put("trendPath", trendPath);
        return map;
    }

    /** 医师绩效构造 */
    private Map<String, Object> createDoctor(String name, String title, int outpatient,
            int surgery, double income) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("title", title);
        map.put("outpatient", outpatient);
        map.put("surgery", surgery);
        map.put("income", income);
        return map;
    }
}
