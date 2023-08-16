package com.alibaba.water3.reducer;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author qingfei
 * @date 2022/06/02
 */
public class Reducers {

    /**
     * 返回第一个满足的结果
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Reducer<T, T> firstOf(Predicate<T> predicate) {
        return new FirstOf<>(predicate);
    }

    public static <T> Reducer<T, T> firstOf() {
        return new FirstOf<>();
    }

    /**
     * 满足条件的返回值收集
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Reducer<T, List<T>> collect(Predicate<T> predicate) {
        return new Collector<>(predicate);
    }

    /**
     * 任意一个满足条件
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Reducer<T, Boolean> anyMatch(Predicate<T> predicate) {
        return new AnyMatcher<>(predicate);
    }

    /**
     * 所有条件都满足
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Reducer<T, Boolean> allMatch(Predicate<T> predicate) {
        return new AllMatcher<>(predicate);
    }

}