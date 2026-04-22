package com.smart.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.manager.entity.SysOperLog;

public interface ISysOperLogService extends IService<SysOperLog> {
    /**
     * 异步保存操作日志
     * 
     * @param operLog 日志对象
     */
    void saveAsync(SysOperLog operLog);
}
