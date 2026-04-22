package com.smart.manager.controller;

import com.smart.manager.annotation.DataScope;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SmReport;
import com.smart.manager.service.ISmReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import java.util.*;

/**
 * 专项报表中心控制器
 */
@RestController
@RequestMapping("/api/sm/report")
@RequiredArgsConstructor
public class SmReportController {

    private final ISmReportService reportService;
    private final com.smart.manager.service.IIndicatorCalcService calcService;

    @DataScope(deptAlias = "sm_report")
    @GetMapping("/list")
    public Result<Page<SmReport>> list(
            SmReport report,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<SmReport> page = new Page<>(pageNum, pageSize);
        return Result.success(reportService.selectReportList(page, report));
    }

    /**
     * 生成/保存报表
     */
    @PostMapping("/generate")
    public Result<SmReport> generate(@RequestBody SmReport report) {
        report.setCreateTime(LocalDateTime.now());
        report.setUpdateTime(LocalDateTime.now());
        if (!StringUtils.hasText(report.getStatus())) {
            report.setStatus("草稿");
        }
        reportService.save(report);
        return Result.success(report);
    }

    /**
     * 删除报表记录
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(reportService.removeById(id));
    }

    /**
     * 导出报表 (真实 Excel 流下载)
     */
    @GetMapping("/export/{id}")
    public void export(@PathVariable Long id, jakarta.servlet.http.HttpServletResponse response)
            throws java.io.IOException {
        SmReport report = reportService.getById(id);
        if (report == null) {
            response.setStatus(404);
            return;
        }

        // 设置响应头
        String fileName = java.net.URLEncoder.encode(report.getName(), java.nio.charset.StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        // 定义表头
        List<List<String>> head = new ArrayList<>();
        head.add(Collections.singletonList("报表名称"));
        head.add(Collections.singletonList("报表类型"));
        head.add(Collections.singletonList("所属科室"));
        head.add(Collections.singletonList("状态"));
        head.add(Collections.singletonList("生成时间"));

        // 填充数据行
        List<List<Object>> rows = new ArrayList<>();
        List<Object> dataRow = new ArrayList<>();
        dataRow.add(report.getName());
        dataRow.add(report.getType());
        dataRow.add(report.getDept());
        dataRow.add(report.getStatus());
        dataRow.add(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        rows.add(dataRow);

        com.alibaba.excel.EasyExcel.write(response.getOutputStream())
                .head(head)
                .sheet("报表数据")
                .doWrite(rows);
    }

    /**
     * 订阅报表 (模拟接口)
     */
    @PostMapping("/subscribe")
    public Result<Boolean> subscribe(@RequestBody Map<String, Object> params) {
        return Result.success(true);
    }

    /**
     * 获取报表模板列表 (模拟接口)
     */
    @GetMapping("/templates")
    public Result<List<Map<String, String>>> templates() {
        return Result.success(Arrays.asList(
                Map.of("id", "1", "name", "月度运营分析模板"),
                Map.of("id", "2", "name", "季度质量监控模板"),
                Map.of("id", "3", "name", "年度业绩考评模板")));
    }
}
