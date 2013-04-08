package com.airhacks.afterburner.injection;

/*
 * #%L
 * afterburner.fx
 * %%
 * Copyright (C) 2013 Adam Bien
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

/**
 *
 * @author adam-bien.com
 */
public class InjectionProvider {

    private static Map<Class, Object> providers = new HashMap<>();

    public static Object instantiateAndInject(Class clazz) {
        Object product = providers.get(clazz);
        if (product == null) {
            try {
                product = clazz.newInstance();
                providers.put(clazz, product);
                injectMembers(product);
                initialize(product);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new IllegalStateException("Cannot instantiate product: " + clazz, ex);
            }
        }

        return product;
    }

    static void injectMembers(Object instance) {
        Class<? extends Object> aClass = instance.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                Class<?> type = field.getType();
                Object target = instantiateAndInject(type);
                try {
                    field.setAccessible(true);
                    field.set(instance, target);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new IllegalStateException("Cannot set field: " + field, ex);
                }
            }
        }
    }

    static void initialize(Object instance) {
        invokeMethodWithAnnotation(instance, PostConstruct.class);
    }

    static void destroy(Object instance) {
        invokeMethodWithAnnotation(instance, PreDestroy.class);
    }

    static void invokeMethodWithAnnotation(Object instance, Class<? extends Annotation> annotationClass) throws IllegalStateException, SecurityException {
        Class<? extends Object> aClass = instance.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(annotationClass)) {
                try {
                    method.setAccessible(true);
                    method.invoke(instance, new Object[]{});
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw new IllegalStateException("Problem invoking " + annotationClass + " : " + method, ex);
                }
            }
        }
    }

    public static void forgetAll() {
        Collection<Object> values = providers.values();
        for (Object object : values) {
            destroy(object);
        }
        providers.clear();
    }
}
