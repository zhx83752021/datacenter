package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.manager.entity.SmAlertMessage;
import com.smart.manager.mapper.SmAlertMessageMapper;
import com.smart.manager.service.ISmAlertMessageService;
import org.springframework.stereotype.Service;

@Service
public class SmAlertMessageServiceImpl extends ServiceImpl<SmAlertMessageMapper, SmAlertMessage>
        implements ISmAlertMessageService {
}
