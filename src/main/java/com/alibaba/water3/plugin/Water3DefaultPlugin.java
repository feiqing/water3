package com.alibaba.water3.plugin;

import java.lang.reflect.InvocationTargetException;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/12 11:23.
 */
public class Water3DefaultPlugin implements Water3Plugin {

    @Override
    public Object invoke(PluginInvocation invocation) throws InvocationTargetException, IllegalAccessException {
        return invocation.getMethod().invoke(invocation.getTarget(), invocation.getArgs());
    }
}
