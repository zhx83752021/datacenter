package com.smart.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.manager.entity.SysDictData;
import java.util.List;

public interface ISysDictDataService extends IService<SysDictData> {
    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictData> selectDictDataByType(String dictType);
}
