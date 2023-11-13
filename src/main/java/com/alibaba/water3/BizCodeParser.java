package com.alibaba.water3;

import com.alibaba.water3.core.BusinessRouter;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:31.
 */
@FunctionalInterface
public interface BizCodeParser<Param> {

    BusinessRouter BUSINESS_ROUTER = new BusinessRouter();

    default BizRouter getBizRouter(Param param) {
        return BUSINESS_ROUTER;
    }

    String parseBizCode(Param param);
}
