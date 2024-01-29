package com.alibaba.water3.proxy.jdk;

import com.alibaba.water3.core.ExtensionExecutor;
import com.alibaba.water3.reducer.Reducer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:49.
 */
@SuppressWarnings("unchecked")
public class JdkProxyFactory<SPI> implements InvocationHandler {

    private final Class<SPI> spi;

    private final Reducer<?, ?> reducer;

    public JdkProxyFactory(Class<SPI> spi, Reducer<?, ?> reducer) {
        this.spi = spi;
        this.reducer = reducer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (ReflectionUtils.isObjectMethod(method)) {
            return method.invoke(this, args);
        }
        return ExtensionExecutor._execute(spi, method, args, reducer);
    }

    public static <SPI> SPI newProxy(Class<SPI> spi, Reducer<?, ?> reducer) {
        return (SPI) Proxy.newProxyInstance(spi.getClassLoader(), new Class[]{spi}, new JdkProxyFactory<>(spi, reducer));
    }
}
