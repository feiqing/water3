package cn.anne.domain;

/**
 * @author qingfei
 * @date 2022/5/2
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
