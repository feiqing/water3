package com.alibaba.water3.utils;

import com.alibaba.water3.domain.Tag;
import com.alibaba.water3.exception.WaterException;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import static java.util.Optional.ofNullable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 19:38.
 */
@Slf4j
public class DomLoader {

    private static final SAXReader saxReader = new SAXReader();

    public static Set<Tag.BusinessScenario> loadingBusinessConfig(String configFileLocation) throws Exception {
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(configFileLocation);
        Set<Tag.BusinessScenario> scenarios = new HashSet<>(resources.length);

        for (Resource resource : resources) {
            String filename = resource.getFilename();
            Document document = saxReader.read(resource.getInputStream());
            Tag.BusinessScenario scenario = loadingBusinessScenario(filename, document.getRootElement());
            if (!scenarios.add(scenario)) {
                throw new WaterException(String.format("BusinessScenario[%s] duplicated in file:[%s]", scenario.scenario, filename));
            }

            log.info("loaded BusinessScenario:[{}][{}][{}] in file:[{}].", scenario.scenario, scenario.extensionAbilityList.size(),
                    scenario.extensionAbilityList.stream().mapToInt(ability -> ability.extensionPointList.size()).sum(), filename);
        }

        return scenarios;
    }

    private static Tag.BusinessScenario loadingBusinessScenario(String file, Element document) {
        String _scenario = getAttrValNoneNull(document, file, "", "<water3/>", "scenario");

        Tag.BusinessScenario scenario = new Tag.BusinessScenario(_scenario);
        scenario.desc = document.attributeValue("desc");
        scenario.extensionAbilityList = new LinkedList<>();
        for (Iterator<Element> iterator = document.elementIterator(); iterator.hasNext(); ) {
            scenario.extensionAbilityList.add(loadingExtensionAbility(file, _scenario, iterator.next()));
        }

        return scenario;
    }

    private static Tag.ExtensionAbility loadingExtensionAbility(String file, String path, Element element) {
        String clazz = getAttrValNoneNull(element, file, path, "<ExtensionAbility/>", "class");
        String base = getAttrValNoneNull(element, file, path, "<ExtensionAbility/>", "base");

        Tag.ExtensionAbility ability = new Tag.ExtensionAbility(clazz, base);
        ability.desc = element.attributeValue("desc");
        ability.extensionPointList = new LinkedList<>();
        for (Iterator<Element> iterator = element.elementIterator(); iterator.hasNext(); ) {
            ability.extensionPointList.add(loadingExtensionPoint(file, path + "#" + clazz, iterator.next()));
        }

        return ability;
    }

    private static Tag.ExtensionPoint loadingExtensionPoint(String file, String path, Element element) {
        String method = getAttrValNoneNull(element, file, path, "<ExtensionPoint/>", "method");

        Tag.ExtensionPoint point = new Tag.ExtensionPoint(method);
        point.args = element.attributeValue("args");
        point.result = element.attributeValue("result");
        point.desc = element.attributeValue("desc");
        point.businesList = new LinkedList<>();
        for (Iterator<Element> iterator = element.elementIterator(); iterator.hasNext(); ) {
            point.businesList.add(loadingBusiness(file, path + "#" + method, iterator.next()));
        }

        return point;
    }

    private static Tag.Business loadingBusiness(String file, String path, Element element) {
        String code = getAttrValNoneNull(element, file, path, "<Business/>", "code");
        String impl = getAttrValNoneNull(element, file, path, "<Business/>", "impl");

        Tag.Business business = new Tag.Business(code, impl);
        business.desc = element.attributeValue("desc");
        ofNullable(element.attributeValue("priority")).map(Integer::valueOf).ifPresent(priority -> business.priority = priority);
        ofNullable(element.attributeValue("domain")).ifPresent(domain -> business.domain = domain);
        if (StringUtils.equals("bean", impl)) {
            business.bean = loadingBean(file, path + "#" + code, element.element("bean"));
        } else if (StringUtils.equals("hsf", impl)) {
            business.hsf = loadingHsf(file, path + "#" + code, code, element.element("hsf"));
        } else {
            throw new WaterException(String.format("path:[%s] business code: %s 's impl:[%s] is not support in file:[%s].", path, code, impl, file));
        }

        return business;
    }

    private static Tag.Bean loadingBean(String file, String path, Element element) {
        String tag = "<bean/>";
        if (element == null) {
            throw new WaterException(String.format("%s's tag %s definition can not be null in file:[%s].", path, tag, file));
        }

        String name = getAttrValNoneNull(element, file, path, tag, "name");
        Tag.Bean bean = new Tag.Bean(name);
        ofNullable(element.attributeValue("lazy")).map(Boolean::valueOf).ifPresent(lazy -> bean.lazy = lazy);
        return bean;
    }

    private static Tag.Hsf loadingHsf(String file, String path, String codes, Element element) {
        String tag = "<hsf/>";
        if (element == null) {
            throw new WaterException(String.format("%s's tag %s definition can not be null in file:[%s].", path, tag, file));
        }

        String service = getAttrValNoneNull(element, file, path, tag, "service");
        String version = getAttrValNoneNull(element, file, path, tag, "version");
        if (Splitter.on(",").trimResults().omitEmptyStrings().splitToList(codes).stream().noneMatch(version::endsWith)) {
            throw new WaterException(String.format("service:[%s] version:[%s] error in file:[%s].", service, version, file));
        }

        Tag.Hsf hsf = new Tag.Hsf(service, version);
        ofNullable(element.attributeValue("group")).ifPresent(group -> hsf.group = group);
        ofNullable(element.attributeValue("timeout")).map(Integer::valueOf).ifPresent(timeout -> hsf.timeout = timeout);
        ofNullable(element.attributeValue("lazy")).map(Boolean::valueOf).ifPresent(lazy -> hsf.lazy = lazy);

        return hsf;
    }

    private static @Nonnull String getAttrValNoneNull(Element element, String file, String path, String tag, String attr) {
        String value = element.attributeValue(attr);
        if (Strings.isNullOrEmpty(value)) {
            if (Strings.isNullOrEmpty(path)) {
                throw new WaterException(String.format("tag:%s's attr '%s' can not be empty in file:[%s].", tag, attr, file));
            } else {
                throw new WaterException(String.format("path:[%s] tag:%s's attr '%s' can not be empty in file:[%s].", path, tag, attr, file));
            }
        }

        return value;
    }
}
