package com.alibaba.water3.plugin;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/12 11:20.
 */
public interface WaterExecutePlugin {

    Object invoke(PluginInvocation invocation) throws Exception;
}
