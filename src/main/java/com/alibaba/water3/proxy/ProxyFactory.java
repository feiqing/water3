package com.alibaba.water3.proxy;

import com.alibaba.water3.proxy.cglib.CglibProxyFactory;
import com.alibaba.water3.proxy.jdk.JdkProxyFactory;
import com.alibaba.water3.proxy.spring.SpringProxyFactory;
import com.alibaba.water3.reducer.Reducer;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:31.
 */
public class ProxyFactory {

    private static volatile String type = null;

    public static <SPI> SPI newProxy(Class<SPI> spi) {
        return newProxy(spi, null);
    }

    public static <SPI> SPI newProxy(Class<SPI> spi, Reducer<?, ?> reducer) {
        if (type != null) {
            switch (type) {
                case "spring":
                    return SpringProxyFactory.newProxy(spi, reducer);
                case "cglib":
                    return CglibProxyFactory.newProxy(spi, reducer);
                case "jdk":
                    return JdkProxyFactory.newProxy(spi, reducer);
            }
        }

        try {
            Class.forName("org.springframework.cglib.proxy.Enhancer");
            type = "spring";
            return SpringProxyFactory.newProxy(spi, reducer);
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("net.sf.cglib.proxy.Enhancer");
            type = "cglib";
            return CglibProxyFactory.newProxy(spi, reducer);
        } catch (ClassNotFoundException ignored) {
        }

        type = "jdk";
        return JdkProxyFactory.newProxy(spi, reducer);
    }
}
