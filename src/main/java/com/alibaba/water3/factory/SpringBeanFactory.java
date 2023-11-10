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

    private static final ConcurrentMap<String, Object> name2bean = new ConcurrentHashMap<>();

    public static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringBeanFactory.applicationContext = applicationContext;
    }

    public static Object getSpringBean(Tag.Bean bean) {
        return getSpringBean(bean.name);
    }

    public static Object getSpringBean(String beanName) {
        return name2bean.computeIfAbsent(beanName, _beanName -> applicationContext.getBean(beanName));
    }
}
