package com.alibaba.water3.starter;

import com.alibaba.water3.core.Water3Register;
import com.alibaba.water3.factory.SpringBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 23:13.
 */
@Component
public class SpringWater3Stater implements InitializingBean, ApplicationContextAware {

    @Override
    public void afterPropertiesSet() throws Exception {
        Water3Register.register("XML");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanFactory.setApplicationContext(applicationContext);
    }
}
