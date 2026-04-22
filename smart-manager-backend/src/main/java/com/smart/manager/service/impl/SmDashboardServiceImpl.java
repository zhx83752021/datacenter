package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.manager.entity.SmDashboard;
import com.smart.manager.mapper.SmDashboardMapper;
import com.smart.manager.service.ISmDashboardService;
import org.springframework.stereotype.Service;

/**
 * 看板配置服务实现
 */
@Service
public class SmDashboardServiceImpl extends ServiceImpl<SmDashboardMapper, SmDashboard> implements ISmDashboardService {
}
