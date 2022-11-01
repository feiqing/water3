package com.alibaba.water.function.reducer;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * T是扩展点执行的结果类型
 * R是reducer后的结果类型
 *
 * @author qingfei
 * @date 2022/05/19
 */
public abstract class Matcher<T, R> {

    protected Predicate<T> predicate;

    /**
     * 是否中断方法执行
     *
     * @param collection
     * @return
     */
    public abstract Boolean willBreak(T collection);

    /**
     * reduce方法
     *
     * @param collections
     * @return
     */
    public abstract R reduce(Collection<T> collections);

}
