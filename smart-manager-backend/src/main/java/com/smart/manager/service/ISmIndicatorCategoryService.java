package com.smart.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.manager.entity.SmIndicatorCategory;

import java.util.List;

public interface ISmIndicatorCategoryService extends IService<SmIndicatorCategory> {
    /**
     * 获取分类树形结构
     */
    List<SmIndicatorCategory> buildCategoryTree();
}
