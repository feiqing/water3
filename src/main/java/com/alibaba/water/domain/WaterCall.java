package com.alibaba.water.domain;

/**
 * @author qingfei
 * @date 2022/08/12
 */
@FunctionalInterface
public interface WaterCall<T> {

    /**
     * 回调方法
     *
     * @param t
     * @return
     */
    void callBack(T t);
}
