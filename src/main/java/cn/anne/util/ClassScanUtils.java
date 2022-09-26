package cn.anne.util;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

/**
 * @author qingfei
 * @date 2022/5/1
 */
public class ClassScanUtils {

    /**
     * 获取注解上的所有类（包含了该类的子类）
     *
     * @param packagePath
     * @param annClass
     * @return
     */
    public static Set<Class<?>> getTypeAnnotation(String packagePath, Class<? extends Annotation> annClass) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(packagePath)
                .addScanners(new TypeAnnotationsScanner()));
        return reflections.getTypesAnnotatedWith(annClass);
    }

    /**
     * 获取该类/接口的所有子类
     *
     * @param packagePath
     * @param superClass
     * @return
     */
    public static Set<Class<?>> getSubTypeOf(String packagePath, Class superClass) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(packagePath)
                .addScanners(new SubTypesScanner()));
        return reflections.getSubTypesOf(superClass);
    }

}
