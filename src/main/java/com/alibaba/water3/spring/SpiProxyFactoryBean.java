package com.alibaba.water3.spring;

import com.alibaba.water3.proxy.ProxyFactory;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

/**
 * <a href="xiaosheng.lxs@alibaba-inc.com">紫思</a>
 *
 * @date 2018/08/16
 * @time 下午5:29
 * @description
 */
public class SpiProxyFactoryBean implements FactoryBean<Object> {

    @Setter
    private Class<?> spi;

    @Override
    public Object getObject() {
        return ProxyFactory.getProxy(spi);
    }

    @Override
    public Class<?> getObjectType() {
        return spi;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
