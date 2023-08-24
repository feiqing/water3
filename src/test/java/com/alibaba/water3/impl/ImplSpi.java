package com.alibaba.water3.impl;

import com.alibaba.water3.spi.ExtSpi;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/24 16:34.
 */
public class ImplSpi implements ExtSpi {

    @Override
    public String handle(String arg) {
        return "imple " + arg;
    }
}
