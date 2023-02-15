package com.alibaba.water.domain;

/**
 * @author 靖杨
 * @describe
 * @since 2023/2/15
 */
public interface WaterRouterInterface {
    Class<?> route(String method);
}
