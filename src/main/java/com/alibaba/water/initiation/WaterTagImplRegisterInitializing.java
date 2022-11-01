package com.alibaba.water.initiation;

import java.util.Properties;
import java.util.Set;

import com.alibaba.water.annotation.WaterBase;
import com.alibaba.water.annotation.WaterInterface;
import com.alibaba.water.annotation.WaterScenario;
import com.alibaba.water.domain.WaterContext;
import com.alibaba.water.function.register.WaterBaseRegister;
import com.alibaba.water.function.register.WaterTagRegister;
import com.alibaba.water.util.ClassScanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author qingfei
 * @date 2022/05/07
 */
@Component
public class WaterTagImplRegisterInitializing implements InitializingBean {

    private static final String SCAN_PATH = "water.scanPath";

    @Override
    public void afterPropertiesSet() throws Exception {
        Properties properties = PropertiesLoaderUtils.loadAllProperties("application.properties");
        String scanPath = (String)properties.get(SCAN_PATH);
        WaterContext.setScanPath(scanPath);
        Set<Class<?>> baseClassSet = ClassScanUtils.getTypeAnnotation(scanPath, WaterBase.class);
        Set<Class<?>> interfaceClassSet = ClassScanUtils.getTypeAnnotation(scanPath, WaterInterface.class);
        Set<Class<?>> tagImplClassSet = ClassScanUtils.getTypeAnnotation(scanPath, WaterScenario.class);
        for (Class<?> interfaceClass : interfaceClassSet) {
            if (!interfaceClass.isInterface()) {
                continue;
            }
            registerWaterInterfaceTagImpl(interfaceClass, tagImplClassSet);
            registerWaterBaseImpl(interfaceClass, baseClassSet);
        }
    }

    private void registerWaterInterfaceTagImpl(Class<?> interfaceClass, Set<Class<?>> tagImplClassSet) {
        for (Class<?> tagImplClass : tagImplClassSet) {
            if (!tagImplClass.isInterface() && interfaceClass.isAssignableFrom(tagImplClass)) {
                WaterScenario annotation = tagImplClass.getAnnotation(WaterScenario.class);
                String[] scenarioList = annotation.scenario();
                for (String scenario : scenarioList) {
                    if (StringUtils.isEmpty(scenario)) {
                        continue;
                    }
                    WaterTagRegister.register(interfaceClass.getName(), scenario, tagImplClass);
                }
            }
        }
    }

    private void registerWaterBaseImpl(Class<?> interfaceClass, Set<Class<?>> baseClassSet) {
        for (Class<?> baseClass : baseClassSet) {
            if (interfaceClass.isAssignableFrom(baseClass)) {
                WaterBaseRegister.register(interfaceClass.getName(), baseClass);
            }
        }
    }
}
