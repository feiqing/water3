package com.alibaba.water3.starter;

import com.alibaba.water3.core.WaterRegister;
import com.alibaba.water3.factory.SpringBeanFactory;
import com.alibaba.water3.spring.SpiProxyBeanRegister;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 23:13.
 */
@Configuration
public class WaterSpringStater implements InitializingBean, ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanFactory.setApplicationContext(applicationContext);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WaterRegister.register("XML");
    }

    @Bean
    public SpiProxyBeanRegister spiProxyBeanRegister() {
        return new SpiProxyBeanRegister();
    }
}
