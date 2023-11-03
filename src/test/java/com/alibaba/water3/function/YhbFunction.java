package com.alibaba.water3.function;

import com.alibaba.water3.MsgProcFunc;
import com.alibaba.water3.domain.SpiImpls;
import com.alibaba.water3.spi.ExtSpi;

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

    public static SpiImpls taiSbLe(String s) {

        SpiImpls impls = new SpiImpls(1);

        impls.add(new SpiImpls.SpiImpl("xxx", new ExtSpi() {
            @Override
            public String handle(String arg) {
                return "custom: " + arg;
            }

            @MsgProcFunc
            public String handle1(String arg) {
                return "handle1: " + arg;
            }

            @MsgProcFunc
            public String handle2(String arg) {
                return "handle2: " + arg;
            }

            @MsgProcFunc
            public String handle3(String arg) {
                return "handle3: " + arg;
            }
        }));

        return impls;
    }
}
