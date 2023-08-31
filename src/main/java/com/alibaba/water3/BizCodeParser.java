package com.alibaba.water3;

import com.alibaba.water3.domain.Tag;

/**
 * @author qingfei
 * @date 2022/05/02
 */
@FunctionalInterface
public interface BizCodeParser<Param> {

    default String parseBizDomain(Param param) {
        return Tag.DOMAIN_BASE;
    }

    String parseBizCode(Param param);
}
