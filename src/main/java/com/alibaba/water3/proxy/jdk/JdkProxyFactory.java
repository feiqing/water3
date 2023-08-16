package com.alibaba.water3.proxy.jdk;

import com.alibaba.water3.core.WaterExecutor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:49.
 */
public class JdkProxyFactory {

    public static <SPI> Object newProxy(Class<SPI> extensionAbility) {
        return Proxy.newProxyInstance(extensionAbility.getClassLoader(), new Class[]{extensionAbility}, new JdkInvocationHandler<>(extensionAbility));

    }

    private static class JdkInvocationHandler<SPI> implements InvocationHandler {

        private final Class<SPI> extensionAbility;

        public JdkInvocationHandler(Class<SPI> extensionAbility) {
            this.extensionAbility = extensionAbility;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return WaterExecutor.doExecute(extensionAbility, method, args);
        }
    }
}
