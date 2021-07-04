package com.itcast.common.utils;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonSerializerUtil {

    private static Map<Class<?>, Schema<?>> classSchemaMap = new ConcurrentHashMap<>();
    private static Objenesis objenesis = new ObjenesisStd(true);

    public static <T> byte[] serialize(T t) {
        Class<T> cls = (Class<T>) t.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        try {
            Schema<T> schema = getClassSchema(cls);
            return ProtobufIOUtil.toByteArray(t, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> cls) {
        try {
            Schema<T> schema = getClassSchema(cls);
            T message = objenesis.newInstance(cls);
            ProtobufIOUtil.mergeFrom(bytes, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static <T> Schema<T> getClassSchema(Class<T> cls) {
        Schema<T> classSchema = null;
        if (classSchemaMap.containsKey(cls)) {
            classSchema = (Schema<T>) classSchemaMap.get(cls);
        } else {
            classSchema = RuntimeSchema.getSchema(cls);
            if (classSchema != null) {
                classSchemaMap.put(cls, classSchema);
            }
        }
        return classSchema;
    }
}
