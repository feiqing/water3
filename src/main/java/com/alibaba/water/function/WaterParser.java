package com.alibaba.water.function;

import com.alibaba.water.domain.WaterContext;
import com.alibaba.water.domain.WaterParamRequest;
import com.alibaba.water.domain.WaterScenarioParser;
import com.alibaba.water.exception.WaterException;
import com.alibaba.water.util.ClassScanUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Set;

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

    public String parserBizCode(WaterParamRequest waterParamRequest) {
        String[] paths = WaterContext.getPath();
        return parserBizCode(paths, waterParamRequest);
    }

    public String parserBizCode(String[] packagePaths, WaterParamRequest waterParamRequest) {
        // 扫描所有实现了WaterScenarioParser接口的类
        Set<Class<?>> parserClassSet = ClassScanUtils.getSubTypeOf(packagePaths, WaterScenarioParser.class);
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
            WaterContext.setBizScenario(bizCode);

            Method parserSub = bizParserClass.getMethod("parserSubScenario", WaterParamRequest.class);
            String subScenario = null;
            Object invoke = parserSub.invoke(obj, waterParamRequest);
            if (invoke != null) {
                subScenario = (String) invoke;
            }
            WaterContext.setSubBizScenario(subScenario);
        } catch (Exception e) {
            throw new WaterException(e.getMessage(), e.getCause());
        }
        return bizCode;
    }

    public void removeBizCode() {
        WaterContext.removeBizCode();
    }



}
