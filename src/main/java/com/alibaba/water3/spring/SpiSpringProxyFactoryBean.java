package com.alibaba.water3.spring;

import com.alibaba.water3.proxy.ProxyFactory;
import com.alibaba.water3.reducer.Reducer;
import com.google.common.base.Preconditions;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2024/1/26 22:34.
 */
@Slf4j
public class SpiSpringProxyFactoryBean<SPI> implements FactoryBean<SPI> {

    @Setter
    private Class<SPI> spi;

    @Setter
    private Reducer<?, ?> reducer;

    @Override
    public SPI getObject() {
        Preconditions.checkState(spi != null && reducer != null && reducer.isSameType());
        SPI proxy = ProxyFactory.newProxy(spi, reducer);
        log.info("Register spi:[{}] reducer:[{}] spring-proxy-bean:[{}] into spring context.", spi, reducer, proxy);
        return proxy;
    }

    @Override
    public Class<SPI> getObjectType() {
        return spi;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
