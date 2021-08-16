package cn.egenie.architect.common.core.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import cn.egenie.architect.common.core.constants.Constants;


/**
 * @author lucien
 * @since 2021/01/05
 */
public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T parse(String json, Class<T> targetClass) {
        try {
            return mapper.readValue(json, targetClass);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> List<T> parseList(String json, Class<T> targetClass) {
        try {
            TypeFactory typeFactory = mapper.getTypeFactory();
            return mapper.readValue(json, typeFactory.constructCollectionType(List.class, targetClass));
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <K, V> Map<K, V> parseMap(String json, Class<K> keyClass, Class<V> valueClass) {
        try {
            TypeFactory typeFactory = mapper.getTypeFactory();
            return mapper.readValue(json, typeFactory.constructMapType(Map.class, keyClass, valueClass));
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return mapper.convertValue(fromValue, toValueType);
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
        return mapper.convertValue(fromValue, toValueTypeRef);
    }

    public static <T> List<T> convertList(Object list, Class<T> toListElementType) {
        TypeFactory typeFactory = mapper.getTypeFactory();
        CollectionType javaType = typeFactory.constructCollectionType(List.class, toListElementType);
        return mapper.convertValue(list, javaType);
    }

    /**
     * @param map underLine 类型的map转驼峰命名的Bean，只支持浅转换
     */
    public static <T> T convertToCamelBean(Map<String, Object> map, Class<T> toValueType) {
        Assert.throwIfNull(map, "map is required");

        Map<String, Object> mapTmp = new HashMap<>(map.size());
        map.forEach((k, v) -> {
            mapTmp.put(Strings.toCamelCase(k), v);
        });

        return convertValue(mapTmp, toValueType);
    }

    /**
     * bean转换成下划线命名的map, 只支持浅转换
     */
    public static Map<String, Object> convertToUnderLineMap(Object fromValue) {
        Assert.throwIfNull(fromValue, "fromValue is required");

        Map<String, Object> mapTmp = convertValue(fromValue, Map.class);
        Map<String, Object> map = new HashMap<>(mapTmp.size());
        mapTmp.forEach((k, v) -> {
            map.put(Strings.toUnderline(k), v);
        });

        return map;
    }

    /**
     * xpath方式获取json
     */
    public static <T> T getValue(String json, String jsonPath, Class<T> valueClass) {
        List<T> values = getValues(json, jsonPath, valueClass);
        return values.get(0);
    }

    /**
     * xpath方式获取json
     */
    public static <T> List<T> getValues(String json, String jsonPath, Class<T> valueClass) {
        try {
            JsonNode node = mapper.readTree(json);
            return getValues(node, jsonPath, valueClass);

        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static JsonNode readTree(String json) {
        try {
            return mapper.readTree(json);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static <T> List<T> getValues(JsonNode jsonNode, String jsonPath, Class<T> valueClass) {
        String[] paths = jsonPath.split(Constants.POINT_SPLITTER);
        Queue<JsonNode> queue = new LinkedList<>();
        queue.offer(jsonNode);

        int level = 0;

        while (level < paths.length) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                JsonNode node = queue.poll();
                if (node == MissingNode.getInstance()) {
                    throw new RuntimeException(Strings.of("path {} is invalid", jsonPath));
                }

                if (node.isObject()) {
                    queue.offer(node.path(paths[level]));
                }
                else if (node.isArray()) {
                    for (JsonNode element : node) {
                        queue.offer(element.path(paths[level]));
                    }
                }
            }

            level++;
        }

        return Funs.map(queue, node -> convertValue(node, valueClass));
    }
}
