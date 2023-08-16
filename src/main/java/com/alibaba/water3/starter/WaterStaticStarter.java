package com.alibaba.water3.starter;

import com.alibaba.water3.core.WaterManger;
import com.alibaba.water3.factory.SpringBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 23:16.
 */
public class WaterStaticStarter {

    public static void start(String springConfigLocation) throws Exception {
        SpringBeanFactory.setApplicationContext(new ClassPathXmlApplicationContext(springConfigLocation));
        WaterManger.register("XML");
    }
}
