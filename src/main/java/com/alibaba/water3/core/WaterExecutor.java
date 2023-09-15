package com.alibaba.water3.core;

import com.alibaba.water3.ExtensionPointInvoker;
import com.alibaba.water3.domain.Entity;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.plugin.PluginInvocation;
import com.alibaba.water3.plugin.WaterPlugin;
import com.alibaba.water3.proxy.ProxyFactory;
import com.alibaba.water3.reducer.Reducer;
import com.alibaba.water3.reducer.Reducers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 17:47.
 */
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class WaterExecutor {

    private static final ThreadLocal<Reducer<?, ?>> reducerCtx = new ThreadLocal<>();
    private static final ThreadLocal<Object> resultCtx = new ThreadLocal<>();

    private static final WaterPlugin[] plugins;

    static {
        try {
            ServiceLoader<WaterPlugin> loader = ServiceLoader.load(WaterPlugin.class, WaterPlugin.class.getClassLoader());
            List<WaterPlugin> _plugins = new LinkedList<>();
            for (WaterPlugin plugin : loader) {
                _plugins.add(plugin);
                log.info("loaded [WaterPlugin]: {}", plugin);
            }
            plugins = _plugins.toArray(new WaterPlugin[0]);
        } catch (Throwable t) {
            log.error("loading WaterPlugin error.", t);
            throw new RuntimeException(t);
        }
    }


    public static <SPI, T, R> R execute(Class<SPI> extensionAbility, ExtensionPointInvoker<SPI, T> invoker, Reducer<T, R> reducer) {
        try {
            reducerCtx.set(reducer);
            invoker.invoke(ProxyFactory.getProxy(extensionAbility));
            return (R) resultCtx.get();
        } finally {
            reducerCtx.remove();
            resultCtx.remove();
        }
    }

    /**
     * Proxy的回调
     *
     * @param extensionAbility
     * @param method
     * @param args
     * @param <SPI>
     * @return
     * @throws Throwable
     */
    public static <SPI> Object proxyExecute(Class<SPI> extensionAbility, Method method, Object[] args) throws Throwable {
        if (!extensionAbility.isInterface()) {
            throw new WaterException(String.format("ExtensionAbility:[%s] is not interface.", extensionAbility));
        }

        Reducer reducer = Optional.ofNullable(reducerCtx.get()).orElse(Reducers.defaultReducer);

        List<Entity.InstanceWrapper> instances = WaterRegister.getSpiInstances(extensionAbility, method.getName());

        List<Object> rs = new ArrayList<>(instances.size());
        for (Entity.InstanceWrapper instance : instances) {
            Object r = invoke(extensionAbility, instance, method, args);
            rs.add(r);
            if (reducer.willBreak(r)) {
                break;
            }
        }

        Object r = null;
        if (!CollectionUtils.isEmpty(rs)) {
            r = reducer.reduce(rs);
        }

        // 防止类转换异常, 强制返回null, result通过线程上下文返回
        resultCtx.set(r);
        return null;
    }

    private static <SPI> Object invoke(Class<SPI> extensionAbility, Entity.InstanceWrapper wrapper, Method method, Object[] args) throws Exception {
        method.setAccessible(true);
        return new PluginInvocation(extensionAbility, method, wrapper.impl, wrapper.instance, args, plugins).processed();
    }
}
