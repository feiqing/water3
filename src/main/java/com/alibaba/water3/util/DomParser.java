package com.alibaba.water3.util;

import com.alibaba.water3.domain.Tag;
import com.alibaba.water3.exception.WaterException;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 19:38.
 */
public class DomParser {

    // todo add log
    public static List<Tag.ExtensionGroup> loadExtensionGroups(String configFileLocation) throws Exception {
        SAXReader reader = new SAXReader();

        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(configFileLocation);
        List<Tag.ExtensionGroup> extensionGroups = new LinkedList<>();
        Set<String> loadedGroupSet = new HashSet<>();

        for (Resource resource : resources) {
            String filename = resource.getFilename();
            // todo 删除ExtensionGroup
            Document document = reader.read(resource.getInputStream());

            for (Iterator<Element> iterator = document.getRootElement().elementIterator(); iterator.hasNext(); ) {
                Tag.ExtensionGroup extensionGroup = loadExtensionGroup(filename, iterator.next());
                if (!loadedGroupSet.add(extensionGroup.name)) {
                    throw new WaterException("todo");
                }
                extensionGroups.add(extensionGroup);
            }
        }

        return extensionGroups;
    }

    private static Tag.ExtensionGroup loadExtensionGroup(String filename, Element extensionGroup) {
        String name = getAttrValNoneNull(extensionGroup, "", "<ExtensionGroup/>", "name");

        Tag.ExtensionGroup entity = new Tag.ExtensionGroup(name);
        entity.desc = extensionGroup.attributeValue("desc");
        entity.extensionAbilityList = new LinkedList<>();
        for (Iterator<Element> iterator = extensionGroup.elementIterator(); iterator.hasNext(); ) {
            entity.extensionAbilityList.add(loadExtensionAbility(name, iterator.next()));
        }

        return entity;
    }

    private static Tag.ExtensionAbility loadExtensionAbility(String path, Element extensionAbility) {
        String clazz = getAttrValNoneNull(extensionAbility, path, "<ExtensionAbility/>", "class");
        String base = getAttrValNoneNull(extensionAbility, path, "<ExtensionAbility/>", "base");

        Tag.ExtensionAbility entity = new Tag.ExtensionAbility(clazz, base);
        entity.desc = extensionAbility.attributeValue("desc");
        entity.extensionPointList = new LinkedList<>();
        for (Iterator<Element> iterator = extensionAbility.elementIterator(); iterator.hasNext(); ) {
            entity.extensionPointList.add(loadExtensionPoint(path + "#" + clazz, iterator.next()));
        }

        return entity;
    }

    private static Tag.ExtensionPoint loadExtensionPoint(String path, Element extensionPoint) {
        String method = getAttrValNoneNull(extensionPoint, path, "<ExtensionPoint/>", "method");

        Tag.ExtensionPoint entity = new Tag.ExtensionPoint(method);
        entity.args = extensionPoint.attributeValue("args");
        entity.result = extensionPoint.attributeValue("result");
        entity.desc = extensionPoint.attributeValue("desc");
        entity.businesList = new LinkedList<>();
        for (Iterator<Element> iterator = extensionPoint.elementIterator(); iterator.hasNext(); ) {
            entity.businesList.add(loadBusiness(path + "#" + method, iterator.next()));
        }

        return entity;
    }

    private static Tag.Business loadBusiness(String path, Element business) {
        String code = getAttrValNoneNull(business, path, "<Business/>", "code");
        String impl = getAttrValNoneNull(business, path, "<Business/>", "impl");

        Tag.Business entity = new Tag.Business(code, impl);
        entity.desc = business.attributeValue("desc");
        setInt(business.attributeValue("priority"), entity::setPriority);
        if (StringUtils.equals("bean", impl)) {
            entity.bean = loadBean(path + "#" + code, business.element("bean"));
        } else if (StringUtils.equals("hsf", impl)) {
            entity.hsf = loadHsf(path + "#" + code, code, business.element("hsf"));
        } else {
            throw new WaterException(String.format("path:[%s] business code: %s 's impl:[%s] is not support", path, code, impl));
        }

        return entity;
    }

    private static Tag.Bean loadBean(String path, Element bean) {
        String tag = "<bean/>";
        Preconditions.checkState(bean != null, String.format("%s's tag %s definition can not be null", path, tag));

        String name = getAttrValNoneNull(bean, path, tag, "name");
        Tag.Bean beanTag = new Tag.Bean(name);
        setBool(bean.attributeValue("lazy"), beanTag::setLazy);
        return beanTag;
    }

    private static Tag.Hsf loadHsf(String path, String codes, Element hsf) {
        String tag = "<hsf/>";
        Preconditions.checkState(hsf != null, String.format("%s's tag %s definition can not be null", path, tag));

        String service = getAttrValNoneNull(hsf, path, tag, "service");
        String version = getAttrValNoneNull(hsf, path, tag, "version");

        boolean versionValid = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(codes).stream().anyMatch(version::endsWith);
        if (!versionValid) {
            throw new WaterException(String.format("service:[%s] version:[%s] error", service, version));
        }

        Tag.Hsf entity = new Tag.Hsf(service, version);
        setString(hsf.attributeValue("group"), entity::setGroup);
        setInt(hsf.attributeValue("timeout"), entity::setTimeout);
        setBool(hsf.attributeValue("lazy"), entity::setLazy);

        return entity;
    }

    private static @Nonnull String getAttrValNoneNull(Element element, String path, String tag, String attr) {
        String value = element.attributeValue(attr);
        if (Strings.isNullOrEmpty(value)) {
            if (Strings.isNullOrEmpty(path)) {
                throw new WaterException(String.format("tag:%s's attr '%s' can not be empty", tag, attr));
            } else {
                throw new WaterException(String.format("path:[%s] tag:%s's attr '%s' can not be empty", path, tag, attr));
            }
        }

        return value;
    }

    private static void setBool(String value, Consumer<Boolean> func) {
        Optional.ofNullable(value).map(Boolean::valueOf).ifPresent(func);
    }

    private static void setInt(String value, Consumer<Integer> func) {
        Optional.ofNullable(value).map(Integer::parseInt).ifPresent(func);
    }

    private static void setString(String value, Consumer<String> func) {
        Optional.ofNullable(value).ifPresent(func);
    }
}
