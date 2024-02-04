package com.alibaba.water.annotation;

import com.alibaba.water.domain.WaterRouterInterface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 靖杨
 * @describe
 * @since 2023/2/9
 * 自定义方法基本路由
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WaterRouter{
    String scenario() default "";
    
    Class<? extends WaterRouterInterface> routeClass();
}
