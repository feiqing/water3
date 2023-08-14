package com.alibaba.water3.reducer;

import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 所有条件都满足
 *
 * @author qingfei
 * @date 2022/05/19
 */
public class AllMatcher<T> extends Reducer<T, Boolean> {

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
