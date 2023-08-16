package com.alibaba.water3;

import com.alibaba.water3.annotation.BizScenario;
import com.alibaba.water3.core.WaterExecutor;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.reducer.Reducer;
import com.alibaba.water3.reducer.Reducers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author qingfei
 * @date 2022/06/02
 */
@SuppressWarnings({"all"})
public final class Water3 {

    private static final ConcurrentMap<Class<?>, String> parser2scenario = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Class<?>, WaterParser> parser2instance = new ConcurrentHashMap<>();

    private Water3() {
    }

    public static <SPI, R> R execute(Class<SPI> extensionAbility, WaterExtensionPointInvoker<SPI, R> invoker) {
        return execute(extensionAbility, invoker, Reducers.firstOf());
    }

    public static <SPI, T, R> R execute(Class<SPI> extensionAbility, WaterExtensionPointInvoker<SPI, T> invoker, Reducer<T, R> reducer) {
        return WaterExecutor.execute(extensionAbility, invoker, reducer);
    }

    /**
     * 解析业务身份
     *
     * @param parser
     * @param t
     * @param <T>
     * @return
     */
    public static <T> String parseBizId(Class<? extends WaterParser<T>> parser, T t) {

        String scenario = parser2scenario.computeIfAbsent(parser, clazz -> {
            BizScenario annotation = clazz.getAnnotation(BizScenario.class);
            if (annotation == null) {
                throw new WaterException(String.format("parser:[%s] none @BizScenario Annotation.", parser.getName()));
            }
            return annotation.value();
        });
        WaterContext.setBizScenario(scenario);

        WaterParser<T> instance = parser2instance.computeIfAbsent(parser, clazz -> {
            try {
                return (WaterParser) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new WaterException(e);
            }
        });

        String bizId = instance.parseBizId(t);
        WaterContext.setBizId(bizId);

        return bizId;
    }
}
