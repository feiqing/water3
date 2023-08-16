package com.alibaba.water3.core;

import com.alibaba.water3.WaterExtensionPointInvoker;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.plugin.PluginInvocation;
import com.alibaba.water3.plugin.WaterPlugin;
import com.alibaba.water3.proxy.ProxyFactory;
import com.alibaba.water3.reducer.Reducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import static com.alibaba.water3.core.WaterManger.getSpiImpls;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 17:47.
 */
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class WaterExecutor {

    private static final ThreadLocal<Reducer<?, ?>> reducerCtx = new ThreadLocal<>();

    private static final WaterPlugin[] plugins;

    static {
        ServiceLoader<WaterPlugin> loader = ServiceLoader.load(WaterPlugin.class, WaterPlugin.class.getClassLoader());
        List<WaterPlugin> _plugins = new LinkedList<>();
        for (WaterPlugin plugin : loader) {
            log.info("loaded [WaterPlugin]: {}", plugin);
            _plugins.add(plugin);
        }
        plugins = _plugins.toArray(new WaterPlugin[0]);
    }


    public static <SPI, T, R> R execute(Class<SPI> extensionAbility, WaterExtensionPointInvoker<SPI, T> invoker, Reducer<T, R> reducer) {
        if (!extensionAbility.isInterface()) {
            throw new WaterException(String.format("ExtensionAbility:[%s] is not interface.", extensionAbility));
        }

        try {
            reducerCtx.set(reducer);
            return (R) invoker.invoke(ProxyFactory.getProxy(extensionAbility));
        } finally {
            reducerCtx.remove();
        }
    }

    public static <SPI> Object doExecute(Class<SPI> extensionAbility, Method method, Object[] args) throws Throwable {
        Reducer reducer = reducerCtx.get();
        List<SPI> impls = getSpiImpls(extensionAbility, method.getName());

        List<Object> rs = new ArrayList<>(impls.size());
        for (SPI impl : impls) {
            Object r = invoke(extensionAbility, impl, method, args);
            rs.add(r);
            if (reducer.willBreak(r)) {
                break;
            }
        }

        Object r = null;
        if (!CollectionUtils.isEmpty(rs)) {
            r = reducer.reduce(rs);
        }

        return r;
    }

    private static <SPI> Object invoke(Class<SPI> extensionAbility, Object target, Method method, Object[] args) throws Exception {
        PluginInvocation invocation = new PluginInvocation(extensionAbility, method, target, args, plugins);
        return invocation.processed();
    }
}
