package com.smart.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manager.entity.SmReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SmReportMapper extends BaseMapper<SmReport> {
    List<SmReport> selectReportList(Page<SmReport> page, @Param("report") SmReport report);
}
