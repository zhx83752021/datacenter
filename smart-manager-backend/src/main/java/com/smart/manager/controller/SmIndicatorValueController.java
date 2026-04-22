package com.smart.manager.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.smart.manager.common.Result;
import com.smart.manager.dto.IndicatorValueImportDTO;
import com.smart.manager.entity.SmIndicatorLib;
import com.smart.manager.entity.SmIndicatorValue;
import com.smart.manager.service.ISmIndicatorLibService;
import com.smart.manager.service.ISmIndicatorValueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/indicator/value")
@RequiredArgsConstructor
public class SmIndicatorValueController {

    private final ISmIndicatorValueService valueService;
    private final ISmIndicatorLibService indicatorLibService;

    /**
     * 手工录入/批量保存指标数据
     */
    @PreAuthorize("hasAuthority('sm:indicator:add')")
    @PostMapping("/save")
    public Result<Boolean> saveBatch(@RequestBody List<SmIndicatorValue> values) {
        // 简单实现：批量保存或更新
        // 建议前端在更新时带上 ID，或者后端根据 unique key (indicatorId + period + dept) 查找 ID
        // 这里暂时直接只用 saveOrUpdateBatch
        return Result.success(valueService.saveOrUpdateBatch(values));
    }

    /**
     * Excel导入指标数据
     */
    @PreAuthorize("hasAuthority('sm:indicator:import')")
    @PostMapping("/import")
    public Result<String> importData(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 获取所有指标编码映射 (Performance: fetch all might be slow if huge lib, but
            // indicator lib usually small < 1000)
            Map<String, Long> codeMap = indicatorLibService.list().stream()
                    .collect(Collectors.toMap(
                            SmIndicatorLib::getCode,
                            SmIndicatorLib::getId,
                            (v1, v2) -> v1));

            List<SmIndicatorValue> saveList = new ArrayList<>();
            List<String> errorList = new ArrayList<>();

            // 2. 读取Excel
            EasyExcel.read(file.getInputStream(), IndicatorValueImportDTO.class,
                    new PageReadListener<IndicatorValueImportDTO>(dataList -> {
                        for (IndicatorValueImportDTO dto : dataList) {
                            Long indicatorId = codeMap.get(dto.getIndicatorCode());
                            if (indicatorId == null) {
                                // errorList.add("未知指标编码: " + dto.getIndicatorCode());
                                // Log error or skip
                                continue;
                            }

                            SmIndicatorValue value = new SmIndicatorValue();
                            value.setIndicatorId(indicatorId);
                            value.setDeptCode(dto.getDeptCode());
                            value.setPeriodDate(dto.getPeriodDate());
                            value.setValue(dto.getValue());

                            saveList.add(value);
                        }
                    })).sheet().doRead();

            // 3. 批量保存
            if (!saveList.isEmpty()) {
                // TODO: 考虑去重逻辑 (UPSERT)
                valueService.saveBatch(saveList);
                return Result.success("成功导入 " + saveList.size() + " 条数据");
            } else {
                return Result.error("未解析到有效数据");
            }

        } catch (IOException e) {
            log.error("导入失败", e);
            return Result.error("导入失败: " + e.getMessage());
        }
    }
}
