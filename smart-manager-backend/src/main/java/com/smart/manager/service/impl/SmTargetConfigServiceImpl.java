package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.manager.entity.SmTargetConfig;
import com.smart.manager.mapper.SmTargetConfigMapper;
import com.smart.manager.service.ISmTargetConfigService;
import org.springframework.stereotype.Service;

@Service
public class SmTargetConfigServiceImpl extends ServiceImpl<SmTargetConfigMapper, SmTargetConfig>
        implements ISmTargetConfigService {
}
