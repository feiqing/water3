package com.alibaba.water3.domain;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 18:08.
 */
public class Entity {

    public static class ExtensionGroup {

        @Nonnull
        public final String name;

        @Nonnull
        public final ConcurrentMap<Class<?>, ExtensionAbility> class2abilityMap;

        public ExtensionGroup(@Nonnull String name, @Nonnull ConcurrentMap<Class<?>, ExtensionAbility> class2abilityMap) {
            this.name = name;
            this.class2abilityMap = class2abilityMap;
        }
    }

    public static class ExtensionAbility {

        @Nonnull
        public final String clazz;

        // 基础实现是一定不支持懒加载的
        @Nonnull
        public final Object baseImpl;

        @Nonnull
        public final ConcurrentMap<String, ExtensionPoint> method2pointMap;

        public ExtensionAbility(@Nonnull String clazz, @Nonnull Object baseImpl, @Nonnull ConcurrentMap<String, ExtensionPoint> method2pointMap) {
            this.clazz = clazz;
            this.baseImpl = baseImpl;
            this.method2pointMap = method2pointMap;
        }
    }

    public static class ExtensionPoint {

        @Nonnull
        public final String method;

        @Nonnull
        public final ConcurrentMap<String, List<Business>> code2businessMap;

        @Nonnull
        public final ConcurrentMap<String, List<Object>> code2implCache = new ConcurrentHashMap<>();

        public ExtensionPoint(@Nonnull String method, @Nonnull ConcurrentMap<String, List<Business>> code2businessMap) {
            this.method = method;
            this.code2businessMap = code2businessMap;
        }
    }

    public static class Business {

        @Nonnull
        public final String code;

        @Nullable
        public Tag.Hsf hsf = null;

        @Nullable
        public Tag.Bean bean = null;

        @Nullable
        public volatile Object impl = null;

        private Business(@Nonnull String code) {
            this.code = code;
        }

        public static Business newHsfInstance(@Nonnull String code, @Nonnull Tag.Hsf hsf, @Nullable Object impl) {
            Business business = new Business(code);
            business.hsf = hsf;
            business.impl = impl;
            return business;
        }

        public static Business newBeanInstance(@Nonnull String code, @Nonnull Tag.Bean bean, @Nullable Object impl) {
            Business business = new Business(code);
            business.bean = bean;
            business.impl = impl;
            return business;
        }
    }
}
