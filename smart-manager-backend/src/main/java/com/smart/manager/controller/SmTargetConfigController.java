package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SmTargetConfig;
import com.smart.manager.service.ISmTargetConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/target/config")
@RequiredArgsConstructor
public class SmTargetConfigController {

    private final ISmTargetConfigService targetConfigService;

    /**
     * 获取目标列表
     */
    @PreAuthorize("hasAuthority('sm:target:list')")
    @GetMapping("/list")
    public Result<IPage<SmTargetConfig>> list(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Long indicatorId) {
        LambdaQueryWrapper<SmTargetConfig> wrapper = new LambdaQueryWrapper<>();
        if (indicatorId != null) {
            wrapper.eq(SmTargetConfig::getIndicatorId, indicatorId);
        }
        return Result.success(targetConfigService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    /**
     * 新增目标
     */
    @PreAuthorize("hasAuthority('sm:target:add')")
    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody SmTargetConfig config) {
        return Result.success(targetConfigService.save(config));
    }

    /**
     * 修改目标
     */
    @PreAuthorize("hasAuthority('sm:target:edit')")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody SmTargetConfig config) {
        return Result.success(targetConfigService.updateById(config));
    }

    /**
     * 删除目标
     */
    @PreAuthorize("hasAuthority('sm:target:remove')")
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(targetConfigService.removeById(id));
    }
}
