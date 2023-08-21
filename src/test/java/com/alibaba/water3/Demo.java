package com.alibaba.water3;

import com.alibaba.water3.annotation.BizScenario;
import com.alibaba.water3.starter.WaterStaticStarter;

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
        public String parseBizDomain(String[] s) {
            return "AT";
        }

        @Override
        public String parseBizId(String[] s) {
            return s[0];
        }
    }

    public static void main(String[] args) throws Exception {
        WaterStaticStarter.start("classpath:spring-*.xml");

        String a  = Water3.parseBizId(DemoWaterParser.class, new String[]{"base"});
        Object r = Water3.execute(Function.class, function -> function.apply("haha"));
        System.out.println(r);

        Water3.parseBizId(DemoWaterParser.class, new String[]{"yhb"});
        r = Water3.execute(Supplier.class, Supplier::get);
        System.out.println(r);
    }
}
