package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SysOperLog;
import com.smart.manager.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/log")
@RequiredArgsConstructor
public class SysOperLogController {

    private final ISysOperLogService logService;

    /**
     * 获取操作日志列表
     */
    @PreAuthorize("hasAuthority('system:log:list')")
    @GetMapping("/list")
    public Result<IPage<SysOperLog>> list(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String title,
            String operName) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        if (title != null)
            wrapper.like(SysOperLog::getTitle, title);
        if (operName != null)
            wrapper.like(SysOperLog::getOperName, operName);
        wrapper.orderByDesc(SysOperLog::getOperTime);
        return Result.success(logService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    /**
     * 删除日志
     */
    @PreAuthorize("hasAuthority('system:log:remove')")
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(logService.removeById(id));
    }
}
