package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.manager.annotation.DataScope;
import com.smart.manager.entity.SmReport;
import com.smart.manager.mapper.SmReportMapper;
import com.smart.manager.service.ISmReportService;
import org.springframework.stereotype.Service;

@Service
public class SmReportServiceImpl extends ServiceImpl<SmReportMapper, SmReport> implements ISmReportService {

    @Override
    @DataScope(deptAlias = "sm_report")
    public Page<SmReport> selectReportList(Page<SmReport> page, SmReport report) {
        return page.setRecords(baseMapper.selectReportList(page, report));
    }
}
