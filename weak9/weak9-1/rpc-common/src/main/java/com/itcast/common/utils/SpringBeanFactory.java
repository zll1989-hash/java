package com.itcast.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

@Component
public class SpringBeanFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    public static <T> T getBean(Class<T> cls) {
        return context.getBean(cls);
    }

    public static Map<String, Object> getBeanListByAnnotationClass(Class<? extends Annotation> annotationClass) {
         return context.getBeansWithAnnotation(annotationClass);
    }

    public static ApplicationContext context() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
