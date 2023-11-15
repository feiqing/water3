package com.alibaba.water3;

import com.alibaba.water3.core.BaseBizRouter;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:31.
 */
@FunctionalInterface
public interface BizCodeParser<Param> {

    BizRouter BASE_BIZ_ROUTER = new BaseBizRouter();

    default BizRouter getBizRouter(Param param) {
        return BASE_BIZ_ROUTER;
    }

    String parseBizCode(Param param);
}
