package com.alibaba.water3;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:31.
 */
@SuppressWarnings("unchecked")
public class BizContext {

    private static final String SYS_SCOPE = "__SYS";

    private static final String EXT_SCOPE = "__EXT";

    private static final ThreadLocal<Map<String, Object>> ctx = ThreadLocal.withInitial(HashMap::new);


    // 业务路由 ...
    protected static void setBizRouter(BizRouter bizRouter) {
        setCtxVal(SYS_SCOPE, "__biz_router__", bizRouter);
    }

    public static BizRouter getBizRouter() {
        return getCtxVal(SYS_SCOPE, "__biz_router__");
    }

    public static void removeBizRouter() {
        ctx.get().remove(SYS_SCOPE + ":" + "__biz_router__");
    }

    // 业务身份 ...
    protected static void setBizCode(String bizCode) {
        setCtxVal(SYS_SCOPE, "__biz_code__", bizCode);
    }

    public static String getBizCode() {
        return getCtxVal(SYS_SCOPE, "__biz_code__");
    }

    public static void removeBizCode() {
        ctx.get().remove(SYS_SCOPE + ":" + "__biz_code__");
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

    public static <Ext> void removeBusinessExt(String namespace) {
        ctx.get().remove(EXT_SCOPE + ":" + namespace);
    }

    public static <Ext> void removeBusinessExt(Ext ext) {
        removeBusinessExt(ext.getClass().getName());
    }

    public static void clear() {
        ctx.remove();
    }

    // helper ...
    private static void setCtxVal(String scope, String key, Object val) {
        ctx.get().put(scope + ":" + key, val);
    }

    private static <T> T getCtxVal(String scope, String key) {
        return (T) ctx.get().get(scope + ":" + key);
    }
}
