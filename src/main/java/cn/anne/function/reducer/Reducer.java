package cn.anne.function.reducer;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author qingfei
 * @date 2022/06/02
 */
public class Reducer {

    /**
     * 返回第一个满足的结果
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Matcher<T, T> firstOf(Predicate<T> predicate) {
        return new FirstOf<>(predicate);
    }

    public static <T> Matcher<T, T> firstOf() {
        return new FirstOf<>();
    }

    /**
     * 满足条件的返回值收集
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Matcher<T, List<T>> collect(Predicate<T> predicate) {
        return new Collector<>(predicate);
    }

    /**
     * 任意一个满足条件
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Matcher<T, Boolean> anyMatch(Predicate<T> predicate) {
        return new AnyMatcher<>(predicate);
    }

    /**
     * 所有条件都满足
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Matcher<T, Boolean> allMatch(Predicate<T> predicate) {
        return new AllMatcher<>(predicate);
    }

}
