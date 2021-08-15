package cn.egenie.architect.common.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import cn.egenie.architect.common.core.constants.Constants;

/**
 * @author lucien
 * @since 2021/01/05
 */
public class Funs {

    public static <T, R> List<R> map(Collection<T> collection, Function<? super T, ? extends R> mapper) {
        if (CollectionUtils.isEmpty(collection)) {
            return Collections.emptyList();
        }

        return collection.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }


    public static <T, U, A, R> R map(Collection<T> collection,
                                     Function<? super T, ? extends U> mapper,
                                     Collector<? super U, A, R> collector) {
        if (CollectionUtils.isEmpty(collection)) {
            return null;
        }

        return collection.stream()
                .map(mapper)
                .collect(collector);
    }


    public static <T, U, R extends Collection<U>> R map(Collection<T> collection,
                                                        Function<? super T, ? extends U> mapper,
                                                        Supplier<R> supplier) {
        R container = supplier.get();
        if (CollectionUtils.isEmpty(collection)) {
            return container;
        }

        collection.forEach(element -> container.add(mapper.apply(element)));
        return container;
    }


    public static <T> List<T> filter(Collection<T> collection, Predicate<? super T> predicate) {
        if (CollectionUtils.isEmpty(collection)) {
            return Collections.emptyList();
        }

        return collection.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public static <T> T filterFirst(Collection<T> collection, Predicate<? super T> predicate) {
        if (CollectionUtils.isEmpty(collection)) {
            return null;
        }

        return collection.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }


    public static <T, R> List<R> filterAndMap(Collection<T> collection,
                                              Predicate<? super T> predicate,
                                              Function<? super T, ? extends R> mapper) {
        if (CollectionUtils.isEmpty(collection)) {
            return Collections.emptyList();
        }

        return collection.stream()
                .filter(predicate)
                .map(mapper)
                .collect(Collectors.toList());
    }


    public static <T, K, U> Map<K, U> toMap(Collection<T> collection,
                                            Function<? super T, ? extends K> keyMapper,
                                            Function<? super T, ? extends U> valueMapper,
                                            BinaryOperator<U> mergeFunction) {
        if (CollectionUtils.isEmpty(collection)) {
            return Collections.emptyMap();
        }

        return collection.stream()
                .collect(Collectors.toMap(keyMapper, valueMapper, mergeFunction));
    }

    public static <T, K, U> Map<K, U> toMapQuietly(Collection<T> collection,
                                                   Function<? super T, ? extends K> keyMapper,
                                                   Function<? super T, ? extends U> valueMapper) {
        if (CollectionUtils.isEmpty(collection)) {
            return Collections.emptyMap();
        }

        return collection.stream()
                .collect(Collectors.toMap(keyMapper, valueMapper, (u1, u2) -> u1));
    }


    public static <T, K> Map<K, List<T>> groupingBy(Collection<T> collection, Function<? super T, ? extends K> classifier) {
        if (CollectionUtils.isEmpty(collection)) {
            return Collections.emptyMap();
        }

        return collection.stream()
                .collect(Collectors.groupingBy(classifier));

    }

    public static <K, V> Map<V, K> reverseMap(Map<K, V> map) {
        if (MapUtils.isEmpty(map)) {
            return Collections.emptyMap();
        }

        Map<V, K> reverseMap = new HashMap<>(map.size());
        map.forEach((k, v) -> reverseMap.put(v, k));

        return reverseMap;
    }


    public static <T> void forEach(Collection<T> collection, BiConsumer<Integer, ? super T> action) {
        Assert.throwIfNull(collection, "collection required");
        Assert.throwIfNull(action, "action required");

        int index = 0;
        for (T element : collection) {
            action.accept(index, element);
            index++;
        }
    }

    public static <T> void forEach(T[] array, BiConsumer<Integer, ? super T> action) {
        Assert.throwIfNull(array, "array required");
        Assert.throwIfNull(action, "action required");

        Stream.iterate(0, i -> i + 1)
                .limit(array.length)
                .forEach(i -> action.accept(i, array[i]));
    }

    public static <T> boolean allMatch(Collection<T> collection, Predicate<? super T> predicate) {
        if (CollectionUtils.isEmpty(collection)) {
            return false;
        }

        return collection.stream()
                .allMatch(predicate);
    }

    public static <T> boolean anyMatch(Collection<T> collection, Predicate<? super T> predicate) {
        if (CollectionUtils.isEmpty(collection)) {
            return false;
        }

        return collection.stream()
                .anyMatch(predicate);
    }

    public static <T> boolean noneMatch(Collection<T> collection, Predicate<? super T> predicate) {
        if (CollectionUtils.isEmpty(collection)) {
            return true;
        }

        return collection.stream()
                .noneMatch(predicate);
    }
}
