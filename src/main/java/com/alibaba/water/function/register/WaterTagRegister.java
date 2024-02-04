package com.alibaba.water.function.register;

import com.alibaba.water.domain.WaterRouterInterface;
import com.alibaba.water3.BizContext;
import com.alibaba.water3.core.ExtensionManager;
import com.alibaba.water3.domain.SpiImpls;
import com.alibaba.water3.factory.SpringBeanFactory;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 1、一个Impl有多个tag（复用）
 * 2、同一个接口的impl中可能存在多个impl对应同一个tag（叠加），优先级问题
 *
 * @author qingfei
 * @date 2022/05/07
 */
public class WaterTagRegister {

    private static final ConcurrentMap<String, WaterRouterInterface> bizCode2Router = new ConcurrentHashMap<>();

    public static void registerCustomRouter(String bizCode, WaterRouterInterface router) {
        bizCode2Router.put(bizCode, router);
    }

    public static boolean hasCustomRouter(String bizCode) {
        return bizCode2Router.containsKey(bizCode);
    }

    // 自定义路由
    public static SpiImpls customRoute(String spi, String bizCode, String extCode, String method) {
        WaterRouterInterface router = bizCode2Router.get(bizCode);
        Preconditions.checkState(router != null);
        List<Class<?>> classes = router.route(extCode, spi, method);
        if (CollectionUtils.isEmpty(classes)) {
            SpiImpls impls = new SpiImpls(1);
            impls.add(BizContext.getBusinessExt("_base_"));
            BizContext.removeBusinessExt("_base_");
            return impls;
        }

        SpiImpls impls = new SpiImpls(classes.size());
        for (Class<?> clazz : classes) {
            impls.add(new SpiImpls.SpiImpl("at-bean", SpringBeanFactory.getSpringBean(clazz)));
        }
        return impls;
    }

    // base 路由: 替换成了新的water3实现, 但一定要保留原始的方法签名
    public static List<Class<?>> getImplClassListByInterfaceAndTag(String spi, String bizCode) {
        List<Class<?>> result = new LinkedList<>();

        for (SpiImpls.SpiImpl impl : ExtensionManager.getBusinessSpiImpls(_spi(spi), bizCode)) {
            if (StringUtils.equals(impl.type, "base")) {
                BizContext.addBusinessExt("_base_", impl);
                continue;
            }

            result.add(AopUtils.getTargetClass(impl.instance));
        }

        return result;
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
