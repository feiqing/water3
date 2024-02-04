package com.alibaba.water.domain;

/**
 * @author qingfei
 * @date 2022/05/02
 */
public interface WaterScenarioParser<T> {

    /**
     * 业务场景解析
     *
     * @param t
     * @return
     */
    String parser(T t);

    default String parserSubScenario(T t) {
        return null;
    }

}
