package cn.egenie.architect.common.core.util;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.slf4j.helpers.MessageFormatter;

import cn.egenie.architect.common.core.constants.Constants;

import lombok.extern.slf4j.Slf4j;

;

/**
 * @author lucien
 * @since 2021/01/05
 */
@Slf4j
public class Strings {

    public static String of(String format, Object... args) {
        if (args == null || args.length == 0) {
            return format;
        }

        return MessageFormatter.arrayFormat(format, args).getMessage();
    }

    /**
     * 避免String.join NPE
     */
    public static String join(Collection<String> collection, String delimiter) {
        if (CollectionUtils.isEmpty(collection)) {
            return Constants.EMPTY_STRING;
        }

        return String.join(delimiter, collection);
    }

    /**
     * 下划线转驼峰
     */
    public static String toCamelCase(String str) {
        return CaseUtils.toCamelCase(str, false, '_');
    }


    /**
     * 驼峰转下划线
     */
    public static String toUnderline(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }

        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(Constants.UNDER_LINE);
                sb.append(Character.toLowerCase(c));
            }
            else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
