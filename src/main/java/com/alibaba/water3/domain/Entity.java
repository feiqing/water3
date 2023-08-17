package com.alibaba.water3.domain;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 18:08.
 */
public class Entity {

    public static class BusinessScenario {

        @Nonnull
        public final String scenario;

        @Nonnull
        public final Map<Class<?>, ExtensionAbility> class2abilityMap;

        public BusinessScenario(@Nonnull String scenario, @Nonnull Map<Class<?>, ExtensionAbility> class2abilityMap) {
            this.scenario = scenario;
            this.class2abilityMap = ImmutableMap.copyOf(class2abilityMap);
        }
    }

    public static class ExtensionAbility {

        @Nonnull
        public final String clazz;

        // 基础实现是一定不支持懒加载的
        @Nonnull
        public final Object baseImpl;

        @Nonnull
        public final Map<String, ExtensionPoint> method2pointMap;

        public ExtensionAbility(@Nonnull String clazz, @Nonnull Object baseImpl, @Nonnull Map<String, ExtensionPoint> method2pointMap) {
            this.clazz = clazz;
            this.baseImpl = baseImpl;
            this.method2pointMap = ImmutableMap.copyOf(method2pointMap);
        }
    }

    public static class ExtensionPoint {

        @Nonnull
        public final String method;

        @Nonnull
        public final Map<String, List<Business>> baseDomain_id2businessMap;

        @Nonnull
        public final Map<String, List<Business>> extDomain_domain2businessMap;

        @Nonnull
        public final ConcurrentMap<String, ConcurrentMap<String, List<Object>>> domain2id2implCache = new ConcurrentHashMap<>();

        public ExtensionPoint(@Nonnull String method,
                              @Nonnull Map<String, List<Business>> baseDomain_id2businessMap,
                              @Nonnull Map<String, List<Business>> extDomain_domain2businessMap) {
            this.method = method;
            this.baseDomain_id2businessMap = ImmutableMap.copyOf(baseDomain_id2businessMap);
            this.extDomain_domain2businessMap = ImmutableMap.copyOf(extDomain_domain2businessMap);
        }
    }

    public static class Business {

        @Nonnull
        public final String domain;

        @Nonnull
        public final String id;

        @Nullable
        public Tag.Hsf hsf = null;

        @Nullable
        public Tag.Bean bean = null;

        @Nullable
        public volatile Object impl = null;

        private Business(@Nonnull String domain, @Nonnull String id) {
            this.domain = domain;
            this.id = id;
        }

        public static Business newHsfInstance(@Nonnull String domain, @Nonnull String id, @Nonnull Tag.Hsf hsf, @Nullable Object impl) {
            Business business = new Business(domain, id);
            business.hsf = hsf;
            business.impl = impl;
            return business;
        }

        public static Business newBeanInstance(@Nonnull String domain, @Nonnull String id, @Nonnull Tag.Bean bean, @Nullable Object impl) {
            Business business = new Business(domain, id);
            business.bean = bean;
            business.impl = impl;
            return business;
        }
    }
}
