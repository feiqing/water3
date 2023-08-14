package com.alibaba.water3.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 21:07.
 */
public class SystemUtils {

    // todo: 优化取名
    public static boolean isGlobalCloseLazyLoading() {
        if (StringUtils.equalsIgnoreCase(System.getProperty("spring.profiles.active"), "production")) {
            return true;
        }
        if (StringUtils.equalsIgnoreCase(System.getProperty("water3.close.lazy.loading"), "true")) {
            return true;
        }

        return false;
    }

    public static boolean isGlobalOpenLazyLoading() {
        if (StringUtils.equalsIgnoreCase(System.getProperty("spring.profiles.active"), "production")) {
            return false;
        }
        if (StringUtils.equalsIgnoreCase(System.getProperty("water3.open.lazy.loading"), "true")) {
            return true;
        }

        return false;
    }
}
