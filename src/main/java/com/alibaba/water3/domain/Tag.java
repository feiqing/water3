package com.alibaba.water3.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 18:08.
 */
public class Tag {

    public static class BusinessScenario implements Serializable {

        private static final long serialVersionUID = -8226078980396592923L;

        public String scenario;

        public String desc;

        public List<ExtensionAbility> extensionAbilityList;

        public BusinessScenario(String scenario) {
            this.scenario = scenario;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BusinessScenario that = (BusinessScenario) o;
            return Objects.equals(scenario, that.scenario);
        }

        @Override
        public int hashCode() {
            return Objects.hash(scenario);
        }
    }

    public static class ExtensionAbility implements Serializable {

        private static final long serialVersionUID = 6557095413659207121L;

        public String clazz;

        public String base;

        public String desc;

        public List<ExtensionPoint> extensionPointList;

        public ExtensionAbility(String clazz, String base) {
            this.clazz = clazz;
            this.base = base;
        }
    }

    public static class ExtensionPoint implements Serializable {

        private static final long serialVersionUID = 8266020009404590458L;

        public String method;

        public String desc;

        public String args;

        public String result;

        public List<Business> businesList;

        public ExtensionPoint(String method) {
            this.method = method;
        }
    }

    public static class Business implements Serializable {

        private static final long serialVersionUID = -253145366338161711L;

        public String id;

        public String impl;

        public String desc;

        public Bean bean;

        public Hsf hsf;

        public long priority = 0L;

        public Business(String id, String impl) {
            this.id = id;
            this.impl = impl;
        }
    }

    public static class Bean implements Serializable {

        private static final long serialVersionUID = -1946088618528756952L;

        public String name;

        public boolean lazy = false;

        public Bean(String name) {
            this.name = name;
        }
    }

    public static class Hsf implements Serializable {

        private static final long serialVersionUID = -6757315625134300013L;

        public String service;

        public String version;

        public String group = "HSF";

        public Integer timeout;

        public boolean lazy = false;

        public Hsf(String service, String version) {
            this.service = service;
            this.version = version;
        }
    }
}
