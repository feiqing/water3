package com.alibaba.water3;

import com.alibaba.water3.annotation.BizScenario;
import com.alibaba.water3.core.WaterExecutor;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.reducer.Reducer;
import com.alibaba.water3.reducer.Reducers;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author qingfei
 * @date 2022/06/02
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class Water3 {

    private static final ConcurrentMap<Class<?>, String> parser2scenario = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Class<?>, WaterParser> parser2instance = new ConcurrentHashMap<>();

    private Water3() {
    }

    public static <SPI, R> R execute(Class<SPI> extensionAbility, WaterExtensionPointInvoker<SPI, R> invoker) {
        return execute(extensionAbility, invoker, Reducers.firstOf());
    }

    public static <SPI, T, R> R execute(Class<SPI> extensionAbility, WaterExtensionPointInvoker<SPI, T> invoker, Reducer<T, R> reducer) {
        try {
            return WaterExecutor.execute(extensionAbility, invoker, reducer);
        } finally {
            WaterContext.removeCtx();
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
    public static <T> String parseBizId(Class<? extends WaterParser<T>> parser, T param) {

        String bizScenario = parser2scenario.computeIfAbsent(parser, clazz -> {
            BizScenario annotation = clazz.getAnnotation(BizScenario.class);
            if (annotation == null) {
                throw new WaterException(String.format("parser:[%s] none @BizScenario Annotation.", parser.getName()));
            }
            return annotation.value();
        });
        Preconditions.checkArgument(!Strings.isNullOrEmpty(bizScenario));
        WaterContext.setBizScenario(bizScenario);

        WaterParser<T> instance = parser2instance.computeIfAbsent(parser, clazz -> {
            try {
                return (WaterParser) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new WaterException(e);
            }
        });

        String bizDomain = instance.parseBizDomain(param);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(bizDomain));
        WaterContext.setBizDomain(bizDomain);

        String bizId = instance.parseBizId(param);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(bizId));
        WaterContext.setBizId(bizId);

        return bizId;
    }
}
