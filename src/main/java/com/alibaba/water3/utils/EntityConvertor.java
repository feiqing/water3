package com.alibaba.water3.utils;

import com.alibaba.water3.domain.Entity;
import com.alibaba.water3.domain.Tag;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.factory.HsfServiceFactory;
import com.alibaba.water3.factory.SpringBeanFactory;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/16 20:45.
 */
public class EntityConvertor {

    public static Map<String, Entity.BusinessScenario> toScenarioMap(Collection<Tag.BusinessScenario> tags) throws Exception {
        Map<String, Entity.BusinessScenario> scenarioMap = new HashMap<>(tags.size());
        for (Tag.BusinessScenario tag : tags) {
            scenarioMap.put(tag.scenario, new Entity.BusinessScenario(tag.scenario, toAbilityMap(tag.extensionAbilityList)));
        }
        return scenarioMap;
    }

    private static Map<Class<?>, Entity.ExtensionAbility> toAbilityMap(List<Tag.ExtensionAbility> tags) throws Exception {
        Map<Class<?>, Entity.ExtensionAbility> abilityMap = new HashMap<>(tags.size());
        for (Tag.ExtensionAbility tag : tags) {
            Object baseImpl = SpringBeanFactory.getSpringBean(new Tag.Bean(tag.base));
            abilityMap.put(Class.forName(tag.clazz), new Entity.ExtensionAbility(tag.clazz, baseImpl, toPointMap(tag.extensionPointList)));
        }
        return abilityMap;
    }

    private static Map<String, Entity.ExtensionPoint> toPointMap(List<Tag.ExtensionPoint> tags) throws Exception {
        Map<String, Entity.ExtensionPoint> pointMap = new HashMap<>(tags.size());
        for (Tag.ExtensionPoint tag : tags) {

            List<Tag.Business> baseDomainBusinesses = new LinkedList<>();
            List<Tag.Business> extDomainBusinesses = new LinkedList<>();
            for (Tag.Business business : tag.businesList) {
                if (StringUtils.equals(business.domain, Tag.DOMAIN_BASE)) {
                    baseDomainBusinesses.add(business);
                } else {
                    extDomainBusinesses.add(business);
                }
            }

            pointMap.put(tag.method, new Entity.ExtensionPoint(tag.method, toBaseDomainBusinessMap(baseDomainBusinesses),
                    toExtDomainBusinessMap(extDomainBusinesses)));
        }
        return pointMap;
    }

    private static Map<String, List<Entity.Business>> toBaseDomainBusinessMap(List<Tag.Business> tags) throws Exception {
        // tips: 此处要将code分割后重新排列
        Map<String, List<Tag.Business>> code2tags = new HashMap<>();
        for (Tag.Business tag : tags) {
            for (String code : Splitter.on(",").trimResults().omitEmptyStrings().split(tag.code)) {
                code2tags.computeIfAbsent(code, _k -> new ArrayList<>()).add(tag);
            }
        }

        Map<String, List<Entity.Business>> code2businessMap = new HashMap<>(code2tags.size());
        for (Map.Entry<String, List<Tag.Business>> entry : code2tags.entrySet()) {
            // tips: 基于优先级重新排序
            entry.getValue().sort(Comparator.comparing(bizTag -> bizTag.priority));

            List<Entity.Business> businessList = new ArrayList<>(entry.getValue().size());
            for (Tag.Business tag : entry.getValue()) {
                businessList.add(toBusinessEntity(tag.domain, tag.code, tag));
            }

            code2businessMap.put(entry.getKey(), businessList);
        }

        return code2businessMap;
    }

    private static Map<String, List<Entity.Business>> toExtDomainBusinessMap(List<Tag.Business> tags) throws Exception {
        Map<String, List<Entity.Business>> domain2businessMap = new HashMap<>(tags.size());
        for (Tag.Business tag : tags) {
            domain2businessMap.computeIfAbsent(tag.domain, _K -> new LinkedList<>()).add(toBusinessEntity(tag.domain, tag.code, tag));

        }
        return domain2businessMap;
    }

    private static Entity.Business toBusinessEntity(String domain, String code, Tag.Business tag) throws Exception {
        if (tag.bean != null) {
            return Entity.Business.newBeanInstance(domain, code, tag.bean, getSpringBean(tag.bean));
        }

        if (tag.hsf != null) {
            return Entity.Business.newHsfInstance(domain, code, tag.hsf, getHsfService(tag.hsf));
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
