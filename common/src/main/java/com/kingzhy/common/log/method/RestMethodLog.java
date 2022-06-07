package com.kingzhy.common.log.method;

/**
 * 接口请求日志打印标签
 *
 * @author wzy
 * @version v1.0
 * @date 2022/5/24 10:17
 */
public @interface RestMethodLog {
    /**
     * 日志描述信息
     *
     * @author wzy
     * @date 2022/5/24 10:18
     * @return java.lang.String
     */
    String value() default "";
}
