package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SysDictData;
import com.smart.manager.entity.SysDictType;
import com.smart.manager.service.ISysDictDataService;
import com.smart.manager.service.ISysDictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final ISysDictTypeService dictTypeService;
    private final ISysDictDataService dictDataService;

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/data/type/{dictType}")
    public Result<List<SysDictData>> dictType(@PathVariable String dictType) {
        return Result.success(dictDataService.selectDictDataByType(dictType));
    }

    /**
     * 获取字典类型列表
     */
    @PreAuthorize("hasAuthority('system:dict:list')")
    @GetMapping("/type/list")
    public Result<IPage<SysDictType>> listType(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String dictName,
            String dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        if (dictName != null)
            wrapper.like(SysDictType::getDictName, dictName);
        if (dictType != null)
            wrapper.like(SysDictType::getDictType, dictType);
        return Result.success(dictTypeService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("hasAuthority('system:dict:add')")
    @PostMapping("/type")
    public Result<Boolean> addType(@RequestBody SysDictType dict) {
        return Result.success(dictTypeService.save(dict));
    }

    /**
     * 修改字典类型
     */
    @PreAuthorize("hasAuthority('system:dict:edit')")
    @PutMapping("/type")
    public Result<Boolean> editType(@RequestBody SysDictType dict) {
        return Result.success(dictTypeService.updateById(dict));
    }

    /**
     * 删除字典类型
     */
    @PreAuthorize("hasAuthority('system:dict:remove')")
    @DeleteMapping("/type/{id}")
    public Result<Boolean> removeType(@PathVariable Long id) {
        return Result.success(dictTypeService.removeById(id));
    }

    /**
     * 获取字典数据列表
     */
    @PreAuthorize("hasAuthority('system:dict:list')")
    @GetMapping("/data/list")
    public Result<IPage<SysDictData>> listData(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String dictType,
            String dictLabel) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        if (dictType != null)
            wrapper.eq(SysDictData::getDictType, dictType);
        if (dictLabel != null)
            wrapper.like(SysDictData::getDictLabel, dictLabel);
        wrapper.orderByAsc(SysDictData::getDictSort);
        return Result.success(dictDataService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    /**
     * 新增字典数据
     */
    @PreAuthorize("hasAuthority('system:dict:add')")
    @PostMapping("/data")
    public Result<Boolean> addData(@RequestBody SysDictData dict) {
        return Result.success(dictDataService.save(dict));
    }

    /**
     * 修改字典数据
     */
    @PreAuthorize("hasAuthority('system:dict:edit')")
    @PutMapping("/data")
    public Result<Boolean> editData(@RequestBody SysDictData dict) {
        return Result.success(dictDataService.updateById(dict));
    }

    /**
     * 删除字典数据
     */
    @PreAuthorize("hasAuthority('system:dict:remove')")
    @DeleteMapping("/data/{id}")
    public Result<Boolean> removeData(@PathVariable Long id) {
        return Result.success(dictDataService.removeById(id));
    }
}
