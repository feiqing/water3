package com.alibaba.water3.utils;

import org.springframework.util.AntPathMatcher;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/17 14:34.
 */
public class PatternMatchUtils {

    private static final AntPathMatcher matcher = new AntPathMatcher();

    public static boolean match(String pattern, String path) {
        return matcher.match(pattern, path);
    }
}
