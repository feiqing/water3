package com.alibaba.water.domain;

import java.util.List;

/**
 * @author 靖杨
 * @describe
 * @since 2023/2/15
 */
public interface WaterRouterInterface {
    List<Class<?>> route(String subScenario,String interfaceName, String method);
}
