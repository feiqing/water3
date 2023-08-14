package com.alibaba.water3.plugin;

import java.lang.reflect.InvocationTargetException;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/12 11:20.
 */
public interface Water3Plugin {

    Object invoke(PluginInvocation invocation) throws InvocationTargetException, IllegalAccessException;
}
