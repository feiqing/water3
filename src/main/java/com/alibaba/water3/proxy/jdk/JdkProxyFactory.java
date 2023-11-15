package com.alibaba.water3.proxy.jdk;

import com.alibaba.water3.core.ExtensionExecutor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:49.
 */
public class JdkProxyFactory<SPI> implements InvocationHandler {

    private final Class<SPI> spi;

    private JdkProxyFactory(Class<SPI> spi) {
        this.spi = spi;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return ExtensionExecutor._execute(spi, method, args);
    }

    public static <SPI> Object newProxy(Class<SPI> spi) {
        return Proxy.newProxyInstance(spi.getClassLoader(), new Class[]{spi}, new JdkProxyFactory<>(spi));
    }
}
