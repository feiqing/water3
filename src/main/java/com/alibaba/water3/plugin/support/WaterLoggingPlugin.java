package com.alibaba.water3.plugin.support;

import com.alibaba.water3.plugin.PluginInvocation;
import com.alibaba.water3.plugin.WaterPlugin;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/15 20:25.
 */
@Slf4j
public class WaterLoggingPlugin implements WaterPlugin {

    @Override
    public Object invoke(PluginInvocation invocation) throws Exception {
        long start = System.currentTimeMillis();
        try {
            return invocation.processed();
        } finally {
            long ts = System.currentTimeMillis() - start;
            log.info("[{}:{}] invoke ts:{}", invocation.getExtensionAbility().getName(), invocation.getMethod().getName(), ts);
        }
    }
}
