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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

/**
 *
 * @author adam-bien.com
 */
public class InjectionProvider {

    private static Map<Class, Object> models = new HashMap<>();
    private static List<Object> presenters = new ArrayList<>();

    public static Object instantiatePresenter(Class clazz) {
        try {
            Object product = injectAndInitialize(clazz.newInstance());
            presenters.add(product);
            return product;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalStateException("Cannot instantiate view: " + clazz, ex);
        }
    }

    static Object instantiateModel(Class clazz) {
        Object product = models.get(clazz);
        if (product == null) {
            try {
                product = injectAndInitialize(clazz.newInstance());
                models.put(clazz, product);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new IllegalStateException("Cannot instantiate view: " + clazz, ex);
            }
        }
        return product;
    }

    static Object injectAndInitialize(Object product) {
        injectMembers(product);
        initialize(product);
        return product;
    }

    static void injectMembers(final Object instance) {
        Class<? extends Object> aClass = instance.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (final Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                Class<?> type = field.getType();
                final Object target = instantiateModel(type);
                AccessController.doPrivileged(new PrivilegedAction() {
                    @Override
                    public Object run() {
                        boolean wasAccessible = field.isAccessible();
                        try {
                            field.setAccessible(true);
                            field.set(instance, target);
                            return null; // return nothing...
                        } catch (IllegalArgumentException | IllegalAccessException ex) {
                            throw new IllegalStateException("Cannot set field: " + field, ex);
                        } finally {
                            field.setAccessible(wasAccessible);
                        }
                    }
                });
            }
        }
    }

    static void initialize(Object instance) {
        invokeMethodWithAnnotation(instance, PostConstruct.class);
    }

    static void destroy(Object instance) {
        invokeMethodWithAnnotation(instance, PreDestroy.class);
    }

    static void invokeMethodWithAnnotation(final Object instance, final Class<? extends Annotation> annotationClass) throws IllegalStateException, SecurityException {
        Class<? extends Object> aClass = instance.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (final Method method : declaredMethods) {
            if (method.isAnnotationPresent(annotationClass)) {
                AccessController.doPrivileged(new PrivilegedAction() {
                    @Override
                    public Object run() {
                        boolean wasAccessible = method.isAccessible();
                        try {
                            method.setAccessible(true);
                            return method.invoke(instance, new Object[]{});
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            throw new IllegalStateException("Problem invoking " + annotationClass + " : " + method, ex);
                        } finally {
                            method.setAccessible(wasAccessible);
                        }
                    }
                });
            }
        }
    }

    public static void forgetAll() {
        Collection<Object> values = models.values();
        for (Object object : values) {
            destroy(object);
        }
        for (Object object : presenters) {
            destroy(object);
        }
        models.clear();
    }
}
