package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SmIndicatorLib;
import com.smart.manager.service.ISmIndicatorLibService;
import com.smart.manager.service.IIndicatorCalcService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.smart.manager.annotation.DataScope;

@RestController
@RequestMapping("/api/indicator")
@RequiredArgsConstructor
public class SmIndicatorController {

    private final ISmIndicatorLibService indicatorService;
    private final IIndicatorCalcService calcService;
    private final com.smart.manager.service.IEtlSyncService etlSyncService;

    /**
     * 手动触发外部数据源同步 (HIS/EMR/LIS)
     */
    // @PreAuthorize("hasAuthority('sm:indicator:edit')")
    @PostMapping("/sync")
    public Result<Integer> syncSourceData(@RequestParam String periodDate) {
        int count = etlSyncService.syncBaseData(periodDate);
        return Result.success(count);
    }

    /**
     * 获取指标列表
     */
    // @DataScope
    // @PreAuthorize
    @GetMapping("/list")
    public Result<Page<SmIndicatorLib>> list(
            SmIndicatorLib indicator,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<SmIndicatorLib> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SmIndicatorLib> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(indicator.getName())) {
            wrapper.like(SmIndicatorLib::getName, indicator.getName());
        }
        if (StringUtils.hasText(indicator.getCode())) {
            wrapper.like(SmIndicatorLib::getCode, indicator.getCode());
        }
        if (indicator.getCategoryId() != null) {
            wrapper.eq(SmIndicatorLib::getCategoryId, indicator.getCategoryId());
        }
        wrapper.orderByDesc(SmIndicatorLib::getCreateTime);

        return Result.success(indicatorService.page(page, wrapper));
    }

    /**
     * 获取详细信息
     */
    // @PreAuthorize
    @GetMapping(value = "/{id}")
    public Result<SmIndicatorLib> getInfo(@PathVariable("id") Long id) {
        return Result.success(indicatorService.getById(id));
    }

    /**
     * 新增指标
     */
    @PreAuthorize("hasAuthority('sm:indicator:add')")
    @PostMapping("/save")
    public Result<Boolean> add(@RequestBody SmIndicatorLib indicator) {
        if ("1".equals(String.valueOf(indicator.getIsComposite()))) {
            if (!calcService.validateFormula(indicator.getCalcFormula())) {
                return Result.error("指标公式格式不正确，请使用 [CODE] 并在其中包含有效运算符");
            }
        }
        return Result.success(indicatorService.save(indicator));
    }

    /**
     * 修改指标
     */
    // @PreAuthorize("hasAuthority('sm:indicator:edit')")
    @PutMapping("/update")
    public Result<Boolean> edit(@RequestBody SmIndicatorLib indicator) {
        if ("1".equals(String.valueOf(indicator.getIsComposite()))) {
            if (!calcService.validateFormula(indicator.getCalcFormula())) {
                return Result.error("指标公式格式不正确");
            }
        }
        return Result.success(indicatorService.updateById(indicator));
    }

    /**
     * 删除指标
     */
    // @PreAuthorize
    @DeleteMapping("/{ids}")
    public Result<Boolean> remove(@PathVariable List<Long> ids) {
        return Result.success(indicatorService.removeBatchByIds(ids));
    }

    /**
     * 获取指标关联属性图谱
     */
    // @DataScope
    @GetMapping("/graph")
    public Result<java.util.Map<String, Object>> getGraphData(SmIndicatorLib indicator) {
        List<SmIndicatorLib> list = indicatorService.list();
        java.util.List<java.util.Map<String, Object>> nodes = new java.util.ArrayList<>();
        java.util.List<java.util.Map<String, Object>> links = new java.util.ArrayList<>();

        for (SmIndicatorLib item : list) {
            java.util.Map<String, Object> node = new java.util.HashMap<>();
            node.put("id", item.getCode());
            node.put("name", item.getName());
            node.put("category", item.getCategoryId());
            nodes.add(node);

            if (org.springframework.util.StringUtils.hasText(item.getCalcFormula())) {
                java.util.List<String> refs = cn.hutool.core.util.ReUtil.findAll("\\[(.*?)\\]", item.getCalcFormula(),
                        1);
                for (String ref : refs) {
                    java.util.Map<String, Object> link = new java.util.HashMap<>();
                    link.put("source", ref);
                    link.put("target", item.getCode());
                    link.put("label", "支撑");
                    links.add(link);
                }
            }
        }

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("nodes", nodes);
        result.put("links", links);
        return Result.success(result);
    }

    /**
     * 手动触发指标计算
     */
    // @PreAuthorize
    @PostMapping("/calculate")
    public Result<java.math.BigDecimal> calculate(
            @RequestParam Long id,
            @RequestParam String periodDate,
            @RequestParam(required = false) String deptCode) {
        SmIndicatorLib indicator = indicatorService.getById(id);
        if (indicator == null)
            return Result.error("指标不存在");

        java.math.BigDecimal val = calcService.calculate(indicator, periodDate, deptCode);
        return Result.success(val);
    }

    /**
     * 批量导入指标定义
     */
    @PreAuthorize("hasAuthority('sm:indicator:add')")
    @PostMapping("/batchImport")
    public Result<Boolean> batchImport(@RequestBody List<SmIndicatorLib> list) {
        for (SmIndicatorLib indicator : list) {
            if ("1".equals(String.valueOf(indicator.getIsComposite()))) {
                if (!calcService.validateFormula(indicator.getCalcFormula())) {
                    return Result.error("指标 [" + indicator.getCode() + "] 公式格式不正确");
                }
            }
        }
        return Result.success(indicatorService.saveBatch(list));
    }

    /**
     * 导出指标定义列表
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<SmIndicatorLib> list = indicatorService.list();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("指标知识库_" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), SmIndicatorLib.class).sheet("指标列表").doWrite(list);
    }
}
