package com.alibaba.water.function.register;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author qingfei
 * @date 2022/10/23
 */
public class WaterBaseRegister {

    private static Map<String, Class<?>> waterBaseMap = Maps.newConcurrentMap();

    public static void register(String interfaceName, Class<?> baseClass) {
        if (waterBaseMap.containsKey(interfaceName)) {
            // todo 打base 多个重复日志error
        }
        waterBaseMap.put(interfaceName, baseClass);
    }

    public static Class<?> getBaseClass(String interfaceName) {
        return waterBaseMap.getOrDefault(interfaceName, null);
    }

    public static Map<String, Class<?>> getWaterBaseMap() {
        return waterBaseMap;
    }

    public static void setWaterBaseMap(Map<String, Class<?>> waterBaseMap) {
        WaterBaseRegister.waterBaseMap = waterBaseMap;
    }
}
