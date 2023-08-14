package com.alibaba.water3.demo;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 23:28.
 */
public class YhbFunction extends BaseFunction {

    @Override
    public Object apply(Object arg) {
        if (arg != null) {
            System.out.println("argType: " + arg.getClass());
        }
        return "yhb apply(arg)";
    }
}
