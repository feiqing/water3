package com.alibaba.water3;

import com.alibaba.water3.domain.SpiImpls;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/11/1 16:47.
 */
@FunctionalInterface
public interface ExtensionMethodsSupplier {

    Collection<Method> get(SpiImpls.SpiImpl impl);

}
