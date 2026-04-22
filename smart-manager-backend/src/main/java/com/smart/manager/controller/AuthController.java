package com.smart.manager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.manager.common.Result;
import com.smart.manager.entity.SysUser;
import com.smart.manager.mapper.SysUserMapper;
import com.smart.manager.utils.JwtUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SysUserMapper sysUserMapper;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody com.smart.manager.dto.LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            String token = jwtUtils.generateToken(loginRequest.getUsername());
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            return Result.success(data);
        } else {
            return Result.error("登录失败");
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @PostMapping("/refresh")
    public Result<Map<String, String>> refreshCheck(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (jwtUtils.validateToken(token)) {
            String username = jwtUtils.getClaimsByToken(token).getSubject();
            String newToken = jwtUtils.generateToken(username);
            Map<String, String> data = new HashMap<>();
            data.put("token", newToken);
            return Result.success(data);
        }
        return Result.error("Invalid token");
    }

    @PostMapping("/updatePassword")
    public Result<Void> updatePassword(@RequestBody com.smart.manager.dto.UpdatePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return Result.error("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        sysUserMapper.updateById(user);

        return Result.success();
    }

    @PostMapping("/updateProfile")
    public Result<Void> updateProfile(@RequestBody com.smart.manager.dto.UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }

        user.setRealName(request.getRealName());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());
        sysUserMapper.updateById(user);

        return Result.success();
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", user.getRealName());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("mobile", user.getMobile());
        data.put("email", user.getEmail());
        data.put("empNo", user.getEmpNo());
        data.put("status", user.getStatus());
        data.put("avatar", user.getAvatar() != null ? user.getAvatar()
                : "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");

        // Extract roles and permissions from authorities
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        authentication.getAuthorities().forEach(auth -> {
            String authority = auth.getAuthority();
            if (authority.startsWith("ROLE_")) {
                roles.add(authority.substring(5)); // Remove "ROLE_" prefix
            } else {
                permissions.add(authority);
            }
        });

        if (roles.isEmpty()) {
            roles.add("common"); // Default fallback
        }

        data.put("roles", roles);
        data.put("permissions", permissions);

        return Result.success(data);
    }
}
