package com.alibaba.water3;

import com.alibaba.water3.reducer.Reducer;
import com.alibaba.water3.spi.ExtSpi;
import com.alibaba.water3.starter.WaterStaticStarter;
import com.google.common.base.Preconditions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/16 14:10.
 */
public class Demo {

    public static class DemoBizCodeParser implements BizCodeParser<String[]> {

        @Override
        public String parseBizCode(String[] s) {
            return s[0];
        }
    }

    public static void main(String[] args) throws Exception {
        WaterStaticStarter.start("classpath:spring-*.xml");

        String bizCode = Water3.parseBizCode(DemoBizCodeParser.class, new String[]{"base"});

        String execute = Water3.execute(ExtSpi.class, spi -> spi.handle(bizCode));
        System.out.println(execute);

//        List<Object> results = Water3.execute(ExtSpi.class, (Predicate<Method>) method -> method.isAnnotationPresent(MsgProcFunc.class), Reducers.collect()
//        , "hhh");
//        System.out.println(results);

        Water3.extExecute(ExtSpi.class, spiImpl -> {
            Method[] implMethods = spiImpl.instance.getClass().getDeclaredMethods();
            List<Method> methods = new ArrayList<>(implMethods.length);
            for (Method method : implMethods) {
                if (method.isAnnotationPresent(MsgProcFunc.class)) {
                    method.setAccessible(true);
                    methods.add(method);
                }
            }
            methods.sort(Comparator.comparingInt(o -> o.getAnnotation(MsgProcFunc.class).order()));

            return methods;
        }, allMatch, "xxxx");


//        Object r = Water3.execute(Function.class, function -> function.apply("haha"));
//        System.out.println(r);
//
//        Water3.parseBizCode(DemoWaterParser.class, new String[]{"yhb"});
//        r = Water3.execute(Supplier.class, Supplier::get);
//        System.out.println(r);
    }

    public static final Reducer<Object, Boolean> allMatch = new Reducer<Object, Boolean>() {
        @Override
        public boolean willBreak(Object item) {
            return false;
        }

        @Override
        public Boolean reduce(Collection<Object> results) {
            for (Object result : results) {
                Preconditions.checkState(result instanceof Boolean);
                if (!((Boolean) result)) {
                    return false;
                }
            }

            return true;
        }
    };
}
