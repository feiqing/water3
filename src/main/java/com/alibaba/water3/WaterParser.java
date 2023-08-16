package com.alibaba.water3;

/**
 * @author qingfei
 * @date 2022/05/02
 */
@FunctionalInterface
public interface WaterParser<T> {

    String parseBizId(T t);
}
