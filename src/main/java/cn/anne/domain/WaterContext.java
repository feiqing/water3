package cn.anne.domain;

/**
 * @author qingfei
 * @date 2022/05/07
 */
public class WaterContext {

    private static ThreadLocal<String> bizScenario = new ThreadLocal<>();

    public static String getBizScenario() {
        return bizScenario.get();
    }

    public static void setBizScenario(String code) {
        bizScenario.set(code);
    }

    public static void removeBizCode() {
        bizScenario.remove();
    }
}
