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
public class JdkProxyFactory {

    public static <SPI> Object newProxy(Class<SPI> extensionSpi) {
        return Proxy.newProxyInstance(extensionSpi.getClassLoader(), new Class[]{extensionSpi}, new JdkInvocationHandler<>(extensionSpi));

    }

    private static class JdkInvocationHandler<SPI> implements InvocationHandler {

        private final Class<SPI> extensionSpi;

        public JdkInvocationHandler(Class<SPI> extensionSpi) {
            this.extensionSpi = extensionSpi;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return ExtensionExecutor._execute(extensionSpi, method, args);
        }
    }
}
