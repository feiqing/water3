package com.alibaba.water3.factory;

import com.alibaba.water3.domain.Tag;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author qingfei
 * @date 2022/05/19
 */
public class SpringBeanFactory {

    private static final ConcurrentMap<Object, Object> beanCache = new ConcurrentHashMap<>();

    public static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringBeanFactory.applicationContext = applicationContext;
    }

    public static Object getSpringBean(Tag.Bean bean) {
        return getSpringBean(bean.name);
    }

    public static Object getSpringBean(String beanName) {
        return beanCache.computeIfAbsent(beanName, _beanName -> applicationContext.getBean(beanName));
    }

    public static Object getSpringBean(Class<?> beanType) {
        return beanCache.computeIfAbsent(beanType, _beanType -> applicationContext.getBean(beanType));
    }
}
