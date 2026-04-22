package com.smart.manager.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.manager.common.annotation.Log;
import com.smart.manager.entity.LoginUser;
import com.smart.manager.entity.SysOperLog;
import com.smart.manager.service.ISysOperLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 操作日志记录处理
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {

    private final ISysOperLogService operLogService;
    private final ObjectMapper objectMapper;

    // 配置织入点
    @Pointcut("@annotation(com.smart.manager.common.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        try {
            // 获得注解
            Log controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) {
                return;
            }

            SysOperLog operLog = new SysOperLog();
            operLog.setStatus(0); // 正常
            // 请求的地址
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operLog.setOperIp(request.getRemoteAddr());
                operLog.setOperUrl(request.getRequestURI());
                operLog.setRequestMethod(request.getMethod());
            }

            if (e != null) {
                operLog.setStatus(1); // 异常
                operLog.setErrorMsg(e.getMessage());
            }

            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");

            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);

            // 设置操作人员
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) authentication.getPrincipal();
                operLog.setOperName(loginUser.getUsername());
            }

            operLog.setOperTime(new Date());

            // 异步保存数据库
            operLogService.saveAsync(operLog);
        } catch (Exception exp) {
            log.error("==日志记录异常==");
            log.error("异常信息:{}", exp.getMessage());
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysOperLog operLog, Object jsonResult)
            throws Exception {
        // 设置业务类型
        operLog.setBusinessType(log.businessType().ordinal());
        // 设置标题
        operLog.setTitle(log.title());
        // 设置操作人类别
        operLog.setOperatorType(log.operatorType());

        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            setRequestValue(joinPoint, operLog);
        }

        // 是否需要保存response
        if (log.isSaveResponseData() && jsonResult != null) {
            operLog.setJsonResult(objectMapper.writeValueAsString(jsonResult));
        }
    }

    /**
     * 获取请求的参数
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                StringBuilder params = new StringBuilder();
                for (Object arg : args) {
                    if (arg != null && !isFilterObject(arg)) {
                        try {
                            params.append(objectMapper.writeValueAsString(arg)).append(" ");
                        } catch (Exception e) {
                            params.append(String.valueOf(arg)).append(" ");
                        }
                    }
                }
                operLog.setOperParam(params.toString().trim());
            }
        } catch (Exception e) {
            log.error("处理日志参数错误", e);
        }
    }

    private boolean isFilterObject(Object o) {
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse;
    }

    private Log getAnnotationLog(JoinPoint joinPoint) {
        org.aspectj.lang.reflect.MethodSignature signature = (org.aspectj.lang.reflect.MethodSignature) joinPoint
                .getSignature();
        java.lang.reflect.Method method = signature.getMethod();
        if (method != null) {
            return method.getAnnotation(Log.class);
        }
        return null;
    }
}
