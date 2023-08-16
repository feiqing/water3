package com.alibaba.water3;

/**
 * @author qingfei
 * @date 2022/05/02
 */
public interface WaterParser<T> {

    String parseBizId(T t);
}
