package com.alibaba.water3;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qingfei
 * @date 2022/05/07
 */
@SuppressWarnings("unchecked")
public class BizContext {

    private static final String SYS_SCOPE = "__SYS";

    private static final String EXT_SCOPE = "__EXT";

    private static final ThreadLocal<Map<String, Object>> ctx = ThreadLocal.withInitial(HashMap::new);


    // 类型 ...
    protected static void setType(String type) {
        setCtxVal(SYS_SCOPE, "__type__", type);
    }

    public static String getType() {
        return getCtxVal(SYS_SCOPE, "__type__");
    }


    // 业务身份 ...
    protected static void setBizCode(String bizCode) {
        setCtxVal(SYS_SCOPE, "__biz_code__", bizCode);
    }

    public static String getBizCode() {
        return getCtxVal(SYS_SCOPE, "__biz_code__");
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
