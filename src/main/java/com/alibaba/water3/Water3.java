package com.alibaba.water3;

import com.alibaba.water3.core.Water3Executor;
import com.alibaba.water3.reducer.Reducer;
import com.alibaba.water3.reducer.Reducers;
import com.alibaba.water3.starter.StaticWater3Starter;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author qingfei
 * @date 2022/06/02
 */
public final class Water3 {

    private Water3() {
    }

    public static <SPI, R> R execute(Class<SPI> extensionAbility, Water3ExtensionPointInvoker<SPI, R> invoker) {
        return execute(extensionAbility, invoker, Reducers.firstOf());
    }

    public static <SPI, T, R> R execute(Class<SPI> extensionAbility, Water3ExtensionPointInvoker<SPI, T> invoker, Reducer<T, R> reducer) {
        return Water3Executor.execute(extensionAbility, invoker, reducer);
    }

    public static void main(String[] args) throws Exception {
        StaticWater3Starter.start("classpath:spring-*.xml");
        Water3Context.setGroup("CREATE");
        Water3Context.setBizCode("xxx");
        Object r = execute(Function.class, function -> function.apply("haha"));
        Water3Context.setBizCode("yhb");
        System.out.println(r);
        r = execute(Supplier.class, function -> function.get());
        System.out.println(r);
    }
}
