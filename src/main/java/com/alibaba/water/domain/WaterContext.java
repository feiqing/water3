package com.alibaba.water.domain;

/**
 * @author qingfei
 * @date 2022/05/07
 */
public class WaterContext {

    private static ThreadLocal<String> bizScenario = new ThreadLocal<>();

    private static ThreadLocal<String> subBizScenario = new ThreadLocal<>();

    private static ThreadLocal<String[]> scanPath = new ThreadLocal<>();

    public static String getBizScenario() {
        return bizScenario.get();
    }

    public static String getSubBizScenario(){
        return subBizScenario.get();
    }

    public static String[] getPath() {
        return scanPath.get();
    }

    public static void setBizScenario(String code) {
        bizScenario.set(code);
    }

    public static void setSubBizScenario(String subCode) {
        subBizScenario.set(subCode);
    }

    public static void setBizScenario(ThreadLocal<String> bizScenario) {
        WaterContext.bizScenario = bizScenario;
    }

    public static ThreadLocal<String[]> getScanPath() {
        return scanPath;
    }

    public static void setScanPath(String[] path) {
        scanPath.set(path);
    }

    public static void setScanPath(ThreadLocal<String[]> scanPath) {
        WaterContext.scanPath = scanPath;
    }

    public static void removeBizCode() {
        bizScenario.remove();
    }

    public static void removePath() {
        scanPath.remove();
    }
}
