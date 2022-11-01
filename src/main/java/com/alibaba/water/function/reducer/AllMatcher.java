package com.alibaba.water.function.reducer;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

import org.springframework.util.CollectionUtils;

/**
 * 所有条件都满足
 *
 * @author qingfei
 * @date 2022/05/19
 */
public class AllMatcher<T> extends Matcher<T, Boolean>{

    public AllMatcher() {
    }

    public AllMatcher(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);
        super.predicate = predicate;
    }

    @Override
    public Boolean willBreak(T collection) {
        return predicate != null && !predicate.test(collection);
    }

    @Override
    public Boolean reduce(Collection<T> collections) {
        if (CollectionUtils.isEmpty(collections)) {
            return false;
        }
        for (T collection : collections) {
            if (predicate != null && !predicate.test(collection)) {
                return false;
            }
        }
        return true;
    }
}
