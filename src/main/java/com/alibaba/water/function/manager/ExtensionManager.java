package com.alibaba.water.function.manager;

import java.util.Collections;
import java.util.List;

import com.alibaba.water.annotation.WaterBase;
import com.alibaba.water.annotation.WaterPriority;
import com.alibaba.water.domain.WaterCallBack;
import com.alibaba.water.domain.WaterContext;
import com.alibaba.water.domain.constant.WaterConstants;
import com.alibaba.water.exception.WaterException;
import com.alibaba.water.function.register.WaterTagRegister;
import com.alibaba.water.util.SpringBeanUtils;
import com.alibaba.water.domain.WaterCall;
import com.alibaba.water.function.reducer.Matcher;
import com.alibaba.water.function.register.WaterBaseRegister;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author qingfei
 * @date 2022/06/02
 */
public class ExtensionManager {

    private static final Logger log = LoggerFactory.getLogger(ExtensionManager.class);

    public static  <T, R, I> R doExecute(Class<I> extensionClass, WaterCallBack<I, T> callBack, Matcher<T, R> matcher) {
        List<Class<?>> implClassList = getImplClassSortedList(extensionClass);
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
        List<Class<?>> implClassList = getImplClassSortedList(extensionClass);
        for (Class<?> implClass : implClassList) {
            T t = getImplResult(callBack, implClass);
            if (matcher.willBreak(t)) {
                break;
            }
        }
        return null;
    }

    private static <T, I> T getImplResult(WaterCallBack<I, T> callBack, Class<?> implClass) {
        Object impl = getImpl(implClass);
        return callBack.callBack((I)impl);
    }

    private static <T, I> T getImplResult(WaterCall<I> callBack, Class<?> implClass) {
        Object impl = getImpl(implClass);
        callBack.callBack((I)impl);
        return null;
    }

    private static List<Class<?>> getImplClassSortedList(Class<?> extensionClass) {
        List<Class<?>> implClassList = getImplClassList(extensionClass);
        sortWithPriority(implClassList);
        return implClassList;
    }

    private static List<Class<?>> getImplClassList(Class<?> extensionClass) {
        String bizScenario = WaterContext.getBizScenario();
        List<Class<?>> implClassList = WaterTagRegister.getImplClassListByInterfaceAndTag(extensionClass.getName(), bizScenario);
        if (CollectionUtils.isEmpty(implClassList)) {
            // 找base实现类
            Class<?> baseClass = WaterBaseRegister.getBaseClass(extensionClass.getName());
            if (baseClass != null) {
                return Collections.singletonList(baseClass);
            }
            log.error("no extension implement find, bizScenario:{}, extension:{}", bizScenario, extensionClass.getName());
            throw new WaterException("没找到对应tag的实现类");
        }
        return implClassList;
    }

    private static Object getImpl(Class<?> implClass) {
        if (implClass.isAnnotationPresent(WaterBase.class)) {
            String beanName = getClassBeanName(implClass);
            return SpringBeanUtils.getBean(beanName);
        }
        return SpringBeanUtils.getBean(implClass);
    }

    private static String getClassBeanName(Class<?> implClass) {
        Component component = implClass.getAnnotation(Component.class);
        Service service = implClass.getAnnotation(Service.class);
        Controller controller = implClass.getAnnotation(Controller.class);
        String className = component == null || StringUtils.isEmpty(component.value()) ? null : component.value();
        className = service == null || StringUtils.isEmpty(service.value()) ? className : service.value();
        className = controller == null || StringUtils.isEmpty(controller.value()) ? className : controller.value();
        if (!StringUtils.isEmpty(className)) {
            return className;
        }
        className = implClass.getSimpleName();
        String beanName;
        if(className.length() > 1) {
            beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
        } else {
            beanName = className.toLowerCase();
        }
        return beanName;
    }

    private static void sortWithPriority(List<Class<?>> implClassList) {
        if (implClassList.size() == 1) {
            return;
        }
        boolean hasPriority = false;
        for (Class<?> implClass : implClassList) {
            if (implClass.isAnnotationPresent(WaterPriority.class)) {
                hasPriority = true;
                break;
            }
        }
        if (!hasPriority) {
            return;
        }
        implClassList.sort((a, b) -> {
            int p1 = a.isAnnotationPresent(WaterPriority.class) ? a.getAnnotation(WaterPriority.class).value() :
                WaterConstants.PRIORITY_VALUE_DEFAULT;
            int p2 = b.isAnnotationPresent(WaterPriority.class) ? b.getAnnotation(WaterPriority.class).value() :
                WaterConstants.PRIORITY_VALUE_DEFAULT;
            return p1 - p2;
        });
    }

}
