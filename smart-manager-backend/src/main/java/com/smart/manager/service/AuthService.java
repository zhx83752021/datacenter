package com.smart.manager.service;

import com.smart.manager.entity.SysUser;
import com.smart.manager.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smart.manager.mapper.SysRoleMapper;
import com.smart.manager.mapper.SysMenuMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.smart.manager.entity.LoginUser;
import com.smart.manager.entity.SysRole;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String name = username != null ? username.trim() : null;
        // 不走 BaseMapper：旧库 del_flag 可能为 NULL，MyBatis-Plus TableLogic 会筛掉导致永远「密码错误」
        SysUser user = findUserForLogin(name);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // 获取角色信息
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(user.getId());
        List<String> roleKeys = roles.stream().map(SysRole::getRoleKey).collect(Collectors.toList());

        // 获取权限信息
        List<String> perms = sysMenuMapper.selectPermsByUserId(user.getId());

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roleKeys != null) {
            roleKeys.forEach(roleKey -> authorities.add(new SimpleGrantedAuthority("ROLE_" + roleKey)));
        }
        if (perms != null) {
            perms.forEach(perm -> authorities.add(new SimpleGrantedAuthority(perm)));
        }

        // 为 admin 提供临时超级权限和具体的模块权限，绕过精确匹配导致的 403
        if ("admin".equals(name)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_admin"));
            authorities.add(new SimpleGrantedAuthority("*:*:*"));
            authorities.add(new SimpleGrantedAuthority("sm:monitor:query"));
            authorities.add(new SimpleGrantedAuthority("sm:monitor:trend"));
            authorities.add(new SimpleGrantedAuthority("sm:cockpit:president"));
            authorities.add(new SimpleGrantedAuthority("sm:cockpit:director"));
            authorities.add(new SimpleGrantedAuthority("sm:indicator:list"));
            authorities.add(new SimpleGrantedAuthority("sm:indicator:query"));
            authorities.add(new SimpleGrantedAuthority("sm:indicator:add"));
            authorities.add(new SimpleGrantedAuthority("sm:indicator:edit"));
            authorities.add(new SimpleGrantedAuthority("sm:indicator:remove"));
            authorities.add(new SimpleGrantedAuthority("sm:alert:rule:list"));
            authorities.add(new SimpleGrantedAuthority("sm:alert:rule:add"));
            authorities.add(new SimpleGrantedAuthority("sm:alert:rule:edit"));
            authorities.add(new SimpleGrantedAuthority("sm:alert:rule:remove"));
        }

        return new LoginUser(user, roles, perms, authorities);
    }

    private SysUser findUserForLogin(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        var sql = "SELECT * FROM sys_user WHERE username = ? AND (del_flag IS NULL OR del_flag = 0) LIMIT 1";
        var list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SysUser.class), username);
        return list.isEmpty() ? null : list.get(0);
    }
}
