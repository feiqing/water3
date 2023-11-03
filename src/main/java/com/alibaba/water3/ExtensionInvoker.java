package com.alibaba.water3;

/**
 * @author qingfei
 * @date 2022/06/02
 */
@FunctionalInterface
public interface ExtensionInvoker<T, R> {

    /**
     * 回调方法
     *
     * @param t
     * @return
     */
    R invoke(T t);

}
