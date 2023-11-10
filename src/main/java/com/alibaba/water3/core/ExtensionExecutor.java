package com.alibaba.water3.core;

import com.alibaba.water3.ExtensionInvoker;
import com.alibaba.water3.domain.SpiImpls;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.plugin.ExtensionInvocation;
import com.alibaba.water3.proxy.ProxyFactory;
import com.alibaba.water3.reducer.Reducer;
import com.alibaba.water3.reducer.Reducers;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.alibaba.water3.core.ExtensionManager.getPlugins;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 17:47.
 */
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExtensionExecutor {

    private static final ThreadLocal<Reducer<?, ?>> reducerCtx = new ThreadLocal<>();
    private static final ThreadLocal<Object> resultCtx = new ThreadLocal<>();

    public static <SPI, T, R> R execute(Class<SPI> extensionSpi, ExtensionInvoker<SPI, T> invoker, Reducer<T, R> reducer) {
        try {
            reducerCtx.set(reducer);
            invoker.invoke(ProxyFactory.getProxy(extensionSpi));
            return (R) resultCtx.get();
        } finally {
            reducerCtx.remove();
            resultCtx.remove();
        }
    }

    /**
     * Proxy的回调
     *
     * @param extensionSpi
     * @param method
     * @param args
     * @param <SPI>
     * @return
     * @throws Throwable
     */
    public static <SPI> Object _execute(Class<SPI> extensionSpi, Method method, Object[] args) throws Throwable {
        if (!extensionSpi.isInterface()) {
            throw new WaterException(String.format("ExtensionSpi:[%s] is not interface.", extensionSpi));
        }

        Reducer reducer = Optional.ofNullable(reducerCtx.get()).orElse((Reducer) Reducers.firstOf());

        SpiImpls impls = ExtensionManager.getSpiImpls(extensionSpi, args);
        List<Object> rs = new ArrayList<>(impls.size());

        for (SpiImpls.SpiImpl impl : impls) {
            Object r = invoke(extensionSpi, impl, method, args);
            rs.add(r);
            if (reducer.willBreak(r)) {
                break;
            }
        }

        // 防止类转换异常, 强制返回null, result通过线程上下文返回
        resultCtx.set(reducer.reduce(rs));
        return null;
    }

    public static <SPI, T, R> R extExecute(Class<SPI> extensionSpi, Function<SpiImpls.SpiImpl, List<Method>> methods, Reducer<T, R> reducer, Object[] args) {
        if (!extensionSpi.isInterface()) {
            throw new WaterException(String.format("ExtensionSpi:[%s] is not interface.", extensionSpi));
        }

        try {
            SpiImpls impls = ExtensionManager.getSpiImpls(extensionSpi, args);

            List<Object> rs = new ArrayList<>(impls.size());
            for (SpiImpls.SpiImpl impl : impls) {
                for (Method method : methods.apply(impl)) {
                    Object r = invoke(extensionSpi, impl, method, args);
                    rs.add(r);
                    if (reducer.willBreak((T) r)) {
                        break;
                    }
                }
            }

            return reducer.reduce((Collection<T>) rs);
        } catch (Throwable t) {
            throw Throwables.propagate(t);
        }
    }

    private static <SPI> Object invoke(Class<SPI> extensionSpi, SpiImpls.SpiImpl impl, Method method, Object[] args) throws Exception {
        method.setAccessible(true);
        return new ExtensionInvocation(extensionSpi, method, impl.type, impl.instance, args, getPlugins()).proceed();
    }
}
