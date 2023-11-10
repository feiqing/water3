package com.alibaba.water3.proxy;

import com.alibaba.water3.proxy.cglib.CglibProxyFactory;
import com.alibaba.water3.proxy.jdk.JdkProxyFactory;
import com.alibaba.water3.proxy.spring.SpringProxyFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:31.
 */
@SuppressWarnings("unchecked")
public class ProxyFactory {

    private static volatile String type = null;

    private static final ConcurrentMap<Class<?>, Object> proxies = new ConcurrentHashMap<>();

    public static <SPI> SPI getProxy(Class<SPI> spi) {
        return (SPI) proxies.computeIfAbsent(spi, ProxyFactory::newProxy);
    }

    private static <SPI> Object newProxy(Class<SPI> spi) {
        if (type != null) {
            switch (type) {
                case "spring":
                    return SpringProxyFactory.newProxy(spi);
                case "cglib":
                    return CglibProxyFactory.newProxy(spi);
                case "jdk":
                    return JdkProxyFactory.newProxy(spi);
            }
        }

        try {
            Class.forName("org.springframework.cglib.proxy.Enhancer");
            type = "spring";
            return SpringProxyFactory.newProxy(spi);
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("net.sf.cglib.proxy.Enhancer");
            type = "cglib";
            return CglibProxyFactory.newProxy(spi);
        } catch (ClassNotFoundException ignored) {
        }

        type = "jdk";
        return JdkProxyFactory.newProxy(spi);
    }
}
