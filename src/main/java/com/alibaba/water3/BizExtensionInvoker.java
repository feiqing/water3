package com.alibaba.water3;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:31.
 */
@FunctionalInterface
public interface BizExtensionInvoker<T, R> {

    /**
     * 回调方法
     *
     * @param t
     * @return
     */
    R invoke(T t);

}
