package com.alibaba.water3.core;

import com.alibaba.water3.WaterContext;
import com.alibaba.water3.domain.Entity;
import com.alibaba.water3.domain.Tag;
import com.alibaba.water3.exception.WaterException;
import com.alibaba.water3.factory.HsfServiceFactory;
import com.alibaba.water3.factory.SpringBeanFactory;
import com.alibaba.water3.util.DomLoader;
import com.alibaba.water3.util.EntityConvertor;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 17:52.
 */
@SuppressWarnings("unchecked")
public class WaterRegister {

    private static final String WATER_XML_CONFIG_LOCATION = "classpath*:water3*.xml";

    // scenario -> ability(class) -> point(method) -> business -> impl
    private static final ConcurrentMap<String, Entity.BusinessScenario> scenarioMap = new ConcurrentHashMap<>();

    public static void register(String configStyle) throws Exception {
        // 1. 加载业务标签/配置
        Collection<Tag.BusinessScenario> tags = Collections.emptyList();
        if (StringUtils.equalsIgnoreCase(configStyle, "XML")) {
            tags = DomLoader.loadingBusinessDefinition(WATER_XML_CONFIG_LOCATION);
        } else {
            // todo: 未来扩展更多的配置方式 ... (yaml? json? groovy? java?)
        }

        // 2. 将业务标签转换为业务实体
        scenarioMap.putAll(EntityConvertor.toScenarioMap(tags));
    }

    public static <SPI> List<SPI> getSpiImpls(Class<SPI> extensionAbility, String extensionPoint) {
        String bizScenario = WaterContext.getBizScenario();
        if (Strings.isNullOrEmpty(bizScenario)) {
            throw new WaterException("BizScenario must be set.");
        }

        String bizId = WaterContext.getBizId();
        if (Strings.isNullOrEmpty(bizId)) {
            throw new WaterException("BizId must be set.");
        }

        Entity.BusinessScenario scenario = scenarioMap.get(bizScenario);
        if (scenario == null) {
            throw new WaterException(String.format("BusinessScenario:[%s] not found.", bizScenario));
        }

        Entity.ExtensionAbility ability = scenario.class2abilityMap.get(extensionAbility);
        if (ability == null) {
            throw new WaterException(String.format("ExtensionAbility:[%s#%s] not found.", scenario.scenario, extensionAbility.getName()));
        }

        Entity.ExtensionPoint point = ability.method2pointMap.get(extensionPoint);
        if (point == null) {
            throw new WaterException(String.format("ExtensionPoint:[%s#%s#%s] not found.", scenario.scenario, ability.clazz, extensionPoint));
        }

        return (List<SPI>) point.id2implCache.computeIfAbsent(bizId, _K -> {
            List<Entity.Business> business = point.id2businessMap.get(bizId);
            if (CollectionUtils.isEmpty(business)) {
                return Collections.singletonList(ability.baseImpl);
            } else {
                return business.stream().map(WaterRegister::makeImpl).collect(Collectors.toList());
            }
        });
    }

    private static Object makeImpl(Entity.Business entity) {
        if (entity.impl != null) {
            return entity.impl;
        }

        // tips: 懒加载的具体实现
        try {
            if (entity.hsf != null) {
                entity.impl = HsfServiceFactory.getHsfService(entity.hsf);
            }
            if (entity.bean != null) {
                entity.impl = SpringBeanFactory.getSpringBean(entity.bean);
            }
        } catch (Exception e) {
            throw new WaterException(e);
        }

        Preconditions.checkState(entity.impl != null);

        return entity.impl;
    }
}
