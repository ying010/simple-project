package com.kingzhy.common.log.method;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * AOP方式实现接口日志打印
 *
 * @author wzy
 * @version v1.0
 * @date 2022/5/24 10:19
 */
@Slf4j
@Aspect
@Component
public class AopRestMethodLog {
    private static final ThreadLocal<Long> EXECUTION_TIME = new ThreadLocal<>();

    /**
     * 定义切点：@RestMethodLog标签标记的方法
     *
     * @author wzy
     * @date 2022/5/24 10:24
     */
    @Pointcut("@annotation(com.kingzhy.common.log.method.RestMethodLog)")
    public void webLogPointCut(){

    }

    /**
     * 环绕通知，打印请求参数和返回参数
     * @author wzy
     * @date 2022/5/24 10:28
     * @param joinPoint 连接点
     * @return java.lang.Object
     */
    @Around("webLogPointCut()")
    public Object webLogPointCutAround(ProceedingJoinPoint joinPoint) throws Throwable{
        try {
            EXECUTION_TIME.set(System.currentTimeMillis());
            // 开始打印请求日志
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            // 获取 @RestMethodLog 注解的描述信息
            String methodDescription = this.getAspectLogDescription(joinPoint);
            //url
            String uri = request.getRequestURI();
            //ip
            String ip = request.getRemoteAddr();
            //method
            String method = request.getMethod();

            //获取浏览器信息
            String ua = request.getHeader("User-Agent");

            Object[] args = joinPoint.getArgs();

            // 打印请求相关参数
            log.info("========================================== Start ==========================================");
            // 打印请求 url
            log.info("uri            : {}", uri);
            // 打印描述信息
            log.info("Description    : {}", methodDescription);
            // 打印 Http method
            log.info("HTTP Method    : {}", method);
            // 打印调用 controller 的全路径以及执行方法
            log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            // 打印请求的 IP
            log.info("IP             : {}", ip);
            // 打印请求的浏览器信息
            log.info("UA             : {}", ua);
            // 打印token
            log.info("TOKEN          : {}", StringUtils.isNotEmpty(request.getHeader("token")) ? request.getHeader("token").replaceAll("[\r\n]", "") : "");
            // 打印请求入参
            for (Object arg : args) {
                try {
                    log.info("Request Args   : {}", JSONObject.toJSONString(arg));
                }catch (Exception e){
                    log.error("Request Args error", e);
                }
            }
        } catch (Exception e) {
            log.info("打印请求日志错误", e);
        }
        Object result = joinPoint.proceed();
        // 打印出参
        log.info("Response Args  : {}", JSON.toJSONString(result));
        return result;
    }

    /**
     * 定义后置通知，打印执行时间清理ThreadLocal;为防止接口执行报错，后置通知不可和环绕通知合并
     *
     * @author wzy
     * @date 2022/5/24 10:53
     */
    @After("webLogPointCut()")
    public void doAfter() {
        // 执行耗时
        log.info("Time-Consuming : {} ms", System.currentTimeMillis() - EXECUTION_TIME.get());
        EXECUTION_TIME.remove();
        // 接口结束后换行，方便分割查看
        log.info("=========================================== End ===========================================");
    }

    /**
     * 获取切入点方法上@RestMethodLog标签的描述信息
     *
     * @author wzy
     * @date 2022/5/24 10:47
     * @param joinPoint 切入点
     * @return java.lang.String
     */
    private String getAspectLogDescription(JoinPoint joinPoint) throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        StringBuilder description = new StringBuilder();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description.append(method.getAnnotation(RestMethodLog.class).value());
                    break;
                }
            }
        }
        return description.toString();
    }


}

