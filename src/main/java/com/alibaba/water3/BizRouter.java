package com.alibaba.water3;

import com.alibaba.water3.domain.SpiImpls;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/11/13 10:42.
 */
public interface BizRouter {

    SpiImpls route(Class<?> spi, Object[] args);

}
