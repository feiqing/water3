package com.alibaba.water3.core;

import com.alibaba.water3.Water3Context;
import com.alibaba.water3.Water3ExtensionPointInvoker;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.proxy.ProxyFactory;
import com.alibaba.water3.reducer.Reducer;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.alibaba.water3.core.Water3Register.getSpiImpls;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 17:47.
 */
public class Water3Executor {

    public static <SPI, T, R> R execute(Class<SPI> extensionAbility, Water3ExtensionPointInvoker<SPI, T> invoker, Reducer<T, R> reducer) {
        if (!extensionAbility.isInterface()) {
            throw new WaterException(String.format("ExtensionAbility:[%s] is not interface.", extensionAbility));
        }

        try {
            // todo 这个reducer传递有没有其他的办法?
            Water3Context.setReducer(reducer);
            // todo: 这里有个强转, 待测试
            return (R) invoker.invoke(ProxyFactory.getProxy(extensionAbility));
        } finally {
            Water3Context.removeReducer();
        }
    }

    /**
     * Proxy回调
     *
     * @param extensionAbility
     * @param method
     * @param args
     * @param <SPI>
     * @return
     * @throws Throwable
     */
    public static <SPI> Object doExecute(Class<SPI> extensionAbility, Method method, Object[] args) throws Throwable {
        Reducer reducer = Water3Context.getReducer();
        List<SPI> spiImplList = getSpiImpls(extensionAbility, method.getName());

        List<Object> rs = new ArrayList<>(spiImplList.size());
        for (SPI spiImpl : spiImplList) {
//            Object r = invoke(extensionAbility, spiImpl, method, args);
            Object r = method.invoke(spiImpl, args);
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

//    private static <SPI> Object invoke(Class<SPI> extensionAbility, Object target, Method method, Object[] args) {
//        ServiceLoader<Water3Plugin> loader = ServiceLoader.load(Water3Plugin.class, Water3Plugin.class.getClassLoader());
//        List<Water3Plugin> plugins = new ArrayList<>();
//        for (Iterator<Water3Plugin> iterator = loader.iterator(); iterator.hasNext(); ) {
//            plugins.add(iterator.next());
//        }
//
//        Preconditions.checkState(!plugins.isEmpty());
//
//        PluginInvocation invocation = new PluginInvocation() {
//
//            @Override
//            public Object[] getArgs() {
//                return args;
//            }
//
//            @Override
//            public Method getMethod() {
//                return method;
//            }
//
//            @Override
//            public Object getTarget() {
//                return target;
//            }
//
//            @Override
//            public Object processed() {
//                return method.invoke(target, args);
//            }
//        };
//
//        Water3Plugin next = plugins.get(plugins.size() - 1);
//
//        for (int i = plugins.size() - 2; i >= 0; i--) {
//            Water3Plugin plugin = plugins.get(i);
//
//        }
//    }
}
