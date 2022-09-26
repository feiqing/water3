package cn.anne.function;

import java.lang.reflect.Method;
import java.util.Set;

import cn.anne.domain.WaterContext;
import cn.anne.domain.WaterParamRequest;
import cn.anne.domain.WaterScenarioParser;
import cn.anne.exception.WaterException;
import cn.anne.util.ClassScanUtils;
import org.springframework.util.StringUtils;

/**
 * @author qingfei
 * @date 2022/05/07
 */
public class WaterParser {

    public static volatile WaterParser parser;

    private WaterParser() {

    }

    public static WaterParser getInstance() {
        if (parser == null) {
            synchronized (WaterParser.class) {
                if (parser == null) {
                    parser = new WaterParser();
                }
            }
        }
        return parser;
    }

    public String parserBizCode(Class<? extends WaterScenarioParser<? extends WaterParamRequest>> bizParserClass,
        WaterParamRequest waterParamRequest) {
        String bizCode = null;
        try {
            Object obj = bizParserClass.newInstance();
            Method parser = bizParserClass.getDeclaredMethod("parser", WaterParamRequest.class);
            bizCode = (String)parser.invoke(obj, waterParamRequest);
            if (StringUtils.isEmpty(bizCode)) {
                throw new WaterException("biz scenario is not exist");
            }
        } catch (Exception e) {
            throw new WaterException(e.getMessage());
        }
        return bizCode;
    }

    public String parserBizCode(String packagePath, WaterParamRequest waterParamRequest) {
        // 扫描所有实现了WaterScenarioParser接口的类
        Set<Class<?>> parserClassSet = ClassScanUtils.getSubTypeOf(packagePath, WaterScenarioParser.class);
        String bizCode = null;
        // 优先级firstOf
        for (Class<?> bizParserClass : parserClassSet) {
            try {
                Object obj = bizParserClass.newInstance();
                Method parser = bizParserClass.getDeclaredMethod("parser", WaterParamRequest.class);
                bizCode = (String)parser.invoke(obj, waterParamRequest);
                if (!StringUtils.isEmpty(bizCode)) {
                    WaterContext.setBizScenario(bizCode);
                    break;
                }
            } catch (Exception e) {
                throw new WaterException("解析bizCode出错");
            }
        }
        return bizCode;
    }

    public void removeBizCode() {
        WaterContext.removeBizCode();
    }



}
