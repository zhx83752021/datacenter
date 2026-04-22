package com.smart.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.manager.entity.SmIndicatorValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SmIndicatorValueMapper extends BaseMapper<SmIndicatorValue> {
    List<SmIndicatorValue> selectValueList(@Param("val") SmIndicatorValue val);
}
