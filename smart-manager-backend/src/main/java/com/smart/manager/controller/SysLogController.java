package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SysOperLog;
import com.smart.manager.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/api/system/log")
@RequiredArgsConstructor
public class SysLogController {

    private final ISysOperLogService operLogService;

    /**
     * 获取操作日志列表
     */
    @PreAuthorize("hasAuthority('system:log:list')")
    @GetMapping("/operation")
    public Result<IPage<SysOperLog>> list(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String title,
            String operName,
            Integer status) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(title)) {
            wrapper.like(SysOperLog::getTitle, title);
        }
        if (StringUtils.hasText(operName)) {
            wrapper.like(SysOperLog::getOperName, operName);
        }
        if (status != null) {
            wrapper.eq(SysOperLog::getStatus, status);
        }
        wrapper.orderByDesc(SysOperLog::getOperTime);
        return Result.success(operLogService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    /**
     * 删除操作日志
     */
    @PreAuthorize("hasAuthority('system:log:remove')")
    @DeleteMapping("/{ids}")
    public Result<Boolean> remove(@PathVariable Long[] ids) {
        return Result.success(operLogService.removeByIds(java.util.Arrays.asList(ids)));
    }

    /**
     * 清空操作日志
     */
    @PreAuthorize("hasAuthority('system:log:remove')")
    @DeleteMapping("/clean")
    public Result<Boolean> clean() {
        operLogService.remove(new LambdaQueryWrapper<>()); // 谨慎操作, 生产环境建议按日期清理
        return Result.success(true);
    }
}
