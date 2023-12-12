package com.alibaba.water3.core;

import com.alibaba.water3.BizContext;
import com.alibaba.water3.BizRouter;
import com.alibaba.water3.domain.Entity;
import com.alibaba.water3.domain.SpiImpls;
import com.alibaba.water3.domain.Tag;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.factory.HsfServiceFactory;
import com.alibaba.water3.factory.SpringBeanFactory;
import com.alibaba.water3.plugin.ExtensionPlugin;
import com.alibaba.water3.utils.DomParser;
import com.alibaba.water3.utils.EntityConvertor;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 17:52.
 */
@Slf4j
public class ExtensionManager {

    private static final String WATER_XML_CONFIG_LOCATION = "classpath*:water3-*.xml";

    private static Map<Class<?>, Entity.Extension> extensionMap;

    private static final ExtensionPlugin[] plugins;

    static {
        try {
            List<ExtensionPlugin> _plugins = new LinkedList<>();
            for (ExtensionPlugin plugin : ServiceLoader.load(ExtensionPlugin.class, ExtensionPlugin.class.getClassLoader())) {
                log.info("loaded [ExtensionPlugin]: {}", plugin);
                _plugins.add(plugin);
            }
            plugins = _plugins.toArray(new ExtensionPlugin[0]);
        } catch (Throwable t) {
            log.error("loading [ExtensionPlugin] error.", t);
            throw new RuntimeException(t);
        }
    }

    public static ExtensionPlugin[] getPlugins() {
        return plugins;
    }

    public static void register(String configStyle, Consumer<Set<Tag.Extension>> processor) throws Exception {
        // 1. 加载业务配置(目前只支持XML, 未来根据需要扩展更多的配置方式: yaml? json? groovy? java?)
        Set<Tag.Extension> extensions = DomParser.loadingConfigFiles(WATER_XML_CONFIG_LOCATION);

        // 2. 预留一个扩展接口, 可以注入外部的配置
        if (processor != null) {
            processor.accept(extensions);
        }

        // 3. 将配置转换为实体
        extensionMap = ImmutableMap.copyOf(EntityConvertor.toExtensionMap(extensions));
    }

    public static Map<Class<?>, Entity.Extension> getExtensionMap() {
        return extensionMap;
    }

    protected static SpiImpls getSpiImpls(Class<?> spi, Object... args) {
        BizRouter bizRouter = BizContext.getBizRouter();
        if (bizRouter == null) {
            throw new WaterException("[BizRouter] can't be null: please invoke Water3.parseBizCode(...) before.");
        }

        Entity.Extension extension = extensionMap.get(spi);
        if (extension == null) {
            throw new WaterException(String.format("ExtensionSpi:[%s] not found.", spi.getName()));
        }
        BizContext.addBusinessExt("__group__", extension.group);

        SpiImpls impls = bizRouter.route(spi, args);
        if (CollectionUtils.isEmpty(impls)) {
            throw new WaterException(String.format("[BizRouter] '%s' [Spi] '%s' route return empty impls !", bizRouter, spi));
        }
        return impls;
    }

    public static SpiImpls getBusinessSpiImpls(Class<?> spi, String bizCode) {
        Entity.Extension extension = extensionMap.get(spi);
        Preconditions.checkState(extension != null);

        return extension.BUSINESS_CODE2IMPL_CACHE.computeIfAbsent(bizCode, _K -> {
            List<Entity.Business> business = extension.businessMap.get(bizCode);
            if (CollectionUtils.isEmpty(business)) {
                return new SpiImpls(Collections.singletonList(new SpiImpls.SpiImpl("base", extension.base)));
            } else {
                return new SpiImpls(business.stream().map(ExtensionManager::makeImpl).collect(toList()));
            }
        });
    }

    private static SpiImpls.SpiImpl makeImpl(Entity.Business entity) {
        if (entity.instance != null) {
            return new SpiImpls.SpiImpl(entity.type, entity.instance);
        }

        // tips: 懒加载的具体实现
        try {
            if (entity.hsf != null) {
                entity.instance = HsfServiceFactory.getHsfService(entity.hsf);
            }
            if (entity.bean != null) {
                entity.instance = SpringBeanFactory.getSpringBean(entity.bean);
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }

        Preconditions.checkState(entity.instance != null);

        return new SpiImpls.SpiImpl(entity.type, entity.instance);
    }

    //    private static SpiImpls invokeRouterImpls(String bizCode, Class<?> spi, Object... args) throws InvocationTargetException, IllegalAccessException {
//        Entity.Extension extension = extensionMap.get(spi);
//        if (extension == null) {
//            throw new WaterException(String.format("ExtensionSpi:[%s] not found.", spi.getName()));
//        }
//
//        List<Entity.Router> routers = extension.ROUTER_CODE2ROUTER_CACHE.computeIfAbsent(bizCode, _K -> {
//            List<Entity.Router> _routers = new ArrayList<>(extension.routerList.size());
//            for (Entity.Router router : extension.routerList) {
//                if (match(router.code, bizCode)) {
//                    _routers.add(router);
//                }
//            }
//            // tips: match后重新基于优先级排序
//            _routers.sort(Comparator.comparing(router -> router.priority));
//            return _routers;
//        });
//
//        SpiImpls implList = new SpiImpls();
//        for (Entity.Router router : routers) {
//            SpiImpls impls = (SpiImpls) router.method.invoke(router.instance, args);
//            if (!CollectionUtils.isEmpty(impls)) {
//                implList.addAll(impls);
//            }
//        }
//
//        if (implList.isEmpty()) {
//            return new SpiImpls(Collections.singletonList(new SpiImpls.SpiImpl("base", extension.base)));
//        }
//
//        return implList;
//    }
}
