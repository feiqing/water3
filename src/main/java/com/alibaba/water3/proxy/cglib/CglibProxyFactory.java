package com.alibaba.water3.proxy.cglib;

import com.alibaba.water3.core.WaterExecutor;
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

    public static <SPI> Object newProxy(Class<SPI> extensionAbility) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(extensionAbility.getClassLoader());
        enhancer.setInterfaces(new Class[]{extensionAbility});
        enhancer.setCallback(new CglibMethodInterceptor<>(extensionAbility));
        return enhancer.create();
    }

    private static class CglibMethodInterceptor<SPI> implements MethodInterceptor {

        private final Class<SPI> extensionAbility;

        public CglibMethodInterceptor(Class<SPI> extensionAbility) {
            this.extensionAbility = extensionAbility;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return WaterExecutor.proxyExecute(extensionAbility, method, objects);
        }
    }
}
