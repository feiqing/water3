package com.alibaba.water3;

import com.alibaba.water3.domain.Tag;

/**
 * @author qingfei
 * @date 2022/05/02
 */
@FunctionalInterface
public interface WaterParser<T> {

    default String parseBizDomain(T t) {
        return Tag.DOMAIN_BASE;
    }

    String parseBizId(T t);
}
