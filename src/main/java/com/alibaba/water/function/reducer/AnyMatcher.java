package com.alibaba.water.function.reducer;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

import org.springframework.util.CollectionUtils;

/**
 * 任意一个满足条件
 *
 * @author qingfei
 * @date 2022/05/19
 */
public class AnyMatcher<T> extends Matcher<T, Boolean>{

    public AnyMatcher() {
    }

    public AnyMatcher(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);
        super.predicate = predicate;
    }

    @Override
    public Boolean willBreak(T collection) {
        return false;
    }

    @Override
    public Boolean reduce(Collection<T> collections) {
        if (CollectionUtils.isEmpty(collections)) {
            return false;
        }
        for (T collection : collections) {
            if (predicate == null || predicate.test(collection)) {
                return true;
            }
        }
        return false;
    }
}
