package com.kingzhy.common.log.post;

import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 同一请求加LogKey
 *
 * @author wzy
 * @version v1.0
 * @date 2022/5/24 11:30
 */
public class LogKeyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String logKey = request.getHeader("log-key");
        if (!isValidLogKey(logKey)) {
            logKey = generateLogKey();
        }
        MDC.put("log-key", logKey);
        response.setHeader("log-key", logKey);
        return true;
    }

    private String generateLogKey() {
        return UUID.randomUUID().toString();
    }

    private boolean isValidLogKey(String logKey) {
        return logKey != null && !logKey.isEmpty();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        MDC.clear();
    }
}
