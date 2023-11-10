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

    public static <SPI> Object newProxy(Class<SPI> spi) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(spi.getClassLoader());
        enhancer.setInterfaces(new Class[]{spi});
        enhancer.setCallback(new CglibMethodInterceptor<>(spi));
        return enhancer.create();
    }

    private static class CglibMethodInterceptor<SPI> implements MethodInterceptor {

        private final Class<SPI> spi;

        public CglibMethodInterceptor(Class<SPI> spi) {
            this.spi = spi;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return ExtensionExecutor._execute(spi, method, objects);
        }
    }
}
