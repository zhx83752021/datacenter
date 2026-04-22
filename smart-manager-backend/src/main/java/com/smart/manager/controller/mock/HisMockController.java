package com.smart.manager.controller.mock;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smart.manager.common.Result;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * HIS 模拟系统适配器 Controller
 * 用于演示环境中生成符合《01_HIS系统数据需求清单》标准规范的 JSON 数据流
 */
@RestController
@RequestMapping("/api/mock/his")
public class HisMockController {

    private final Random random = new Random();

    /**
     * HIS-01 门诊挂号统计
     */
    @GetMapping("/outpatient/statistics")
    public Result<Map<String, Object>> getOutpatientStatistics(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "deptCode", required = false) String deptCode) {

        // 门急诊挂号量基数产生随机化波动
        int total = 2000 + random.nextInt(1500);
        int emergency = (int) (total * 0.15);
        int normal = total - emergency;

        Map<String, Object> data = new HashMap<>();
        data.put("totalVisits", total);
        data.put("emergencyVisits", emergency);
        data.put("normalVisits", normal);

        // 模拟各科室人次拆分数组
        List<Map<String, Object>> deptBreakdown = new ArrayList<>();

        // 当传入特定科室时，只返回该科室
        if (deptCode != null && !deptCode.isEmpty()) {
            deptBreakdown.add(createDeptVisits(deptCode, "特定科室", total));
            data.put("deptBreakdown", deptBreakdown);
            return Result.success(data);
        }

        // 全院统计：根据我们定义的字典随机分配几大核心科室的接诊量
        deptBreakdown.add(createDeptVisits("PED", "儿内科", (int) (total * 0.4)));
        deptBreakdown.add(createDeptVisits("SUR", "小儿外科", (int) (total * 0.2)));
        deptBreakdown.add(createDeptVisits("OBS", "产科", (int) (total * 0.2)));
        deptBreakdown.add(createDeptVisits("FMD", "发热门诊", (int) (total * 0.1)));
        deptBreakdown.add(createDeptVisits("OTHER", "其他", total - (int) (total * 0.9)));

        data.put("deptBreakdown", deptBreakdown);
        return Result.success(data);
    }

    /**
     * HIS-02 入出院统计
     */
    @GetMapping("/inpatient/statistics")
    public Result<Map<String, Object>> getInpatientStatistics(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

        int admissions = 300 + random.nextInt(100);
        int discharges = 280 + random.nextInt(120);
        int currentInpatients = 1200 + random.nextInt(200);

        Map<String, Object> data = new HashMap<>();
        data.put("admissions", admissions);
        data.put("discharges", discharges);
        data.put("currentInpatients", currentInpatients);

        // 模拟转科明细记录 (如 产科 -> 重症监护)
        List<Map<String, Object>> transfers = new ArrayList<>();
        int transferCount = 10 + random.nextInt(10);
        for (int i = 0; i < transferCount; i++) {
            Map<String, Object> transfer = new HashMap<>();
            transfer.put("patientId", "PID" + (10000 + random.nextInt(9000)));
            transfer.put("admitTime",
                    LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            transfer.put("transferTime",
                    LocalDateTime.now().minusHours(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            transfer.put("fromDept", "OBS");
            transfer.put("toDept", "ICU");
            transfer.put("isWithin48h", random.nextBoolean()); // 测试核心判定条件
            transfers.add(transfer);
        }
        data.put("transfers", transfers);

        return Result.success(data);
    }

    private Map<String, Object> createDeptVisits(String code, String name, int visits) {
        Map<String, Object> dept = new HashMap<>();
        dept.put("deptCode", code);
        dept.put("deptName", name);
        dept.put("visits", visits);
        return dept;
    }
}
