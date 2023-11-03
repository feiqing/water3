package com.alibaba.water3.proxy.cglib;

import com.alibaba.water3.core.ExtensionExecutor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:49.
 */
public class CglibProxyFactory {

    public static <SPI> Object newProxy(Class<SPI> extensionSpi) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(extensionSpi.getClassLoader());
        enhancer.setInterfaces(new Class[]{extensionSpi});
        enhancer.setCallback(new CglibMethodInterceptor<>(extensionSpi));
        return enhancer.create();
    }

    private static class CglibMethodInterceptor<SPI> implements MethodInterceptor {

        private final Class<SPI> extensionSpi;

        public CglibMethodInterceptor(Class<SPI> extensionSpi) {
            this.extensionSpi = extensionSpi;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return ExtensionExecutor._execute(extensionSpi, method, objects);
        }
    }
}
