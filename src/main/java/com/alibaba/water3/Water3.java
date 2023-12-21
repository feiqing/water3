package com.alibaba.water3;

import com.alibaba.water3.core.ExtensionExecutor;
import com.alibaba.water3.domain.SpiImpls;
import com.alibaba.water3.reducer.Reducer;
import com.alibaba.water3.reducer.Reducers;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static com.alibaba.water3.BizCodeParser.BASE_BIZ_ROUTER;
import static com.alibaba.water3.utils.SysNamespace.CALLER;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:31.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class Water3 {

    private static final ConcurrentMap<Class<?>, BizCodeParser> parser2instance = new ConcurrentHashMap<>();

    private Water3() {
    }

    public static <SPI, R> R execute(Class<SPI> spi, BizExtensionInvoker<SPI, R> invoker) {
        return execute(spi, invoker, Reducers.firstOf());
    }

    public static <SPI, T, R> R execute(Class<SPI> spi, BizExtensionInvoker<SPI, T> invoker, Reducer<T, R> reducer) {
        BizContext.setSpi(spi);
        BizContext.addBusinessExt(CALLER, "execute");
        try {
            return ExtensionExecutor.execute(spi, invoker, reducer);
        } finally {
            BizContext.removeSpi();
            BizContext.removeBusinessExt(CALLER);
        }
    }

    public static <SPI, R> R execute(String bizCode, Class<SPI> spi, BizExtensionInvoker<SPI, R> invoker) {
        return execute(bizCode, BASE_BIZ_ROUTER, spi, invoker, Reducers.firstOf());
    }

    public static <SPI, T, R> R execute(String bizCode, Class<SPI> spi, BizExtensionInvoker<SPI, T> invoker, Reducer<T, R> reducer) {
        return execute(bizCode, BASE_BIZ_ROUTER, spi, invoker, reducer);
    }

    public static <SPI, T, R> R execute(String bizCode, BizRouter bizRouter, Class<SPI> spi, BizExtensionInvoker<SPI, T> invoker, Reducer<T, R> reducer) {
        BizContext.setBizCode(bizCode);
        BizContext.setBizRouter(bizRouter);
        BizContext.setSpi(spi);
        BizContext.addBusinessExt(CALLER, "execute");
        try {
            return ExtensionExecutor.execute(spi, invoker, reducer);
        } finally {
            BizContext.removeSpi();
            BizContext.removeBusinessExt(CALLER);
            BizContext.removeBizRouter();
            BizContext.removeBizCode();
        }
    }

    public static <SPI, T, R> R extExecute(Class<SPI> spi, Function<SpiImpls.SpiImpl, List<Method>> methods, Object... args) {
        return extExecute(spi, methods, Reducers.firstOf(), args);
    }

    public static <SPI, T, R> R extExecute(Class<SPI> spi, Function<SpiImpls.SpiImpl, List<Method>> methods, Reducer<T, R> reducer, Object... args) {
        BizContext.setSpi(spi);
        BizContext.addBusinessExt(CALLER, "extExecute");
        try {
            return ExtensionExecutor.extExecute(spi, methods, reducer, args);
        } finally {
            BizContext.removeSpi();
            BizContext.removeBusinessExt(CALLER);
        }
    }


    /**
     * 解析业务身份
     *
     * @param parser
     * @param param
     * @param <T>
     * @return
     */
    public static <T> String parseBizCode(Class<? extends BizCodeParser<T>> parser, T param) {

        BizCodeParser<T> instance = parser2instance.computeIfAbsent(parser, clazz -> {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return (BizCodeParser) constructor.newInstance();
            } catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
                throw Throwables.propagate(e);
            }
        });

        BizRouter bizRouter = instance.getBizRouter(param);
        Preconditions.checkArgument(bizRouter != null);
        BizContext.setBizRouter(bizRouter);

        String bizCode = instance.parseBizCode(param);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(bizCode));
        BizContext.setBizCode(bizCode);

        return bizCode;
    }
}
