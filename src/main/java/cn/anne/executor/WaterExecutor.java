package cn.anne.executor;

import javax.annotation.Resource;

import cn.anne.domain.WaterCall;
import cn.anne.domain.WaterCallBack;
import cn.anne.function.manager.ExtensionManager;
import cn.anne.function.reducer.Matcher;
import cn.anne.function.reducer.Reducer;

/**
 * @author qingfei
 * @date 2022/06/02
 */
public class WaterExecutor {

    public static volatile WaterExecutor executor;

    @Resource
    private ExtensionManager extensionManager;

    private WaterExecutor() {}

    public static WaterExecutor getInstance() {
        if (executor == null) {
            synchronized (WaterExecutor.class) {
                if (executor == null) {
                    executor = new WaterExecutor();
                }
            }
        }
        return executor;
    }

    public <T, I> T execute(Class<I> extensionClass, WaterCallBack<I, T> callBack) {
        return execute(extensionClass, callBack, Reducer.firstOf());
    }

    public <T, R, I> R execute(Class<I> extensionClass, WaterCallBack<I, T> callBack, Matcher<T, R> matcher) {
        return ExtensionManager.doExecute(extensionClass, callBack, matcher);
    }

    public <T, R, I> Void executeVoidReturnType(Class<I> extensionClass, WaterCall<I> callBack) {
        return ExtensionManager.doExecuteVoidReturnType(extensionClass, callBack, Reducer.firstOf());
    }

    public <T, R, I> Void executeVoidReturnType(Class<I> extensionClass, WaterCall<I> callBack, Matcher<T, R> matcher) {
        return ExtensionManager.doExecuteVoidReturnType(extensionClass, callBack, matcher);
    }

}
