package com.alibaba.water3;

import com.alibaba.water3.domain.Tag;
import com.alibaba.water3.factory.SpringBeanFactory;
import com.alibaba.water3.starter.WaterStaticStarter;
import org.springframework.aop.support.AopUtils;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/11/10 11:31.
 */
public class Demo2 {

    public static void main(String[] args) throws Exception {
        WaterStaticStarter.start("classpath:spring-*.xml");

        final Object springBean = SpringBeanFactory.getSpringBean("implSpi");


        System.out.println(springBean.getClass());
        System.out.println(AopUtils.isAopProxy(springBean));
        System.out.println(AopUtils.getTargetClass(springBean));

    }
}
