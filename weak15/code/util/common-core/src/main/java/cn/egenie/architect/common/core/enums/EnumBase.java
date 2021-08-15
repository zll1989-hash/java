package cn.egenie.architect.common.core.enums;

import java.util.EnumSet;
import java.util.Map;

import cn.egenie.architect.common.core.util.Assert;
import cn.egenie.architect.common.core.util.Funs;

/**
 * @author lucien
 * @since 2021/01/07
 */
public interface EnumBase {
    int id();

    String message();

    static <E extends Enum<E> & EnumBase> E ofId(Class<E> type, int id) {
        E t = ofIdNullable(type, id);
        Assert.throwIfNull(t, "No enum const {} for id {}", type.getName(), id);
        return t;
    }

    static <E extends Enum<E> & EnumBase> E ofIdNullable(Class<E> type, int id) {
        EnumSet<E> values = EnumSet.allOf(type);
        return values.stream()
                .filter(t -> t.id() == id)
                .findFirst()
                .orElse(null);
    }

    static <E extends Enum<E> & EnumBase> E ofMessage(Class<E> type, String message) {
        E t = ofMessageNullable(type, message);
        Assert.throwIfNull(t, "No enum const {} for message {}", type.getName(), message);
        return t;
    }

    static <E extends Enum<E> & EnumBase> E ofMessageNullable(Class<E> type, String message) {
        Assert.throwIfBlank(message, "message must not be blank");

        EnumSet<E> values = EnumSet.allOf(type);
        return values.stream()
                .filter(t -> t.message().equals(message))
                .findFirst()
                .orElse(null);
    }

    static <E extends Enum<E> & EnumBase> Map<Integer, String> enumToMap(Class<E> type) {
        EnumSet<E> values = EnumSet.allOf(type);
        return Funs.toMapQuietly(values, EnumBase::id, EnumBase::message);
    }

    static <E extends Enum<E> & EnumBase> boolean hasId(Class<E> type, int id) {
        return ofIdNullable(type, id) != null;
    }

    static <E extends Enum<E> & EnumBase> boolean notHasId(Class<E> type, int id) {
        return !hasId(type, id);
    }

    static <E extends Enum<E> & EnumBase> boolean hasMessage(Class<E> type, String message) {
        return ofMessageNullable(type, message) != null;
    }

    static <E extends Enum<E> & EnumBase> boolean notHasMessage(Class<E> type, String message) {
        return !hasMessage(type, message);
    }
}
