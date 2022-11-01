package com.alibaba.water.domain;

import java.io.Serializable;

/**
 * @author qingfei
 * @date 2022/06/02
 */
@FunctionalInterface
public interface WaterCallBack<T, R> extends Serializable {

    /**
     * 回调方法
     *
     * @param t
     * @return
     */
    R callBack(T t);
}
