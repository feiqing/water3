package com.alibaba.water.domain;

/**
 * @author qingfei
 * @date 2022/05/02
 */
public interface WaterScenarioParser<T extends WaterParamRequest> {

    /**
     * 业务场景解析
     *
     * @param t
     * @return
     */
    String parser(T t);
}
