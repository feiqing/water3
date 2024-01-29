package com.alibaba.water3.core;

import com.alibaba.water3.BizContext;
import com.alibaba.water3.BizExtensionInvoker;
import com.alibaba.water3.domain.SpiImpls;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.plugin.ExtensionInvocation;
import com.alibaba.water3.proxy.ProxyFactory;
import com.alibaba.water3.reducer.Reducer;
import com.alibaba.water3.utils.SysNamespace;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static com.alibaba.water3.core.ExtensionManager.getPlugins;
import static com.alibaba.water3.utils.SysNamespace.CALLER;
import static com.alibaba.water3.utils.SysNamespace.METHOD;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 17:47.
 */
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExtensionExecutor {

    private static final ConcurrentMap<Class<?>, Object> spiProxies = new ConcurrentHashMap<>();

    private static final ThreadLocal<MutablePair<Reducer<?, ?>, Object>> ctx = ThreadLocal.withInitial(MutablePair::new);

    public static <SPI, T, R> R execute(Class<SPI> spi, BizExtensionInvoker<SPI, T> invoker, Reducer<T, R> reducer) {
        try {
            ctx.get().setLeft(reducer);
            Object invoke = invoker.invoke((SPI) spiProxies.computeIfAbsent(spi, ProxyFactory::newProxy));
            if (reducer.isSameType()) {
                return (R) invoke;
            } else {
                return (R) ctx.get().getRight();
            }
        } finally {
            ctx.remove();
        }
    }

    public static <SPI> Object _execute(Class<SPI> spi, Method method, Object[] args, Reducer reducer) throws Throwable {
        if (!spi.isInterface()) {
            throw new WaterException(String.format("ExtensionSpi:[%s] is not interface.", spi));
        }

        try {
            BizContext.addBusinessExt(SysNamespace.SPI, spi);
            BizContext.addBusinessExt(METHOD, method.getName());
            BizContext.addBusinessExt(CALLER, "execute");
            reducer = reducer != null ? reducer : Objects.requireNonNull(ctx.get().getLeft());

            SpiImpls impls = ExtensionManager.getSpiImpls(spi, args);
            List<Object> rs = new ArrayList<>(impls.size());

            for (SpiImpls.SpiImpl impl : impls) {
                Object r = invoke(spi, impl, method, args);
                rs.add(r);
                if (reducer.willBreak(r)) {
                    break;
                }
            }

            Object reduce = reducer.reduce(rs);
            if (reducer.isSameType()) {
                return reduce;
            } else {
                ctx.get().setRight(reduce);
                return null;
            }
        } finally {
            BizContext.removeBusinessExt(CALLER);
            BizContext.removeBusinessExt(METHOD);
            BizContext.removeBusinessExt(SysNamespace.SPI);
        }
    }

    public static <SPI, T, R> R extExecute(Class<SPI> spi, Function<SpiImpls.SpiImpl, List<Method>> methods, Reducer<T, R> reducer, Object[] args) {
        if (!spi.isInterface()) {
            throw new WaterException(String.format("ExtensionSpi:[%s] is not interface.", spi));
        }

        BizContext.addBusinessExt(SysNamespace.SPI, spi);
        BizContext.addBusinessExt(CALLER, "extExecute");

        try {
            SpiImpls impls = ExtensionManager.getSpiImpls(spi, args);

            List<Object> rs = new ArrayList<>(impls.size());
            for (SpiImpls.SpiImpl impl : impls) {
                for (Method method : methods.apply(impl)) {
                    Object r = invoke(spi, impl, method, args);
                    rs.add(r);
                    if (reducer.willBreak((T) r)) {
                        break;
                    }
                }
            }

            return reducer.reduce((Collection<T>) rs);
        } catch (Throwable t) {
            throw Throwables.propagate(t);
        } finally {
            BizContext.removeBusinessExt(CALLER);
            BizContext.removeBusinessExt(SysNamespace.SPI);
        }
    }

    private static <SPI> Object invoke(Class<SPI> spi, SpiImpls.SpiImpl impl, Method method, Object[] args) throws Exception {
        method.setAccessible(true);
        return new ExtensionInvocation(spi, method, impl.type, impl.instance, args, getPlugins()).proceed();
    }
}
