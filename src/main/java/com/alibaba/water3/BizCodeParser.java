package com.alibaba.water3;

/**
 * @author qingfei
 * @date 2022/05/02
 */
@FunctionalInterface
public interface BizCodeParser<Param> {

    String TYPE_BUSINESS = "Business";

    String TYPE_ROUTER = "Router";

    default String type(Param param) {
        return TYPE_BUSINESS;
    }

    String parseBizCode(Param param);
}
