package cn.anne.initiation;

import java.util.Properties;
import java.util.Set;

import cn.anne.annotation.WaterInterface;
import cn.anne.annotation.WaterScenario;
import cn.anne.function.register.WaterTagRegister;
import cn.anne.util.ClassScanUtils;
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
        Set<Class<?>> interfaceClassSet = ClassScanUtils.getTypeAnnotation(scanPath, WaterInterface.class);
        Set<Class<?>> tagImplClassSet = ClassScanUtils.getTypeAnnotation(scanPath, WaterScenario.class);
        for (Class<?> interfaceClass : interfaceClassSet) {
            if (!interfaceClass.isInterface()) {
                continue;
            }
            registerWaterInterfaceTagImpl(interfaceClass, tagImplClassSet);
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
}
