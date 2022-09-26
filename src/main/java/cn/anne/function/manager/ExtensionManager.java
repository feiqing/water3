package cn.anne.function.manager;

import java.util.List;

import cn.anne.domain.WaterCall;
import cn.anne.domain.WaterCallBack;
import cn.anne.domain.WaterContext;
import cn.anne.exception.WaterException;
import cn.anne.function.reducer.Matcher;
import cn.anne.function.register.WaterTagRegister;
import cn.anne.util.SpringBeanUtils;
import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

/**
 * @author qingfei
 * @date 2022/06/02
 */
public class ExtensionManager {

    public static  <T, R, I> R doExecute(Class<I> extensionClass, WaterCallBack<I, T> callBack, Matcher<T, R> matcher) {
        List<Class<?>> implClassList = getImplClassList(extensionClass);
        List<T> resultList = Lists.newArrayListWithCapacity(8);
        for (Class<?> implClass : implClassList) {
            T t = getImplResult(callBack, implClass);
            resultList.add(t);
            if (matcher.willBreak(t)) {
                break;
            }
        }
        R r = null;
        if (!CollectionUtils.isEmpty(resultList)) {
            r = matcher.reduce(resultList);
        }
        return r;
    }

    public static <T, R, I> Void doExecuteVoidReturnType(Class<I> extensionClass, WaterCall<I> callBack, Matcher<T, R> matcher) {
        List<Class<?>> implClassList = getImplClassList(extensionClass);
        for (Class<?> implClass : implClassList) {
            T t = getImplResult(callBack, implClass);
            if (matcher.willBreak(t)) {
                break;
            }
        }
        return null;
    }

    private static List<Class<?>> getImplClassList(Class<?> extensionClass) {
        String bizScenario = WaterContext.getBizScenario();
        List<Class<?>> implClassList = WaterTagRegister.getImplClassListByInterfaceAndTag(extensionClass.getName(), bizScenario);
        if (CollectionUtils.isEmpty(implClassList)) {
            throw new WaterException("没找到对应tag的实现类");
        }
        return implClassList;
    }

    private static <T, I> T getImplResult(WaterCallBack<I, T> callBack, Class<?> implClass) {
        Object impl = SpringBeanUtils.getBean(implClass);
        return callBack.callBack((I)impl);
    }

    private static <T, I> T getImplResult(WaterCall<I> callBack, Class<?> implClass) {
        Object impl = SpringBeanUtils.getBean(implClass);
        callBack.callBack((I)impl);
        return null;
    }
}
