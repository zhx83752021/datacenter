package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.manager.entity.SmIndicatorLib;
import com.smart.manager.mapper.SmIndicatorLibMapper;
import com.smart.manager.service.ISmIndicatorLibService;
import org.springframework.stereotype.Service;

@Service
public class SmIndicatorLibServiceImpl extends ServiceImpl<SmIndicatorLibMapper, SmIndicatorLib>
        implements ISmIndicatorLibService {
}
