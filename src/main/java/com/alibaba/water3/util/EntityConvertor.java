package com.alibaba.water3.util;

import com.alibaba.water3.domain.Entity;
import com.alibaba.water3.domain.Tag;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.factory.HsfServiceFactory;
import com.alibaba.water3.factory.SpringBeanFactory;
import com.google.common.base.Splitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/16 20:45.
 */
public class EntityConvertor {

    public static ConcurrentMap<String, Entity.BusinessScenario> toScenarioMap(Collection<Tag.BusinessScenario> tags) throws Exception {
        ConcurrentMap<String, Entity.BusinessScenario> scenarioMap = new ConcurrentHashMap<>(tags.size());
        for (Tag.BusinessScenario tag : tags) {
            scenarioMap.put(tag.scenario, new Entity.BusinessScenario(tag.scenario, toAbilityMap(tag.extensionAbilityList)));
        }
        return scenarioMap;
    }

    private static ConcurrentMap<Class<?>, Entity.ExtensionAbility> toAbilityMap(List<Tag.ExtensionAbility> tags) throws Exception {
        ConcurrentMap<Class<?>, Entity.ExtensionAbility> abilityMap = new ConcurrentHashMap<>(tags.size());
        for (Tag.ExtensionAbility tag : tags) {
            Object baseImpl = SpringBeanFactory.getSpringBean(new Tag.Bean(tag.base));
            abilityMap.put(Class.forName(tag.clazz), new Entity.ExtensionAbility(tag.clazz, baseImpl, toPointMap(tag.extensionPointList)));
        }
        return abilityMap;
    }

    private static ConcurrentMap<String, Entity.ExtensionPoint> toPointMap(List<Tag.ExtensionPoint> tags) throws Exception {
        ConcurrentMap<String, Entity.ExtensionPoint> pointMap = new ConcurrentHashMap<>(tags.size());
        for (Tag.ExtensionPoint tag : tags) {
            pointMap.put(tag.method, new Entity.ExtensionPoint(tag.method, toBusinessMap(tag.businesList)));
        }
        return pointMap;
    }

    private static ConcurrentMap<String, List<Entity.Business>> toBusinessMap(List<Tag.Business> tags) throws Exception {

        // tips: 此处要将id分割后重新排列
        Map<String, List<Tag.Business>> id2tags = new HashMap<>();
        for (Tag.Business tag : tags) {
            for (String code : Splitter.on(",").trimResults().omitEmptyStrings().split(tag.id)) {
                id2tags.computeIfAbsent(code, _k -> new ArrayList<>()).add(tag);
            }
        }

        ConcurrentMap<String, List<Entity.Business>> code2businessMap = new ConcurrentHashMap<>(id2tags.size());
        for (Map.Entry<String, List<Tag.Business>> entry : id2tags.entrySet()) {
            String id = entry.getKey();
            List<Tag.Business> _tags = entry.getValue();
            // tips: 基于优先级重新排序
            _tags.sort(Comparator.comparing(bizTag -> bizTag.priority));

            List<Entity.Business> businessList = new ArrayList<>(_tags.size());
            for (Tag.Business tag : _tags) {
                businessList.add(toBusinessEntity(id, tag));
            }

            code2businessMap.put(id, businessList);
        }

        return code2businessMap;
    }

    private static Entity.Business toBusinessEntity(String code, Tag.Business tag) throws Exception {
        if (tag.bean != null) {
            return Entity.Business.newBeanInstance(code, tag.bean, getSpringBean(tag.bean));
        }

        if (tag.hsf != null) {
            return Entity.Business.newHsfInstance(code, tag.hsf, getHsfService(tag.hsf));
        }

        throw new WaterException(String.format("BusinessExt:[%s] <bean/> and <hsf/> definition all empty.", code));
    }

    private static Object getSpringBean(Tag.Bean bean) {
        if (SystemUtils.isGlobalCloseLazyLoading()) {
            return SpringBeanFactory.getSpringBean(bean);
        }

        if (SystemUtils.isGlobalOpenLazyLoading()) {
            return null;
        }

        if (!bean.lazy) {
            return SpringBeanFactory.getSpringBean(bean);
        }

        return null;
    }

    private static Object getHsfService(Tag.Hsf hsf) throws Exception {
        if (SystemUtils.isGlobalCloseLazyLoading()) {
            return HsfServiceFactory.getHsfService(hsf);
        }

        if (SystemUtils.isGlobalOpenLazyLoading()) {
            return null;
        }

        if (!hsf.lazy) {
            return HsfServiceFactory.getHsfService(hsf);
        }

        return null;
    }
}
