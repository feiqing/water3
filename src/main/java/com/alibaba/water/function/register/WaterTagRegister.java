package com.alibaba.water.function.register;

import com.alibaba.water3.core.ExtensionManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 1、一个Impl有多个tag（复用）
 * 2、同一个接口的impl中可能存在多个impl对应同一个tag（叠加），优先级问题
 *
 * @author qingfei
 * @date 2022/05/07
 */
public class WaterTagRegister {

    // base 路由: 替换成了新的water3实现, 但一定要保留原始的方法签名
    public static List<Class<?>> getImplClassListByInterfaceAndTag(String spi, String bizCode) {
        return ExtensionManager.getBusinessSpiImpls(_spi(spi), bizCode).stream().filter(impl -> !StringUtils.equals(impl.type, "base")).map(impl -> AopUtils.getTargetClass(impl.instance)).collect(Collectors.toList());
    }

    private static final ConcurrentMap<String, Class<?>> spi2class = new ConcurrentHashMap<>();

    private static Class<?> _spi(String spi) {
        return spi2class.computeIfAbsent(spi, _K -> {
            try {
                return Class.forName(spi);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
