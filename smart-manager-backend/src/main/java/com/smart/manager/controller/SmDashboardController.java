package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SmDashboard;
import com.smart.manager.service.ISmDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 看板配置管理核心控制器
 */
@RestController
@RequestMapping("/api/sm/dashboard")
@RequiredArgsConstructor
public class SmDashboardController {

    private final ISmDashboardService dashboardService;

    /**
     * 获取看板列表
     */
    @GetMapping("/list")
    public Result<Page<SmDashboard>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {

        LambdaQueryWrapper<SmDashboard> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), SmDashboard::getName, name)
                .eq(StringUtils.hasText(category), SmDashboard::getCategory, category)
                .eq(StringUtils.hasText(status), SmDashboard::getStatus, status)
                .orderByDesc(SmDashboard::getUpdateTime);

        Page<SmDashboard> page = new Page<>(pageNum, pageSize);
        return Result.success(dashboardService.page(page, wrapper));
    }

    /**
     * 根据ID获取看板配置
     */
    @GetMapping("/config/{id}")
    public Result<SmDashboard> getConfig(@PathVariable Long id) {
        return Result.success(dashboardService.getById(id));
    }

    /**
     * 保存/更新看板
     */
    @PostMapping("/save")
    public Result<SmDashboard> save(@RequestBody SmDashboard dashboard) {
        dashboardService.saveOrUpdate(dashboard);
        return Result.success(dashboard);
    }

    /**
     * 发布/草稿状态切换与复制
     * 为简单起见，这里假设直接通过 saveOrUpdate 保存发布状态亦可，但也提供快速状态切换接口。
     */
    @PutMapping("/publish")
    public Result<Boolean> publish(@RequestBody SmDashboard dashboard) {
        // 由于只需要更新状态，可以使用 updateById
        SmDashboard updateDef = new SmDashboard();
        updateDef.setId(dashboard.getId());
        updateDef.setStatus(dashboard.getStatus());
        return Result.success(dashboardService.updateById(updateDef));
    }

    /**
     * 删除看板
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(dashboardService.removeById(id));
    }

    /**
     * 获取“我的看板” (当前用户可见的)
     */
    @GetMapping("/my")
    public Result<List<SmDashboard>> getMyDashboards() {
        // 简化实现：返回所有已发布的看板作为“我的看板”
        LambdaQueryWrapper<SmDashboard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmDashboard::getStatus, "published")
                .orderByDesc(SmDashboard::getUpdateTime);
        return Result.success(dashboardService.list(wrapper));
    }

    /**
     * 授权管理 (简化占位)
     */
    @PostMapping("/authorize")
    public Result<Boolean> authorize(@RequestBody java.util.Map<String, Object> params) {
        return Result.success(true);
    }

    /**
     * 预览看板
     */
    @GetMapping("/preview/{id}")
    public Result<SmDashboard> preview(@PathVariable Long id) {
        return Result.success(dashboardService.getById(id));
    }
}
