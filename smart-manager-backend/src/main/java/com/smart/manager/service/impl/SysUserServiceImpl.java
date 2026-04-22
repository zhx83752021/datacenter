package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.manager.annotation.DataScope;
import com.smart.manager.entity.SysUser;
import com.smart.manager.mapper.SysUserMapper;
import com.smart.manager.service.ISysUserService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 用户管理服务实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUserList(SysUser user) {
        // 注：由于目前主要使用 MyBatis-Plus 的 IService 默认实现，
        // 这里只是为了演示 @DataScope 的注入效果。
        // 实际上需要在 Mapper 中定义具体的 SQL 并在 XML 或注解中引用 ${params.dataScope}
        return baseMapper.selectList(null); // 临时替代
    }
}
