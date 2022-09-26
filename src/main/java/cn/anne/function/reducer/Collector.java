package cn.anne.function.reducer;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

/**
 * 满足条件的返回值收集
 *
 * @author qingfei
 * @date 2022/05/19
 */
public class Collector<T> extends Matcher<T, List<T>> {

    public Collector() {
    }

    public Collector(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);
        super.predicate = predicate;
    }

    @Override
    public Boolean willBreak(T collection) {
        return false;
    }

    @Override
    public List<T> reduce(Collection<T> collections) {
        if (CollectionUtils.isEmpty(collections)) {
            return Lists.newArrayListWithCapacity(0);
        }
        List<T> list = Lists.newArrayListWithCapacity(16);
        for (T collection : collections) {
            if (predicate == null || predicate.test(collection)) {
                list.add(collection);
            }
        }
        return list;
    }
}
