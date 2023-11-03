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
public class SpringProxyFactory {

    public static <SPI> Object newProxy(Class<SPI> extensionSpi) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(extensionSpi.getClassLoader());
        enhancer.setInterfaces(new Class[]{extensionSpi});
        enhancer.setCallback(new SpringMethodInterceptor<>(extensionSpi));
        return enhancer.create();
    }


    private static class SpringMethodInterceptor<SPI> implements MethodInterceptor {

        private final Class<SPI> extensionSpi;

        public SpringMethodInterceptor(Class<SPI> extensionSpi) {
            this.extensionSpi = extensionSpi;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return ExtensionExecutor._execute(extensionSpi, method, objects);
        }
    }
}
