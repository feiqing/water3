package cn.anne.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qingfei
 * @date 2022/05/16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WaterExtension {

    String desc() default "";

//    Reducer reducer() default Reducer.FIRST_OF;

}
