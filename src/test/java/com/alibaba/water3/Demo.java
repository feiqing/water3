package com.alibaba.water3;

import com.alibaba.water3.annotation.BizScenario;
import com.alibaba.water3.starter.StaticWater3Starter;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/16 14:10.
 */
public class Demo {

    @BizScenario("ORDER_CREATE")
    public static class DemoWaterParser implements WaterParser<String[]> {
        @Override
        public String parseBizId(String[] s) {
            return s[0];
        }
    }

    public static void main(String[] args) throws Exception {
        StaticWater3Starter.start("classpath:spring-*.xml");
        Water3.parseBizId(DemoWaterParser.class, new String[]{"string"});

        Object r = Water3.execute(Function.class, function -> function.apply("haha"));
        WaterContext.setBizId("yhb");
        System.out.println(r);
        r = Water3.execute(Supplier.class, function -> function.get());
        System.out.println(r);
    }
}
