package com.alibaba.water3.proxy.spring;

import com.alibaba.water3.core.Water3Executor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:38.
 */
public class SpringProxyFactory {

    public static <SPI> Object newProxy(Class<SPI> extensionAbility) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(extensionAbility.getClassLoader());
        enhancer.setInterfaces(new Class[]{extensionAbility});
        enhancer.setCallback(new SpringMethodInterceptor<>(extensionAbility));
        return enhancer.create();
    }


    private static class SpringMethodInterceptor<SPI> implements MethodInterceptor {

        private final Class<SPI> extensionAbility;

        public SpringMethodInterceptor(Class<SPI> extensionAbility) {
            this.extensionAbility = extensionAbility;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return Water3Executor.doExecute(extensionAbility, method, objects);
        }
    }
}
