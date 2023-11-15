package com.alibaba.water3.proxy.spring;

import com.alibaba.water3.core.ExtensionExecutor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:38.
 */
public class SpringProxyFactory<SPI> implements MethodInterceptor {

    private final Class<SPI> spi;

    private SpringProxyFactory(Class<SPI> spi) {
        this.spi = spi;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return ExtensionExecutor._execute(spi, method, objects);
    }

    public static <SPI> Object newProxy(Class<SPI> spi) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(spi.getClassLoader());
        enhancer.setInterfaces(new Class[]{spi});
        enhancer.setCallback(new SpringProxyFactory<>(spi));
        return enhancer.create();
    }
}
