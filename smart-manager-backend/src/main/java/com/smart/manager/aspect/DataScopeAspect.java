package com.smart.manager.aspect;

import com.smart.manager.annotation.DataScope;
import com.smart.manager.entity.LoginUser;
import com.smart.manager.entity.SysRole;
import com.smart.manager.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 数据权限过滤切面
 */
@Aspect
@Component
@Slf4j
public class DataScopeAspect {

    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    @SuppressWarnings("unchecked")
    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint joinPoint, DataScope controllerDataScope) throws Throwable {
        clearDataScope(joinPoint);
        handleDataScope(joinPoint, controllerDataScope);
    }

    @SuppressWarnings("unchecked")
    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope) {
        // [TEST ONLY] 模拟环境下跳过数据权限拦截
        if (true)
            return;

        // 获取当前登录的用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            return;
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        SysUser user = loginUser.getUser();

        // 如果是超级管理员，则不过滤数据
        if (user != null && "admin".equals(user.getUsername())) {
            return;
        }

        StringBuilder sqlString = new StringBuilder();

        for (SysRole role : loginUser.getRoles()) {
            String dataScope = role.getDataScope();
            if (DATA_SCOPE_ALL.equals(dataScope)) {
                sqlString = new StringBuilder();
                break;
            } else if (DATA_SCOPE_CUSTOM.equals(dataScope)) {
                // 自定义数据权限逻辑 (根据角色部门关联表过滤)
                sqlString.append(String.format(
                        " OR %s.id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = %d ) ",
                        controllerDataScope.deptAlias(), role.getId()));
            } else if (DATA_SCOPE_DEPT.equals(dataScope)) {
                sqlString.append(String.format(" OR %s.id = %d ",
                        controllerDataScope.deptAlias(), user.getDeptId()));
            } else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
                sqlString.append(String.format(
                        " OR %s.id IN ( SELECT id FROM sys_dept WHERE id = %d or find_in_set( %d , ancestors ) ) ",
                        controllerDataScope.deptAlias(), user.getDeptId(), user.getDeptId()));
            } else if (DATA_SCOPE_SELF.equals(dataScope)) {
                if (StringUtils.hasText(controllerDataScope.userAlias())) {
                    sqlString.append(String.format(" OR %s.id = %d ",
                            controllerDataScope.userAlias(), user.getId()));
                } else {
                    // 数据权限仅本人且没有用户别名时，赋予一个不成立的条件
                    sqlString.append(" OR 1=0 ");
                }
            }
        }

        if (StringUtils.hasText(sqlString.toString())) {
            Object arg = joinPoint.getArgs()[0];
            if (arg instanceof com.smart.manager.entity.BaseEntity) {
                com.smart.manager.entity.BaseEntity baseEntity = (com.smart.manager.entity.BaseEntity) arg;
                baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
            } else if (arg instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) arg;
                map.put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
            }
        }
    }

    /**
     * 拼接前清空参数的中的dataScope内容
     */
    private void clearDataScope(final JoinPoint joinPoint) {
        Object arg = joinPoint.getArgs()[0];
        if (arg instanceof com.smart.manager.entity.BaseEntity) {
            com.smart.manager.entity.BaseEntity baseEntity = (com.smart.manager.entity.BaseEntity) arg;
            baseEntity.getParams().put(DATA_SCOPE, "");
        } else if (arg instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) arg;
            map.put(DATA_SCOPE, "");
        }
    }
}
