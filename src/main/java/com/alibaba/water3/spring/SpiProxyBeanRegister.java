package com.alibaba.water3.spring;

import com.alibaba.water3.core.WaterRegister;
import com.alibaba.water3.domain.Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Configuration;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/30 15:43.
 */
@Slf4j
@Configuration
public class SpiProxyBeanRegister implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        for (Entity.BusinessScenario scenario : WaterRegister.getScenarioMap().values()) {
            for (Class<?> spi : scenario.abilityMap.keySet()) {
                RootBeanDefinition definition = new RootBeanDefinition();
                definition.setBeanClass(SpiProxyFactoryBean.class);
                definition.getPropertyValues().addPropertyValue("spi", spi);
                definition.setPrimary(true);
                beanDefinitionRegistry.registerBeanDefinition(spi.getName(), definition);
                log.info("Register spi:[{}] in spring context.", spi.getName());
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
