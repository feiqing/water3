package com.alibaba.water3.plugin.support;

import com.alibaba.water3.WaterContext;
import com.alibaba.water3.plugin.PluginInvocation;
import com.alibaba.water3.plugin.WaterPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/15 20:25.
 */
public class WaterLoggingPlugin implements WaterPlugin {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Object invoke(PluginInvocation invocation) throws Exception {
        long start = System.currentTimeMillis();
        Object result = null;
        Throwable except = null;
        try {
            return (result = invocation.processed());
        } catch (Throwable t) {
            except = t;
            throw t;
        } finally {
            long rt = System.currentTimeMillis() - start;
            logger.info("|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}",
                    WaterContext.getBizScenario(),
                    invocation.getExtensionAbilityClass().getName(),
                    invocation.getExtensionPointMethod().getName(),
                    WaterContext.getBizDomain(),
                    WaterContext.getBizCode(),
                    invocation.getImpl(),
                    getArgs(invocation),
                    getResult(result),
                    rt,
                    except == null ? "n" : except
            );
        }
    }

    protected Object getArgs(PluginInvocation invocation) {
        return "";
    }

    protected Object getResult(Object result) {
        return "";
    }
}
