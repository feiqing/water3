package com.alibaba.water3.core;

import com.alibaba.water3.Water3Context;
import com.alibaba.water3.domain.Entity;
import com.alibaba.water3.domain.Tag;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.factory.HsfServiceFactory;
import com.alibaba.water3.factory.SpringBeanFactory;
import com.alibaba.water3.util.DomParser;
import com.alibaba.water3.util.SystemUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 17:52.
 */
public class Water3Register {

    private static final String WATER_XML_CONFIG_LOCATION = "classpath*:water3*.xml";

    // group -> ability(class) -> point(method) -> business -> impl
    private static final ConcurrentMap<String, Entity.ExtensionGroup> name2groupMap = new ConcurrentHashMap<>();

    public static <SPI> List<SPI> getSpiImpls(Class<SPI> extensionAbility, String extensionPointMethod) {
        String groupName = Water3Context.getGroupName();
        // todo @翡青 异常信息
        Preconditions.checkState(!Strings.isNullOrEmpty(groupName));

        String bizCode = Water3Context.getBizCode();
        // todo @翡青 异常信息
        Preconditions.checkState(!Strings.isNullOrEmpty(bizCode));

        Entity.ExtensionGroup group = name2groupMap.get(groupName);
        if (group == null) {
            throw new WaterException(String.format("ExtensionGroup:[%s] not found.", groupName));
        }

        Entity.ExtensionAbility ability = group.class2abilityMap.get(extensionAbility);
        if (ability == null) {
            throw new WaterException(String.format("ExtensionAbility:[%s#%s] not found.", group.name, extensionAbility.getName()));
        }

        Entity.ExtensionPoint point = ability.method2pointMap.get(extensionPointMethod);
        if (point == null) {
            throw new WaterException(String.format("ExtensionPoint:[%s#%s#%s] not found.", group.name, ability.clazz, extensionPointMethod));
        }

        return (List<SPI>) point.code2implCache.computeIfAbsent(bizCode, _K -> {
            List<Entity.Business> business = point.code2businessMap.get(bizCode);
            if (CollectionUtils.isEmpty(business)) {
                return Collections.singletonList(ability.baseImpl);
            } else {
                return business.stream().map(Water3Register::makeImpl).collect(Collectors.toList());
            }
        });
    }

    private static Object makeImpl(Entity.Business entity) {
        if (entity.impl != null) {
            return entity.impl;
        }

        // tips: 懒加载的具体实现
        if (entity.hsf != null) {
            if (!entity.hsf.lazy) {
                // todo: 不是懒加载, 肯定有问题
                throw new WaterException("不是懒加载");
            }

            try {
                entity.impl = HsfServiceFactory.getHsfService(entity.hsf);
            } catch (Exception e) {
                throw new WaterException(e);
            }
        }
        if (entity.bean != null) {
            if (!entity.bean.lazy) {
                // todo: 不是懒加载, 肯定有问题
                throw new WaterException("不是懒加载");
            }

            entity.impl = SpringBeanFactory.getSpringBean(entity.bean);
        }

        Preconditions.checkState(entity.impl != null);

        return entity.impl;
    }


    public static void register(String configStyle) throws Exception {
        List<Tag.ExtensionGroup> extensionGroups = Collections.emptyList();
        if (StringUtils.equalsIgnoreCase(configStyle, "XML")) {
            extensionGroups = DomParser.loadExtensionGroups(WATER_XML_CONFIG_LOCATION);
        } else {
            // tips: 未来扩展更多的配置方式 ... (yaml? json? groovy? java?)
        }

        for (Tag.ExtensionGroup extensionGroup : extensionGroups) {
            String group = extensionGroup.name;
            Entity.ExtensionGroup entity = new Entity.ExtensionGroup(group, registerClass2abilityMap(extensionGroup.extensionAbilityList));
            name2groupMap.put(group, entity);
        }
    }

    private static ConcurrentMap<Class<?>, Entity.ExtensionAbility> registerClass2abilityMap(List<Tag.ExtensionAbility> extensionAbilityList) throws Exception {
        ConcurrentMap<Class<?>, Entity.ExtensionAbility> class2abilityMap = new ConcurrentHashMap<>(extensionAbilityList.size());
        for (Tag.ExtensionAbility tag : extensionAbilityList) {

            Object baseImpl = SpringBeanFactory.getSpringBean(new Tag.Bean(tag.base));
            Entity.ExtensionAbility entity = new Entity.ExtensionAbility(tag.clazz, baseImpl, registerMethod2pointMap(tag.extensionPointList));

            class2abilityMap.put(Class.forName(tag.clazz), entity);
        }
        return class2abilityMap;
    }

    private static ConcurrentMap<String, Entity.ExtensionPoint> registerMethod2pointMap(List<Tag.ExtensionPoint> tags) throws Exception {
        ConcurrentMap<String, Entity.ExtensionPoint> method2pointMap = new ConcurrentHashMap<>(tags.size());
        for (Tag.ExtensionPoint tag : tags) {
            method2pointMap.put(tag.method, new Entity.ExtensionPoint(tag.method, registerCode2businessMap(tag.businesList)));
        }
        return method2pointMap;
    }

    private static ConcurrentMap<String, List<Entity.Business>> registerCode2businessMap(List<Tag.Business> tags) throws Exception {

        // tips: 此处要将code分割后重新排列
        Map<String, List<Tag.Business>> code2tag = new HashMap<>();
        for (Tag.Business tag : tags) {
            for (String code : Splitter.on(",").trimResults().omitEmptyStrings().split(tag.code)) {
                code2tag.computeIfAbsent(code, _k -> new ArrayList<>()).add(tag);
            }
        }

        // tips: 基于优先级重新排序
        ConcurrentMap<String, List<Entity.Business>> code2businessMap = new ConcurrentHashMap<>(code2tag.size());
        for (Map.Entry<String, List<Tag.Business>> entry : code2tag.entrySet()) {
            String code = entry.getKey();
            List<Tag.Business> bizTags = entry.getValue();
            bizTags.sort(Comparator.comparing(bizTag -> bizTag.priority));
            List<Entity.Business> bizEntities = new ArrayList<>(bizTags.size());
            for (Tag.Business bizTag : bizTags) {
                bizEntities.add(toBusinessEntity(code, bizTag));
            }

            code2businessMap.put(code, bizEntities);
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

        // todo: 应该抛出异常, 因为前面已经来解析的时候做了拦截&校验了
        return null;
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
