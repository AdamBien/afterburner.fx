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
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

/**
 *
 * @author adam-bien.com
 */
public class InjectionProvider {

    public final static String CONFIGURATION_FILE = "configuration.properties";

    private static final Map<Class, Object> modelsAndServices = new WeakHashMap<>();
    private static final Set<Object> presenters = Collections.newSetFromMap(new WeakHashMap<>());

    public static Object instantiatePresenter(Class clazz) {
        try {
            Object product = registerExistingAndInject(clazz.newInstance());
            return product;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalStateException("Cannot instantiate view: " + clazz, ex);
        }
    }

    /**
     * Caches the passed presenter internally and injects all fields internally
     *
     * @param instance An already existing (legacy) presenter interesting in
     * injection
     * @return presenter with injected fields
     */
    public static Object registerExistingAndInject(Object instance) {
        Object product = injectAndInitialize(instance);
        presenters.add(product);
        return product;
    }

    public static Object instantiateModelOrService(Class clazz) {
        Object product = modelsAndServices.get(clazz);
        if (product == null) {
            try {
                product = injectAndInitialize(clazz.newInstance());
                if (!modelsAndServices.containsKey(clazz)) {
                    modelsAndServices.put(clazz, product);
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new IllegalStateException("Cannot instantiate view: " + clazz, ex);
            }
        }
        return product;
    }

    public static void setModelOrService(Class clazz, Object instance) {
        modelsAndServices.put(clazz, instance);
    }

    static Object injectAndInitialize(Object product) {
        injectMembers(product);
        initialize(product);
        return product;
    }

    static Properties getProperties(Class clazz) {
        Properties configuration = new Properties();
        try (InputStream stream = clazz.getResourceAsStream(CONFIGURATION_FILE)) {
            if (stream == null) {
                return null;
            }
            configuration.load(stream);
        } catch (IOException ex) {
            //a property file does not have to exist...
        }
        return configuration;
    }

    static String getProperty(Class clazz, String key) {
        Properties properties = getProperties(clazz);
        if (properties != null) {
            return properties.getProperty(key);
        }
        return null;
    }

    static void injectMembers(final Object instance) {
        Class<? extends Object> clazz = instance.getClass();
        injectMembers(clazz, instance);
    }

    public static void injectMembers(Class<? extends Object> clazz, final Object instance) throws SecurityException {
        Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                Class<?> type = field.getType();
                if (type.isAssignableFrom(String.class)) {
                    String key = field.getName();
                    String systemProperty = System.getProperty(key);
                    String resultingValue;
                    if (systemProperty != null) {
                        resultingValue = systemProperty;
                    } else {
                        resultingValue = getProperty(clazz, key);
                    }
                    injectIntoField(field, instance, resultingValue);
                } else {
                    final Object target = instantiateModelOrService(type);
                    injectIntoField(field, instance, target);
                }
            }
        }
        Class<? extends Object> superclass = clazz.getSuperclass();
        if (superclass != null) {
            injectMembers(superclass, instance);
        }
    }

    static void injectIntoField(final Field field, final Object instance, final Object target) {
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

    static void initialize(Object instance) {
        Class<? extends Object> clazz = instance.getClass();
        invokeMethodWithAnnotation(clazz, instance, PostConstruct.class);
    }

    static void destroy(Object instance) {
        Class<? extends Object> clazz = instance.getClass();
        invokeMethodWithAnnotation(clazz, instance, PreDestroy.class);
    }

    static void invokeMethodWithAnnotation(Class clazz, final Object instance, final Class<? extends Annotation> annotationClass) throws IllegalStateException, SecurityException {
        Method[] declaredMethods = clazz.getDeclaredMethods();
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
        Class superclass = clazz.getSuperclass();
        if (superclass != null) {
            invokeMethodWithAnnotation(superclass, instance, annotationClass);
        }
    }

    public static void forgetAll() {
        Collection<Object> values = modelsAndServices.values();
        for (Object object : values) {
            destroy(object);
        }
        for (Object object : presenters) {
            destroy(object);
        }
        presenters.clear();
        modelsAndServices.clear();
    }
}
