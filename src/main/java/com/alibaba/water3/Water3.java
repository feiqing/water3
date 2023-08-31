package com.alibaba.water3;

import com.alibaba.water3.annotation.BizScenario;
import com.alibaba.water3.core.WaterExecutor;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.reducer.Reducer;
import com.alibaba.water3.reducer.Reducers;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author qingfei
 * @date 2022/06/02
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class Water3 {

    private static final ConcurrentMap<Class<?>, String> parser2scenario = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Class<?>, BizCodeParser> parser2instance = new ConcurrentHashMap<>();

    private Water3() {
    }

    public static <SPI, R> R execute(Class<SPI> extensionAbility, ExtensionPointInvoker<SPI, R> invoker) {
        return (R) execute(extensionAbility, invoker, Reducers.defaultReducer);
    }

    public static <SPI, T, R> R execute(Class<SPI> extensionAbility, ExtensionPointInvoker<SPI, T> invoker, Reducer<T, R> reducer) {
        return WaterExecutor.execute(extensionAbility, invoker, reducer);
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

        String bizScenario = parser2scenario.computeIfAbsent(parser, clazz -> {
            BizScenario annotation = clazz.getAnnotation(BizScenario.class);
            if (annotation == null) {
                throw new WaterException(String.format("parser:[%s] none @BizScenario Annotation.", parser.getName()));
            }
            return annotation.value();
        });
        Preconditions.checkArgument(!Strings.isNullOrEmpty(bizScenario));
        BizContext.setBizScenario(bizScenario);

        BizCodeParser<T> instance = parser2instance.computeIfAbsent(parser, clazz -> {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return (BizCodeParser) constructor.newInstance();
            } catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
                throw Throwables.propagate(e);
            }
        });

        String bizDomain = instance.parseBizDomain(param);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(bizDomain));
        BizContext.setBizDomain(bizDomain);

        String bizCode = instance.parseBizCode(param);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(bizCode));
        BizContext.setBizCode(bizCode);

        return bizCode;
    }
}
