package com.alibaba.water.function.register;

import java.util.Map;

import com.alibaba.water.exception.WaterException;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qingfei
 * @date 2022/10/23
 */
public class WaterBaseRegister {

    private static final Logger log = LoggerFactory.getLogger(WaterBaseRegister.class);

    private static Map<String, Class<?>> waterBaseMap = Maps.newConcurrentMap();

    public static void register(String interfaceName, Class<?> baseClass) {
        if (waterBaseMap.containsKey(interfaceName)) {
            log.error("extension has more than one base implement, extension:{}", interfaceName);
            throw new WaterException("同一个扩展点存在多个base实现类");
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
