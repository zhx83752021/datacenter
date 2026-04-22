package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SysUser;
import com.smart.manager.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.smart.manager.common.annotation.Log;
import com.smart.manager.common.enums.BusinessType;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final ISysUserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 获取用户列表
     */
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/list")
    public Result<IPage<SysUser>> list(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String username,
            String realName,
            String mobile) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (StringUtils.hasText(realName)) {
            wrapper.like(SysUser::getRealName, realName);
        }
        if (StringUtils.hasText(mobile)) {
            wrapper.like(SysUser::getMobile, mobile);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return Result.success(userService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    /**
     * 获取用户详情
     */
    @PreAuthorize("hasAuthority('system:user:query')")
    @GetMapping("/{id}")
    public Result<SysUser> getInfo(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    /**
     * 新增用户
     */
    @PreAuthorize("hasAuthority('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Boolean> add(@RequestBody SysUser user) {
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return Result.success(userService.save(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("hasAuthority('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Boolean> edit(@RequestBody SysUser user) {
        // 不允许通过此接口修改密码
        user.setPassword(null);
        return Result.success(userService.updateById(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("hasAuthority('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public Result<Boolean> resetPwd(@RequestBody SysUser user) {
        SysUser updateDoc = new SysUser();
        updateDoc.setId(user.getId());
        updateDoc.setPassword(passwordEncoder.encode(user.getPassword()));
        return Result.success(userService.updateById(updateDoc));
    }
}
