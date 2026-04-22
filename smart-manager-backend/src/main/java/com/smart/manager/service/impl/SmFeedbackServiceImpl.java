package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.manager.entity.SmFeedback;
import com.smart.manager.mapper.SmFeedbackMapper;
import com.smart.manager.service.ISmFeedbackService;
import org.springframework.stereotype.Service;

@Service
public class SmFeedbackServiceImpl extends ServiceImpl<SmFeedbackMapper, SmFeedback> implements ISmFeedbackService {
}
