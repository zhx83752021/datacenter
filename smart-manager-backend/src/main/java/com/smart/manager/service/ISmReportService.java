package com.smart.manager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.manager.entity.SmReport;

public interface ISmReportService extends IService<SmReport> {
    Page<SmReport> selectReportList(Page<SmReport> page, SmReport report);
}
