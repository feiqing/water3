package com.alibaba.water3.function;

import java.util.function.Supplier;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 23:26.
 */
public class BaseSupplier implements Supplier<Object> {

    @Override
    public Object get() {
        return "base get()";
    }
}
