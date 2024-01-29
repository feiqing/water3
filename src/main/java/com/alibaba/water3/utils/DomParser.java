package com.alibaba.water3.utils;

import com.alibaba.water3.domain.Tag;
import com.alibaba.water3.exception.WaterException;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.Nonnull;
import java.util.*;

import static java.util.Optional.ofNullable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 19:38.
 */
@Slf4j
public class DomParser {

    private static final SAXReader saxReader = new SAXReader();

    public static Set<Tag.Extension> loadingConfigFiles(String configFileLocation) throws Exception {
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(configFileLocation);
        Set<Tag.Extension> allExtensions = new HashSet<>(resources.length);

        for (Resource resource : resources) {
            String file = resource.getFilename();
            List<Tag.Extension> extensions = loadingExtensions(file, saxReader.read(resource.getInputStream()).getRootElement());

            for (Tag.Extension extension : extensions) {
                if (!allExtensions.add(extension)) {
                    throw new WaterException(String.format("Extension:[%s] is duplicated in file:[%s]", extension.clazz, file));
                }

                log.info("loaded Extension:[{}/{}] business:{} from file:[{}].", extension.clazz, extension.desc, extension.businessList.size(), file);
            }
        }

        return allExtensions;
    }

    private static List<Tag.Extension> loadingExtensions(String file, Element document) {

        List<Tag.Extension> extensions = new LinkedList<>();
        for (Iterator<Element> iterator = document.elementIterator(); iterator.hasNext(); ) {
            extensions.add(loadingExtension(file, iterator.next()));
        }

        return extensions;
    }

    private static Tag.Extension loadingExtension(String file, Element element) {
        String clazz = getAttrValNoneNull(element, file, null, "<Extension/>", "class");
        String base = getAttrValNoneNull(element, file, null, "<Extension/>", "base");

        Tag.Extension extension = new Tag.Extension(clazz, base);
        extension.businessList = new LinkedList<>();
        for (Iterator<Element> iterator = element.elementIterator(); iterator.hasNext(); ) {
            extension.businessList.add(loadingBusiness(file, clazz, iterator.next()));
        }
        extension.desc = element.attributeValue("desc");
        extension.group = parseGroup(file);
        extension.proxy = BooleanUtils.toBoolean(element.attributeValue("proxy"));

        return extension;
    }

    private static String parseGroup(String file) {
        return StringUtils.upperCase(StringUtils.substringAfter(StringUtils.substringBefore(file, ".xml"), "water3-"));
    }

    private static Tag.Business loadingBusiness(String file, String path, Element element) {
        String code = getAttrValNoneNull(element, file, path, "<Business/>", "code");
        String type = getAttrValNoneNull(element, file, path, "<Business/>", "type");

        Tag.Business business = new Tag.Business(code, type);
        business.desc = element.attributeValue("desc");
        ofNullable(element.attributeValue("priority")).map(Integer::valueOf).ifPresent(priority -> business.priority = priority);

        if (StringUtils.equals("bean", type)) {
            business.bean = loadingBean(file, path + "#" + code, element.element("bean"));
        } else if (StringUtils.equals("hsf", type)) {
            business.hsf = loadingHsf(file, path + "#" + code, code, element.element("hsf"));
        } else {
            throw new WaterException(String.format("path:[%s] business code: %s 's type:[%s] is not support in file:[%s].", path, code, type, file));
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
//        if (Splitter.on(",").trimResults().omitEmptyStrings().splitToList(codes).stream().noneMatch(version::endsWith)) {
//            throw new WaterException(String.format("service:[%s] version:[%s] error in file:[%s].", service, version, file));
//        }

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
