package com.alibaba.water3.core;

import com.alibaba.water3.BizContext;
import com.alibaba.water3.BizRouter;
import com.alibaba.water3.domain.SpiImpls;
import com.alibaba.water3.exception.WaterException;
import com.google.common.base.Strings;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/11/13 10:48.
 */
public class BaseBizRouter implements BizRouter {

    @Override
    public SpiImpls route(Class<?> spi, Object[] args) {

        String bizCode = BizContext.getBizCode();
        if (Strings.isNullOrEmpty(bizCode)) {
            throw new WaterException("[BizCode] can't be empty: please invoke Water3.parseBizCode(...) before.");
        }

        return ExtensionManager.getBusinessSpiImpls(spi, bizCode);
    }
}
