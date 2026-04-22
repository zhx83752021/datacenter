package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.manager.entity.SmFile;
import com.smart.manager.mapper.SmFileMapper;
import com.smart.manager.service.ISmFileService;
import org.springframework.stereotype.Service;

@Service
public class SmFileServiceImpl extends ServiceImpl<SmFileMapper, SmFile> implements ISmFileService {
}
