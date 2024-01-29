package com.alibaba.water3.utils;

import com.alibaba.water3.domain.Entity;
import com.alibaba.water3.domain.Tag;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.factory.HsfServiceFactory;
import com.alibaba.water3.factory.SpringBeanFactory;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/16 20:45.
 */
@Slf4j
public class EntityConvertor {

    public static Map<Class<?>, Entity.Extension> toExtensionMap(Collection<Tag.Extension> extensionTags) throws Exception {
        Map<Class<?>, Entity.Extension> extensionMap = new HashMap<>(extensionTags.size());
        for (Tag.Extension extensionTag : extensionTags) {
            Object base = SpringBeanFactory.getSpringBean(new Tag.Bean(extensionTag.base));

            Map<String, List<Entity.Business>> businessMap = toBusinessMap(extensionTag.businessList);

            Entity.Extension extension = new Entity.Extension(extensionTag.clazz, base, businessMap);
            extension.group = extensionTag.group;
            extension.proxy = extensionTag.proxy;

            extensionMap.put(Class.forName(extensionTag.clazz), extension);
        }

        return extensionMap;
    }

    private static Map<String, List<Entity.Business>> toBusinessMap(List<Tag.Business> tags) throws Exception {
        // tips: 此处要将code分割后重新排列
        Map<String, List<Tag.Business>> code2tags = new HashMap<>();
        for (Tag.Business tag : tags) {
            for (String code : Splitter.on(",").trimResults().omitEmptyStrings().split(tag.code)) {
                code2tags.computeIfAbsent(code, _k -> new ArrayList<>()).add(tag);
            }
        }

        Map<String, List<Entity.Business>> code2businessMap = new HashMap<>(code2tags.size());
        for (Map.Entry<String, List<Tag.Business>> entry : code2tags.entrySet()) {
            // tips: 基于优先级重新排序
            // -> 业务优先级在code打散之后就可以进行重新排序了
            // -> 但是路由的优先级需要match后再进行重新排序
            entry.getValue().sort(Comparator.comparing(bizTag -> bizTag.priority));

            List<Entity.Business> businessList = new ArrayList<>(entry.getValue().size());
            for (Tag.Business tag : entry.getValue()) {
                businessList.add(toBusinessEntity(tag));
            }

            code2businessMap.put(entry.getKey(), businessList);
        }

        return code2businessMap;
    }

    private static Entity.Business toBusinessEntity(Tag.Business tag) throws Exception {
        if (tag.bean != null) {
            return Entity.Business.newBeanInstance(tag.code, tag.type, tag.bean, getSpringBean(tag.bean));
        }

        if (tag.hsf != null) {
            return Entity.Business.newHsfInstance(tag.code, tag.type, tag.hsf, getHsfService(tag.hsf));
        }

        throw new WaterException(String.format("Business:[%s] <bean/> and <hsf/> definition all empty.", tag.code));
    }

    private static Object getSpringBean(Tag.Bean bean) {
//        if (SystemConfig.isGlobalCloseLazyLoading()) {
//            return SpringBeanFactory.getSpringBean(bean);
//        }
//
//        if (SystemConfig.isGlobalOpenLazyLoading()) {
//            return null;
//        }

        if (!bean.lazy) {
            return SpringBeanFactory.getSpringBean(bean);
        }

        return null;
    }

    private static Object getHsfService(Tag.Hsf hsf) throws Exception {
//        if (SystemConfig.isGlobalCloseLazyLoading()) {
//            return HsfServiceFactory.getHsfService(hsf);
//        }
//
//        if (SystemConfig.isGlobalOpenLazyLoading()) {
//            return null;
//        }

        if (!hsf.lazy) {
            return HsfServiceFactory.getHsfService(hsf);
        }

        return null;
    }

//    private static List<Entity.Router> toRouterList(List<Tag.Router> tags) throws Exception {
//        List<Entity.Router> routerList = new ArrayList<>(tags.size());
//        for (Tag.Router tag : tags) {
//            routerList.add(toRouterEntity(tag));
//        }
//        return routerList;
//    }
//
//    private static Entity.Router toRouterEntity(Tag.Router tag) throws Exception {
//
//        Object instance = null;
//        Class<?> instanceType = null;
//        boolean needStaticMethod = false;
//        if (StringUtils.startsWith(tag.type, "@class")) {
//            String className = StringUtils.substringBeforeLast(StringUtils.substringAfter(tag.type, "('"), "')");
//            instanceType = Class.forName(className);
//            needStaticMethod = true;
//        } else if (StringUtils.startsWith(tag.type, "@instance")) {
//            String className = StringUtils.substringBeforeLast(StringUtils.substringAfter(tag.type, "('"), "')");
//            instanceType = Class.forName(className);
//            instance = instanceType.newInstance();
//        } else if (StringUtils.startsWith(tag.type, "@bean")) {
//            String beanName = StringUtils.substringBeforeLast(StringUtils.substringAfter(tag.type, "('"), "')");
//            instance = SpringBeanFactory.getSpringBean(new Tag.Bean(beanName));
//            Preconditions.checkState(instance != null);
//            instanceType = instance.getClass();
//        }
//
//        if (instanceType == null) {
//            throw new WaterException(String.format("Router:[%s] type must one of @class/@instance/@bean", tag.code));
//        }
//
//        Method method = findRouterMethod(tag.code, instanceType, tag.method, needStaticMethod);
//
//        Entity.Router router = Entity.Router.newRouter(tag.code, tag.type, method, instance);
//        router.priority = tag.priority;
//        return router;
//    }
//
//
//    private static Method findRouterMethod(String code, Class<?> instanceType, String method, boolean needStaticMethod) {
//        Set<Method> methods = new HashSet<>();
//        for (Method input : instanceType.getDeclaredMethods()) {
//            if (!StringUtils.equals(input.getName(), method)) {
//                continue;
//            }
//
//            if (!Modifier.isPublic(input.getModifiers())) {
//                throw new WaterException(String.format("Router:[%s] method:[%s] must public.", code, method));
//            }
//
//            if (needStaticMethod && !Modifier.isStatic(input.getModifiers())) {
//                throw new WaterException(String.format("Router:[%s] method:[%s] must static.", code, method));
//            }
//
//            if (!SpiImpls.class.isAssignableFrom(input.getReturnType())) {
//                throw new WaterException(String.format("Router:[%s] method:[%s] must return type 'com.alibaba.water3.domain.SpiImpls'.", code, method));
//            }
//
//            methods.add(input);
//        }
//
//        if (methods.isEmpty()) {
//            throw new WaterException(String.format("Router:[%s](class:[%s]) method:[%s] not found.", code, instanceType, method));
//        }
//
//        if (methods.size() > 1) {
//            log.warn("Router:[{}] founded method:[{}] more than one, use  first!!!", code, method);
//        }
//
//        return methods.iterator().next();
//    }
}
