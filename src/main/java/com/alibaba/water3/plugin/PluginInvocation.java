package com.alibaba.water3.plugin;

import java.lang.reflect.Method;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/12 11:21.
 */
public interface PluginInvocation {

    Object[] getArgs();

    Method getMethod();

    Object getTarget();

    Object processed();
}
