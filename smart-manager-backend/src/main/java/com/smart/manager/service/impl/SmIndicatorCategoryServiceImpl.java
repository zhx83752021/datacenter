package com.smart.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.manager.entity.SmIndicatorCategory;
import com.smart.manager.mapper.SmIndicatorCategoryMapper;
import com.smart.manager.service.ISmIndicatorCategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SmIndicatorCategoryServiceImpl extends ServiceImpl<SmIndicatorCategoryMapper, SmIndicatorCategory>
        implements ISmIndicatorCategoryService {

    @Override
    public List<SmIndicatorCategory> buildCategoryTree() {
        // 1. 获取所有可用分类
        LambdaQueryWrapper<SmIndicatorCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmIndicatorCategory::getStatus, "0");
        wrapper.orderByAsc(SmIndicatorCategory::getOrderNum);
        List<SmIndicatorCategory> categories = this.baseMapper.selectList(wrapper);

        // 2. 构建树形结构
        List<SmIndicatorCategory> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<>();

        for (SmIndicatorCategory dept : categories) {
            tempList.add(dept.getId());
        }

        for (SmIndicatorCategory category : categories) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(category.getParentId())) {
                recursionFn(categories, category);
                returnList.add(category);
            }
        }

        if (returnList.isEmpty()) {
            return categories;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SmIndicatorCategory> list, SmIndicatorCategory t) {
        // 得到子节点列表
        List<SmIndicatorCategory> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SmIndicatorCategory tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SmIndicatorCategory> getChildList(List<SmIndicatorCategory> list, SmIndicatorCategory t) {
        List<SmIndicatorCategory> tlist = new ArrayList<>();
        for (SmIndicatorCategory n : list) {
            if (n.getParentId() != null && n.getParentId().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SmIndicatorCategory> list, SmIndicatorCategory t) {
        return getChildList(list, t).size() > 0;
    }
}
