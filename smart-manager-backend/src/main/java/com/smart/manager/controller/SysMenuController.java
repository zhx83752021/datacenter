package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SysMenu;
import com.smart.manager.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final ISysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/list")
    public Result<List<SysMenu>> list(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(menu.getMenuName())) {
            wrapper.like(SysMenu::getMenuName, menu.getMenuName());
        }
        if (StringUtils.hasText(menu.getStatus())) {
            wrapper.eq(SysMenu::getStatus, menu.getStatus());
        }
        wrapper.orderByAsc(SysMenu::getParentId).orderByAsc(SysMenu::getOrderNum);
        return Result.success(menuService.list(wrapper));
    }

    /**
     * 获取菜单详情
     */
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/{id}")
    public Result<SysMenu> getInfo(@PathVariable Long id) {
        return Result.success(menuService.getById(id));
    }

    /**
     * 获取菜单树
     */
    @GetMapping("/treelist")
    public Result<List<SysMenu>> treelist(SysMenu menu) {
        List<SysMenu> menus = menuService.list();
        return Result.success(menuService.buildMenuTree(menus));
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping
    public Result<Boolean> add(@RequestBody SysMenu menu) {
        return Result.success(menuService.save(menu));
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping
    public Result<Boolean> edit(@RequestBody SysMenu menu) {
        return Result.success(menuService.updateById(menu));
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("hasAuthority('system:menu:remove')")
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        // TODO: 校验是否有子菜单
        return Result.success(menuService.removeById(id));
    }
}
