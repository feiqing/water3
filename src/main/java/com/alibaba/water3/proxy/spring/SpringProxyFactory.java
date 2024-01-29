package com.alibaba.water3.proxy.spring;

import com.alibaba.water3.core.ExtensionExecutor;
import com.alibaba.water3.reducer.Reducer;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:38.
 */
@SuppressWarnings("unchecked")
public class SpringProxyFactory<SPI> implements MethodInterceptor {

    private final Class<SPI> spi;

    private final Reducer<?, ?> reducer;

    public SpringProxyFactory(Class<SPI> spi, Reducer<?, ?> reducer) {
        this.spi = spi;
        this.reducer = reducer;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if (ReflectionUtils.isObjectMethod(method)) {
            return method.invoke(this, objects);
        }
        return ExtensionExecutor._execute(spi, method, objects, reducer);
    }

    public static <SPI> SPI newProxy(Class<SPI> spi, Reducer<?, ?> reducer) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(spi.getClassLoader());
        enhancer.setInterfaces(new Class[]{spi});
        enhancer.setCallback(new SpringProxyFactory<>(spi, reducer));
        return (SPI) enhancer.create();
    }
}
