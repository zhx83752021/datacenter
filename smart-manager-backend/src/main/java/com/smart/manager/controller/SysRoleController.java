package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SysRole;
import com.smart.manager.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.smart.manager.common.annotation.Log;
import com.smart.manager.common.enums.BusinessType;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final ISysRoleService roleService;

    /**
     * 获取角色列表
     */
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/list")
    public Result<IPage<SysRole>> list(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String roleName,
            String roleKey) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(roleName)) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        if (StringUtils.hasText(roleKey)) {
            wrapper.like(SysRole::getRoleKey, roleKey);
        }
        wrapper.orderByAsc(SysRole::getRoleSort);
        return Result.success(roleService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    /**
     * 获取角色详情
     */
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/{id}")
    public Result<SysRole> getInfo(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    /**
     * 新增角色
     */
    @PreAuthorize("hasAuthority('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Boolean> add(@RequestBody SysRole role) {
        return Result.success(roleService.save(role));
    }

    /**
     * 修改角色
     */
    @PreAuthorize("hasAuthority('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Boolean> edit(@RequestBody SysRole role) {
        return Result.success(roleService.updateById(role));
    }

    /**
     * 删除角色
     */
    @PreAuthorize("hasAuthority('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        // TODO: 校验角色是否正在被使用
        return Result.success(roleService.removeById(id));
    }

    /**
     * 修改保存数据权限
     */
    @PreAuthorize("hasAuthority('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public Result<Boolean> dataScope(@RequestBody SysRole role) {
        return Result.success(roleService.updateById(role));
    }

    /**
     * 获取所有角色（下拉框使用）
     */
    @GetMapping("/optionselect")
    public Result<List<SysRole>> optionselect() {
        return Result.success(roleService.list());
    }
}
