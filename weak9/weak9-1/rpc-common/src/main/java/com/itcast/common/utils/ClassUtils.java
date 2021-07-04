package com.itcast.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类加载器管理工具
 */
public class ClassUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtils.class);

    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class<?> loadClass(String className) {
        return loadClass(className, true);
    }

    public static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cls = null;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.error("Load class {} error!", className, e);
            throw new RuntimeException(e);
        }
        return cls;
    }
}
