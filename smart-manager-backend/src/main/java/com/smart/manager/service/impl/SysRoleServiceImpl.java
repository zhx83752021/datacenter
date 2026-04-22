package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.manager.entity.SysRole;
import com.smart.manager.mapper.SysRoleMapper;
import com.smart.manager.service.ISysRoleService;
import org.springframework.stereotype.Service;

/**
 * 角色管理服务实现类
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {
}
