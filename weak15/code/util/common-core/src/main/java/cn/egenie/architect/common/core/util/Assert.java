package cn.egenie.architect.common.core.util;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 1. spring assert使用反向逻辑容易出错，这里使用正向逻辑
 * 2. 抛出异常使用log4j方式拼接参数
 *
 * @author lucien
 * @since 2021/01/05
 */
public class Assert {
    public static void throwIfTrue(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void throwIfTrue(boolean expression, String format, Object... args) {
        if (expression) {
            throw new IllegalArgumentException(Strings.of(format, args));
        }
    }

    public static void throwIfNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void throwIfNull(Object object, String format, Object... args) {
        if (object == null) {
            throw new IllegalArgumentException(Strings.of(format, args));
        }
    }

    public static void throwIfBlank(String text, String message) {
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void throwIfBlank(String text, String format, Object... args) {
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException(Strings.of(format, args));
        }
    }

    public static void throwIfEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void throwIfEmpty(Collection<?> collection, String format, Object... args) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalArgumentException(Strings.of(format, args));
        }
    }

    public static void throwIfEmpty(Map<?, ?> map, String message) {
        if (MapUtils.isEmpty(map)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void throwIfEmpty(Map<?, ?> map, String format, Object... args) {
        if (MapUtils.isEmpty(map)) {
            throw new IllegalArgumentException(Strings.of(format, args));
        }
    }
}
