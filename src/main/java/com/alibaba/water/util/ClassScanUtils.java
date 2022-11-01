package com.alibaba.water.util;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

/**
 * @author qingfei
 * @date 2022/05/01
 */
public class ClassScanUtils {

    /**
     * 获取注解上的所有类（包含了该类的子类）
     *
     * @param packagePath
     * @param annClass
     * @return
     */
    public static Set<Class<?>> getSubTypeAnnotation(String packagePath, Class<? extends Annotation> annClass) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(packagePath)
                .addScanners(new TypeAnnotationsScanner()));
        return reflections.getTypesAnnotatedWith(annClass);
    }

    public static Set<Class<?>> getTypeAnnotation(String packagePath, Class<? extends Annotation> annClass) {
        Set<Class<?>> subTypeAnnotation = getSubTypeAnnotation(packagePath, annClass);
        return subTypeAnnotation.stream().filter(clazz -> clazz.isAnnotationPresent(annClass)).collect(Collectors.toSet());
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
