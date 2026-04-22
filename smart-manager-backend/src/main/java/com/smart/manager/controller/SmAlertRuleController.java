package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SmAlertRule;
import com.smart.manager.service.ISmAlertRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alert/rule")
@RequiredArgsConstructor
public class SmAlertRuleController {

    private final ISmAlertRuleService alertRuleService;

    /**
     * 获取规则列表
     */
    @PreAuthorize("hasAuthority('sm:alert:rule:list')")
    @GetMapping("/list")
    public Result<IPage<SmAlertRule>> list(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Long indicatorId) {
        LambdaQueryWrapper<SmAlertRule> wrapper = new LambdaQueryWrapper<>();
        if (indicatorId != null) {
            wrapper.eq(SmAlertRule::getIndicatorId, indicatorId);
        }
        return Result.success(alertRuleService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    /**
     * 新增规则
     */
    @PreAuthorize("hasAuthority('sm:alert:rule:add')")
    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody SmAlertRule rule) {
        return Result.success(alertRuleService.save(rule));
    }

    /**
     * 修改规则状态
     */
    @PreAuthorize("hasAuthority('sm:alert:rule:edit')")
    @PutMapping("/switch")
    public Result<Boolean> switchStatus(@RequestBody SmAlertRule rule) {
        // Assume rule only contains ID and status
        return Result.success(alertRuleService.updateById(rule));
    }

    /**
     * 删除规则
     */
    @PreAuthorize("hasAuthority('sm:alert:rule:remove')")
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(alertRuleService.removeById(id));
    }
}
