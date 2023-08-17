package com.alibaba.water3;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qingfei
 * @date 2022/05/07
 */
public class WaterContext {

    private static final String SYS_SCOPE = "__SYS";

    private static final String EXT_SCOPE = "__EXT";

    private static final ThreadLocal<Map<String, Object>> ctx = ThreadLocal.withInitial(HashMap::new);


    // 业务场景 ...
    protected static void setBizScenario(String scenario) {
        setCtxVal(SYS_SCOPE, "__biz_scenario__", scenario);
    }

    public static String getBizScenario() {
        return getCtxVal(SYS_SCOPE, "__biz_scenario__");
    }

    // 业务域 ...
    protected static void setBizDomain(String domain) {
        setCtxVal(SYS_SCOPE, "__biz_domain__", domain);
    }

    public static String getBizDomain() {
        return getCtxVal(SYS_SCOPE, "__biz_domain__");
    }


    // 业务身份 ...
    protected static void setBizId(String bizId) {
        setCtxVal(SYS_SCOPE, "__biz_id__", bizId);
    }

    public static String getBizId() {
        return getCtxVal(SYS_SCOPE, "__biz_id__");
    }

    // 业务扩展 ...
    public static <Ext> void addBusinessExt(String namespace, Ext ext) {
        setCtxVal(EXT_SCOPE, namespace, ext);
    }

    public static <Ext> void addBusinessExt(Ext ext) {
        addBusinessExt(ext.getClass().getName(), ext);
    }

    public static <Ext> Ext getBusinessExt(String namespace) {
        return getCtxVal(EXT_SCOPE, namespace);
    }

    public static <Ext> Ext getBusinessExt(Class<Ext> ext) {
        return getBusinessExt(ext.getName());
    }

    // helper ...
    private static void setCtxVal(String scope, String key, Object val) {
        ctx.get().put(scope + ":" + key, val);
    }

    private static <T> T getCtxVal(String scope, String key) {
        return (T) ctx.get().get(scope + ":" + key);
    }
}
