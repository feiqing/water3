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

    private int idx = -1;

    @Getter
    private final Class<?> extensionAbility;

    @Getter
    @Setter
    private Method method;

    @Getter
    @Setter
    private Object target;

    @Getter
    @Setter
    private Object[] args;

    private final WaterPlugin[] plugins;

    public PluginInvocation(Class<?> extensionAbility, Method method, Object target, Object[] args, WaterPlugin[] plugins) {
        this.extensionAbility = extensionAbility;
        this.method = method;
        this.target = target;
        this.args = args;
        this.plugins = plugins;
    }

    public Object processed() throws Exception {
        if (++idx < plugins.length) {
            return plugins[idx].invoke(this);
        } else {
            return method.invoke(target, args);
        }
    }
}
