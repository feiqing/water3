package com.alibaba.water3.plugin;

import com.google.common.base.Preconditions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/12 11:28.
 */
public class PluginManager {

    public static void main(String[] args) {
        ServiceLoader<Water3Plugin> loader = ServiceLoader.load(Water3Plugin.class, Water3Plugin.class.getClassLoader());
        List<Water3Plugin> plugins = new ArrayList<>();
        for (Iterator<Water3Plugin> iterator = loader.iterator(); iterator.hasNext(); ) {
            plugins.add(iterator.next());
        }

        Preconditions.checkState(!plugins.isEmpty());

        Water3Plugin next = plugins.get(plugins.size() - 1);

        for (int i = plugins.size() - 2; i >= 0; i--) {
            PluginInvocation invocation = new PluginInvocation() {

                @Override
                public Object[] getArgs() {
                    return new Object[0];
                }

                @Override
                public Method getMethod() {
                    return null;
                }

                @Override
                public Object getTarget() {
                    return null;
                }

                @Override
                public Object processed() {
                    return null;
                }
            };

            Water3Plugin plugin = plugins.get(i);
        }
        // todo


    }
}
