package com.alibaba.water3.plugin;

import com.alibaba.water3.BizContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/15 20:25.
 */
public class WaterLoggingPlugin implements WaterPlugin {

    private static final Logger logger = LoggerFactory.getLogger(WaterLoggingPlugin.class);

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
            logger.info("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|",
                    BizContext.getBizScenario(),
                    invocation.getExtensionAbilityClass().getName(),
                    invocation.getExtensionPointMethod().getName(),
                    BizContext.getBizDomain(),
                    BizContext.getBizCode(),
                    invocation.getImpl(),
                    invocation.getTarget(),
                    getArgs(invocation),
                    getResult(result),
                    rt,
                    except
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
