package com.smart.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.manager.entity.SysMenu;
import java.util.List;

/**
 * 菜单管理服务接口
 */
public interface ISysMenuService extends IService<SysMenu> {
    /**
     * 构建前端路由所需要的树
     */
    List<SysMenu> buildMenuTree(List<SysMenu> menus);
}
