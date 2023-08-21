package com.alibaba.water3.core;

import com.alibaba.water3.WaterContext;
import com.alibaba.water3.domain.Entity;
import com.alibaba.water3.domain.Tag;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.factory.HsfServiceFactory;
import com.alibaba.water3.factory.SpringBeanFactory;
import com.alibaba.water3.utils.DomLoader;
import com.alibaba.water3.utils.EntityConvertor;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.alibaba.water3.utils.PatternMatchUtils.match;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 17:52.
 */
@SuppressWarnings("unchecked")
public class WaterRegister {

    private static final String WATER_XML_CONFIG_LOCATION = "classpath*:water3*.xml";

    private static Map<String, Entity.BusinessScenario> scenarioMap;

    public static void register(String configStyle) throws Exception {
        // 1. 加载业务配置(目前只支持XML, 未来根据需要扩展更多的配置方式: yaml? json? groovy? java?)
        Collection<Tag.BusinessScenario> scenarios = DomLoader.loadingBusinessConfig(WATER_XML_CONFIG_LOCATION);
        // 2. 将业务标签转换为业务实体
        scenarioMap = ImmutableMap.copyOf(EntityConvertor.toScenarioMap(scenarios));
    }

    protected static List<Entity.InstanceWrapper> getSpiInstances(Class<?> extensionAbility, String extensionPoint) {
        String bizScenario = WaterContext.getBizScenario();
        if (Strings.isNullOrEmpty(bizScenario)) {
            throw new WaterException("[BizScenario] can't be empty: please invoke Water3.parseBizCode(...) before.");
        }

        String bizDomain = WaterContext.getBizDomain();
        if (Strings.isNullOrEmpty(bizDomain)) {
            throw new WaterException("[BizDomain] can't be empty: please invoke Water3.parseBizCode(...) before.");
        }

        String bizCode = WaterContext.getBizCode();
        if (Strings.isNullOrEmpty(bizCode)) {
            throw new WaterException("[BizCode] can't be empty: please invoke Water3.parseBizCode(...) before.");
        }

        Entity.BusinessScenario scenario = scenarioMap.get(bizScenario);
        if (scenario == null) {
            throw new WaterException(String.format("BusinessScenario:[%s] not found.", bizScenario));
        }

        Entity.ExtensionAbility ability = scenario.abilityMap.get(extensionAbility);
        if (ability == null) {
            throw new WaterException(String.format("ExtensionAbility:[%s#%s] not found.", scenario.scenario, extensionAbility.getName()));
        }

        Entity.ExtensionPoint point = ability.pointMap.get(extensionPoint);
        if (point == null) {
            throw new WaterException(String.format("ExtensionPoint:[%s#%s#%s] not found.", scenario.scenario, ability.clazz, extensionPoint));
        }

        return point.DOMAIN_CODE_INSTANCE_CACHE.computeIfAbsent(bizDomain, _K -> new ConcurrentHashMap<>()).computeIfAbsent(bizCode, _K -> {

            // BASE DOMAIN: 走普通的KV匹配逻辑
            // 扩展  DOMAIN: 走模式匹配逻辑(由于扩展DOMAIN大部分for平台扩展场景, 因此模式匹配会更加适用)
            // -- 由于扩展DOMAIN在XML定义时很难判断执行优先级, 因此本期暂定priority属性失效, 完全根据定义的顺序来排序
            // -- @翡青 是否要根据运行时的priority来排序呢? 思考ing.... (有需求再提吧~)

            List<Entity.Business> business;
            if (StringUtils.equals(bizDomain, Tag.DOMAIN_BASE)) {
                business = point.baseDomainBusinessMap.get(bizCode);
            } else {
                business = point.extDomainBusinessMap.getOrDefault(bizDomain, emptyList()).stream().filter(biz -> match(biz.code, bizCode)).collect(toList());
            }

            if (CollectionUtils.isEmpty(business)) {
                return Collections.singletonList(new Entity.InstanceWrapper("base", ability.base));
            } else {
                return business.stream().map(WaterRegister::makeInstance).collect(toList());
            }
        });
    }

    private static Entity.InstanceWrapper makeInstance(Entity.Business entity) {
        if (entity.instance != null) {
            return new Entity.InstanceWrapper(entity.impl, entity.instance);
        }

        // tips: 懒加载的具体实现
        try {
            if (entity.hsf != null) {
                entity.instance = HsfServiceFactory.getHsfService(entity.hsf);
            }
            if (entity.bean != null) {
                entity.instance = SpringBeanFactory.getSpringBean(entity.bean);
            }
        } catch (Exception e) {
            throw new WaterException(e);
        }

        Preconditions.checkState(entity.instance != null);

        return new Entity.InstanceWrapper(entity.impl, entity.instance);
    }
}
