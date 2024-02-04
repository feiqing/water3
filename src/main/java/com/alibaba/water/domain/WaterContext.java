package com.alibaba.water.domain;

import com.alibaba.water3.BizContext;

/**
 * @author qingfei
 * @date 2022/05/07
 */
public class WaterContext {

    public static String getBizScenario() {
        return BizContext.getBizCode();
    }

    public static String getSubBizScenario() {
        return BizContext.getBusinessExt("_xgbc_");
    }
}
