package cn.egenie.architect.common.core.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import cn.egenie.architect.common.core.exception.BusinessException;


/**
 * @author lucien
 * @since 2021/01/05
 */
public class Bssert {
    public static void throwIfTrue(boolean expression, String message) {
        if (expression) {
            throw new BusinessException(message);
        }
    }

    public static void throwIfTrue(boolean expression, String format, Object... args) {
        if (expression) {
            throw new BusinessException(Strings.of(format, args));
        }
    }

    public static void throwIfTrue(boolean expression, Consumer<BusinessException> consumer, String format, Object... args) {
        if (expression) {
            BusinessException t = new BusinessException(Strings.of(format, args));
            consumer.accept(t);
            throw t;
        }
    }

    public static void throwIfNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message);
        }
    }

    public static void throwIfNull(Object object, String format, Object... args) {
        if (object == null) {
            throw new BusinessException(Strings.of(format, args));
        }
    }

    public static void throwIfNull(Object object, Consumer<BusinessException> consumer, String format, Object... args) {
        if (object == null) {
            BusinessException t = new BusinessException(Strings.of(format, args));
            consumer.accept(t);
            throw t;
        }
    }


    public static void throwIfBlank(String text, String message) {
        if (StringUtils.isBlank(text)) {
            throw new BusinessException(message);
        }
    }


    public static void throwIfBlank(String text, String format, Object... args) {
        if (StringUtils.isBlank(text)) {
            throw new BusinessException(Strings.of(format, args));
        }
    }

    public static void throwIfBlank(String text, Consumer<BusinessException> consumer, String format, Object... args) {
        if (StringUtils.isBlank(text)) {
            BusinessException t = new BusinessException(Strings.of(format, args));
            consumer.accept(t);
            throw t;
        }
    }

    public static void throwIfEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(message);
        }
    }

    public static void throwIfEmpty(Collection<?> collection, String format, Object... args) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(Strings.of(format, args));
        }
    }

    public static void throwIfEmpty(Collection<?> collection, Consumer<BusinessException> consumer, String format, Object... args) {
        if (CollectionUtils.isEmpty(collection)) {
            BusinessException t = new BusinessException(Strings.of(format, args));
            consumer.accept(t);
            throw t;
        }
    }

    public static void throwIfEmpty(Map<?, ?> map, String message) {
        if (MapUtils.isEmpty(map)) {
            throw new BusinessException(message);
        }
    }

    public static void throwIfEmpty(Map<?, ?> map, String format, Object... args) {
        if (MapUtils.isEmpty(map)) {
            throw new BusinessException(Strings.of(format, args));
        }
    }

    public static void throwIfEmpty(Map<?, ?> map, Consumer<BusinessException> consumer, String format, Object... args) {
        if (MapUtils.isEmpty(map)) {
            BusinessException t = new BusinessException(Strings.of(format, args));
            consumer.accept(t);
            throw t;
        }
    }
}
