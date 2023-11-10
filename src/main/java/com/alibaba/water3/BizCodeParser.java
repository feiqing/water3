package com.alibaba.water3;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:31.
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
