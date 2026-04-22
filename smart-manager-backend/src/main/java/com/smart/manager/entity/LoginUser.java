package com.smart.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

/**
 * 登录用户信息
 */
@Getter
@Setter
public class LoginUser extends User {

    private SysUser user;

    /**
     * 权限列表
     */
    private List<String> permissions;

    /**
     * 角色列表
     */
    private List<SysRole> roles;

    public LoginUser(SysUser user, List<SysRole> roles, List<String> permissions,
            Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), authorities);
        this.user = user;
        this.roles = roles;
        this.permissions = permissions;
    }
}
