package com.alibaba.water.function.register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.water.domain.WaterRouterInterface;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 1、一个Impl有多个tag（复用）
 * 2、同一个接口的impl中可能存在多个impl对应同一个tag（叠加），优先级问题
 *
 * @author qingfei
 * @date 2022/05/07
 */
public class WaterTagRegister {

    private static Map<String, Map<String, List<Class<?>>>> interfaceTagImplMap = Maps.newConcurrentMap();

    /**
     * 方法级别的业务身份路由，业务自定义路由
     * <scenario,<mathod,routeImplementClass>>
     */
    private static Map<String, WaterRouterInterface> methodRouteCustomImplMap = Maps.newConcurrentMap();

    public static void register(String interfaceName, String tag, Class<?> implClass) {
        Map<String, List<Class<?>>> tagImplMap = interfaceTagImplMap.getOrDefault(interfaceName, Maps.newConcurrentMap());
        List<Class<?>> implList = tagImplMap.getOrDefault(tag, Lists.newArrayList());
        implList.add(implClass);
        if (!tagImplMap.containsKey(tag)) {
            tagImplMap.put(tag, implList);
        }
        if (!interfaceTagImplMap.containsKey(interfaceName)) {
            interfaceTagImplMap.put(interfaceName, tagImplMap);
        }
    }

    public static void register(String tag, WaterRouterInterface router) {
        if(!methodRouteCustomImplMap.containsKey(tag)){
            methodRouteCustomImplMap.put(tag,router);
        }
    }

    public static List<Class<?>> getImplClassListByInterfaceAndTag(String interfaceName, String tag, String method) {
        if (methodRouteCustomImplMap.containsKey(tag)) {
            //自定义方法级别路由
            WaterRouterInterface waterRouterInterface = methodRouteCustomImplMap.get(tag);
            if (waterRouterInterface != null) {
                Class<?> implClass = waterRouterInterface.route(method);
                List<Class<?>> routeList = new ArrayList<>();
                routeList.add(implClass);
                return routeList;
            }
            return Collections.emptyList();
        } else {
            return getImplClassListByInterfaceAndTag(interfaceName, tag);
        }
    }


    public static List<Class<?>> getImplClassListByInterfaceAndTag(String interfaceName, String tag) {
        Map<String, List<Class<?>>> tagImplMap = interfaceTagImplMap.get(interfaceName);
        if (tagImplMap == null) {
            return null;
        }
        return tagImplMap.getOrDefault(tag, null);

    }

    public static boolean isCustomRouter(String tag) {
        return methodRouteCustomImplMap.containsKey(tag);
    }

    public static Map<String, Map<String, List<Class<?>>>> getInterfaceTagImplMap() {
        return interfaceTagImplMap;
    }

    public static void setInterfaceTagImplMap(
            Map<String, Map<String, List<Class<?>>>> interfaceTagImplMap) {
        WaterTagRegister.interfaceTagImplMap = interfaceTagImplMap;
    }
}
