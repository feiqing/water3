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

/**
 * @author qingfei
 * @date 2022/06/02
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class Water3 {

    private static final ConcurrentMap<Class<?>, BizCodeParser> parser2instance = new ConcurrentHashMap<>();

    private Water3() {
    }

    public static <SPI, R> R execute(Class<SPI> extensionSpi, ExtensionInvoker<SPI, R> invoker) {
        return execute(extensionSpi, invoker, Reducers.firstOf());
    }

    public static <SPI, T, R> R execute(Class<SPI> extensionSpi, ExtensionInvoker<SPI, T> invoker, Reducer<T, R> reducer) {
        BizContext.addBusinessExt("__spi__", extensionSpi);
        try {
            return ExtensionExecutor.execute(extensionSpi, invoker, reducer);
        } finally {
            BizContext.removeBusinessExt("__spi__");
        }
    }

    public static <SPI, T, R> R extExecute(Class<SPI> extensionSpi, Function<SpiImpls.SpiImpl, List<Method>> methods, Object... args) {
        return extExecute(extensionSpi, methods, Reducers.firstOf(), args);
    }

    public static <SPI, T, R> R extExecute(Class<SPI> extensionSpi, Function<SpiImpls.SpiImpl, List<Method>> methods, Reducer<T, R> reducer, Object... args) {
        BizContext.addBusinessExt("__spi__", extensionSpi);
        try {
            return ExtensionExecutor.extExecute(extensionSpi, methods, reducer, args);
        } finally {
            BizContext.removeBusinessExt("__spi__");
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

        String type = instance.type(param);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(type));
        BizContext.setType(type);

        String bizCode = instance.parseBizCode(param);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(bizCode));
        BizContext.setBizCode(bizCode);

        return bizCode;
    }
}
