package com.alibaba.water3;

import com.alibaba.water3.reducer.Reducer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qingfei
 * @date 2022/05/07
 */
public class Water3Context {

    private static final ThreadLocal<Map<String, Object>> context = ThreadLocal.withInitial(HashMap::new);

    private static final ThreadLocal<Reducer> reducer = new ThreadLocal<>();

    private static ThreadLocal<String> bizScenario = new ThreadLocal<>();

    private static ThreadLocal<String> subBizScenario = new ThreadLocal<>();

    private static ThreadLocal<String[]> scanPath = new ThreadLocal<>();

    public static void setReducer(Reducer reducer) {
        Water3Context.reducer.set(reducer);
    }

    public static Reducer getReducer() {
        return Water3Context.reducer.get();
    }

    public static void removeReducer() {
        reducer.remove();
    }


    public static void setBizCode(String bizCode) {
        context.get().put("__bizCode__", bizCode);
    }

    public static String getBizCode() {
        return (String) context.get().get("__bizCode__");
    }

    public static void setGroup(String group) {
        context.get().put("__group__", group);
    }

    public static String getGroupName() {
        return (String) context.get().get("__group__");
    }

    public static String getBizScenario() {
        return bizScenario.get();
    }

    public static String getSubBizScenario() {
        return subBizScenario.get();
    }

    public static void setBizScenario(String code) {
        bizScenario.set(code);
    }

    public static void setSubBizScenario(String subCode) {
        subBizScenario.set(subCode);
    }

    public static void setBizScenario(ThreadLocal<String> bizScenario) {
        Water3Context.bizScenario = bizScenario;
    }

    public static void removeBizCode() {
        bizScenario.remove();
    }

    public static <Ext> void addBusinessExt(String namespace, Ext ext) {
        context.get().put("__biz_ext__:" + namespace, ext);
    }

    public static <Ext> void addBusinessExt(Ext ext) {
        addBusinessExt(ext.getClass().getName(), ext);
    }

    public static <Ext> Ext getBusinessExt(String namespace) {
        return (Ext) context.get().get(namespace);
    }

    public static <Ext> Ext getBusinessExt(Class<Ext> ext) {
        return getBusinessExt(ext.getName());
    }


}
