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
        public final Map<Class<?>, ExtensionAbility> abilityMap;

        public BusinessScenario(@Nonnull String scenario, @Nonnull Map<Class<?>, ExtensionAbility> abilityMap) {
            this.scenario = scenario;
            this.abilityMap = ImmutableMap.copyOf(abilityMap);
        }
    }

    public static class ExtensionAbility {

        @Nonnull
        public final String clazz;

        // 基础实现是一定不支持懒加载的
        @Nonnull
        public final Object base;

        @Nonnull
        public final Map<String, ExtensionPoint> pointMap;

        public ExtensionAbility(@Nonnull String clazz, @Nonnull Object base, @Nonnull Map<String, ExtensionPoint> pointMap) {
            this.clazz = clazz;
            this.base = base;
            this.pointMap = ImmutableMap.copyOf(pointMap);
        }
    }

    public static class ExtensionPoint {

        @Nonnull
        public final String method;

        @Nonnull
        public final Map<String, List<Business>> baseDomainBusinessMap; // bizCode -> business

        @Nonnull
        public final Map<String, List<Business>> extDomainBusinessMap; // domain -> business

        @Nonnull
        public final ConcurrentMap<String, ConcurrentMap<String, List<InstanceWrapper>>> DOMAIN_CODE_INSTANCE_CACHE = new ConcurrentHashMap<>();

        public ExtensionPoint(@Nonnull String method, @Nonnull Map<String, List<Business>> baseDomainBbusinessMap,
                              @Nonnull Map<String, List<Business>> extDomainBusinessMap) {
            this.method = method;
            this.baseDomainBusinessMap = ImmutableMap.copyOf(baseDomainBbusinessMap);
            this.extDomainBusinessMap = ImmutableMap.copyOf(extDomainBusinessMap);
        }
    }

    public static class InstanceWrapper {

        @Nonnull
        public final String impl;

        @Nonnull
        public final Object instance;

        public InstanceWrapper(@Nonnull String impl, @Nonnull Object instance) {
            this.impl = impl;
            this.instance = instance;
        }
    }

    public static class Business {

        @Nonnull
        public final String domain;

        @Nonnull
        public final String code;

        @Nonnull
        public final String impl;

        @Nullable
        public Tag.Hsf hsf = null;

        @Nullable
        public Tag.Bean bean = null;

        @Nullable
        public volatile Object instance = null;

        public Business(@Nonnull String domain, @Nonnull String code, @Nonnull String impl) {
            this.domain = domain;
            this.code = code;
            this.impl = impl;
        }

        public static Business newHsfInstance(@Nonnull String domain, @Nonnull String code, @Nonnull String impl, @Nonnull Tag.Hsf hsf,
                                              @Nullable Object instance) {
            Business business = new Business(domain, code, impl);
            business.hsf = hsf;
            business.instance = instance;
            return business;
        }

        public static Business newBeanInstance(@Nonnull String domain, @Nonnull String code, @Nonnull String impl, @Nonnull Tag.Bean bean,
                                               @Nullable Object instance) {
            Business business = new Business(domain, code, impl);
            business.bean = bean;
            business.instance = instance;
            return business;
        }
    }
}
