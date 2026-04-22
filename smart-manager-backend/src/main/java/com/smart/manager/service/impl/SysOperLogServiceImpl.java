package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.manager.entity.SysOperLog;
import com.smart.manager.mapper.SysOperLogMapper;
import com.smart.manager.service.ISysOperLogService;
import org.springframework.stereotype.Service;

import org.springframework.scheduling.annotation.Async;

@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements ISysOperLogService {

    @Async
    @Override
    public void saveAsync(SysOperLog operLog) {
        this.save(operLog);
    }
}
