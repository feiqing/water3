package com.alibaba.water3.plugin;

import com.alibaba.water3.BizContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.water3.utils.SysNamespace.GROUP;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/15 20:25.
 */
public class ExtensionLoggingPlugin implements ExtensionPlugin {

    private static final Logger logger = LoggerFactory.getLogger("water");

    @Override
    public Object invoke(ExtensionInvocation invocation) throws Exception {
        long start = System.currentTimeMillis();
        String group = BizContext.getBusinessExt(GROUP);
        String spi = invocation.getExtensionSpi().getName();
        String method = invocation.getExtensionMethod().getName();
        String router = BizContext.getBizRouter().getClass().getSimpleName();
        String bizCode = BizContext.getBizCode();
        String type = invocation.getType();
        Object instance = invocation.getInstance();
        Object[] args = invocation.getArgs();
        Object result = null;
        Throwable except = null;
        try {
            return (result = invocation.proceed());
        } catch (Throwable t) {
            except = t;
            throw t;
        } finally {
            long rt = System.currentTimeMillis() - start;
            if (except == null) {
                logger.info("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|", group, spi, method, router, bizCode, type, instance, getArgs(args), getResult(result), rt);
            } else {
                logger.error("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|", group, spi, method, router, bizCode, type, instance, getArgs(args), getResult(result), rt,
                        except);
            }
        }
    }

    protected Object getArgs(Object[] args) {
        return "";
    }

    protected Object getResult(Object result) {
        return "";
    }
}
