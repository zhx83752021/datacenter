package com.smart.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.manager.entity.SysUser;
import java.util.List;

/**
 * 用户管理服务接口
 */
public interface ISysUserService extends IService<SysUser> {
    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUserList(SysUser user);
}
