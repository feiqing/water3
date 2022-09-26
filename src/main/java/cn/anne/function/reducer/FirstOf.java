package cn.anne.function.reducer;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 返回满足条件优先级最高的结果
 *
 * @author qingfei
 * @date 2022/05/19
 */
public class FirstOf<T> extends Matcher<T, T> {

    public FirstOf() {
    }

    public FirstOf(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);
        super.predicate = predicate;
    }

    @Override
    public Boolean willBreak(T collection) {
        return predicate == null || predicate.test(collection);
    }

    @Override
    public T reduce(Collection<T> collections) {
        if (collections.isEmpty()) {
            return null;
        }
        for (T collection : collections) {
            if (predicate == null || predicate.test(collection)) {
                return collection;
            }
        }
        return null;
    }
}
