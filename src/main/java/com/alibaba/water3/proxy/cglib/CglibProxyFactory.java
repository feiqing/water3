package com.alibaba.water3.proxy.cglib;

import com.alibaba.water3.core.ExtensionExecutor;
import com.alibaba.water3.reducer.Reducer;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:49.
 */
@SuppressWarnings("unchecked")
public class CglibProxyFactory<SPI> implements MethodInterceptor {

    private final Class<SPI> spi;

    private final Reducer<?, ?> reducer;

    public CglibProxyFactory(Class<SPI> spi, Reducer<?, ?> reducer) {
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
        enhancer.setCallback(new CglibProxyFactory<>(spi, reducer));
        return (SPI) enhancer.create();
    }
}
