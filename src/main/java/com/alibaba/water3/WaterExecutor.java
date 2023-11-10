package com.alibaba.water3;


import com.alibaba.water3.reducer.Reducer;
import com.alibaba.water3.reducer.Reducers;

/**
 * @author qingfei
 * @date 2022/06/02
 */
public class WaterExecutor {

    public static volatile WaterExecutor executor;

    private WaterExecutor() {
    }

    public static WaterExecutor getInstance() {
        if (executor == null) {
            synchronized (WaterExecutor.class) {
                if (executor == null) {
                    executor = new WaterExecutor();
                }
            }
        }
        return executor;
    }

    public <T, I> T execute(Class<I> extensionClass, ExtensionInvoker<I, T> invoker) {
        return execute(extensionClass, invoker, Reducers.firstOf());
    }

    public <T, R, I> R execute(Class<I> extensionClass, ExtensionInvoker<I, T> invoker, Reducer<T, R> matcher) {
        return Water3.execute(extensionClass, invoker, matcher);
    }

    public <T, I> T execute(Class<I> extensionClass, ExtensionInvoker<I, T> invoker, String method) {
        return execute(extensionClass, invoker, method, Reducers.firstOf());
    }

    public <T, R, I> R execute(Class<I> extensionClass, ExtensionInvoker<I, T> invoker, String method, Reducer<T, R> reducer) {
        return Water3.execute(extensionClass, invoker, reducer);
    }
}
