package com.kingzhy.common.log.post;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wzy
 * @version v1.0
 * @date 2022/5/24 17:02
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置拦截器
     *
     * @param registry
     * @author wangzhy
     * @date 2022/1/22 15:42
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogKeyInterceptor());
    }
}
