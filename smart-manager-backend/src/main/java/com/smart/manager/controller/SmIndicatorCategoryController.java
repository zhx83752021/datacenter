package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SmIndicatorCategory;
import com.smart.manager.service.ISmIndicatorCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/indicator/category")
@RequiredArgsConstructor
public class SmIndicatorCategoryController {

    private final ISmIndicatorCategoryService categoryService;

    /**
     * 获取分类树形结构
     */
    @PreAuthorize("hasAuthority('sm:category:list')")
    @GetMapping("/tree")
    public Result<List<SmIndicatorCategory>> getTree() {
        return Result.success(categoryService.buildCategoryTree());
    }

    /**
     * 获取分类列表（扁平）
     */
    @PreAuthorize("hasAuthority('sm:category:list')")
    @GetMapping("/list")
    public Result<List<SmIndicatorCategory>> list(SmIndicatorCategory category) {
        LambdaQueryWrapper<SmIndicatorCategory> wrapper = new LambdaQueryWrapper<>();
        if (category.getName() != null) {
            wrapper.like(SmIndicatorCategory::getName, category.getName());
        }
        if (category.getStatus() != null) {
            wrapper.eq(SmIndicatorCategory::getStatus, category.getStatus());
        }
        return Result.success(categoryService.list(wrapper));
    }

    /**
     * 新增分类
     */
    @PreAuthorize("hasAuthority('sm:category:add')")
    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody SmIndicatorCategory category) {
        return Result.success(categoryService.save(category));
    }

    /**
     * 修改分类
     */
    @PreAuthorize("hasAuthority('sm:category:edit')")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody SmIndicatorCategory category) {
        return Result.success(categoryService.updateById(category));
    }

    /**
     * 删除分类
     */
    @PreAuthorize("hasAuthority('sm:category:remove')")
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        // TODO: 检查是否有子分类或关联指标
        return Result.success(categoryService.removeById(id));
    }
}
