package com.alibaba.water3.plugin;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/12 11:21.
 */
public class PluginInvocation {

    @Getter
    private final Class<?> extensionAbilityClass;

    @Getter
    @Setter
    private Method extensionPointMethod;

    @Getter
    @Setter
    private String impl;

    @Getter
    @Setter
    private Object target;

    @Getter
    @Setter
    private Object[] args;

    private final WaterPlugin[] plugins;

    public PluginInvocation(Class<?> extensionAbilityClass, Method extensionPointMethod, String impl, Object target, Object[] args, WaterPlugin[] plugins) {
        this.extensionAbilityClass = extensionAbilityClass;
        this.extensionPointMethod = extensionPointMethod;
        this.target = target;
        this.args = args;
        this.plugins = plugins;
    }

    private int idx = -1;

    public Object processed() throws Exception {
        if (++idx < plugins.length) {
            return plugins[idx].invoke(this);
        } else {
            return extensionPointMethod.invoke(target, args);
        }
    }
}
