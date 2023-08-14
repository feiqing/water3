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

    private static final ConcurrentMap<String, Object> beanCache = new ConcurrentHashMap<>();

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringBeanFactory.applicationContext = applicationContext;
    }

    public static Object getSpringBean(Tag.Bean bean) {
        return beanCache.computeIfAbsent(bean.name, _k -> applicationContext.getBean(bean.name));
    }

    public static <T> T getSpringBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

}
