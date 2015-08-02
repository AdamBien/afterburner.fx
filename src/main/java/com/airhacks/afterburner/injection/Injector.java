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

import com.airhacks.afterburner.configuration.Configurator;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author adam-bien.com
 * @author Mewes Kochheim
 */
public class Injector {

    private static final Map<Class<?>, Object> singletons = new WeakHashMap<>();
    private static final Set<Object> presenters = Collections.newSetFromMap(new WeakHashMap<>());
    private static final Configurator configurator = new Configurator();
    private static Function<Class<?>, Object> instanceSupplier = getDefaultInstanceSupplier();
    private static Consumer<String> LOG = getDefaultLogger();

    // Public

    public static void forgetAll() {
        Collection<Object> values = singletons.values();
        values.stream().forEach(Injector::destroy);
        presenters.stream().forEach(Injector::destroy);
        presenters.clear();
        singletons.clear();
        resetInstanceSupplier();
        resetConfigurationSource();
    }

    public static Consumer<String> getDefaultLogger() {
        return l -> {};
    }

    public static <T> T initialize(final T instance) {
        invokeMethodWithAnnotation(instance.getClass(), instance, PostConstruct.class);
        return instance;
    }

    public static <T> T injectAndInitialize(final T instance, Function<String, Object> injectionContext) {
        return initialize(inject(instance, injectionContext));
    }

    public static <T> T injectAndInitialize(final T instance) {
        return injectAndInitialize(instance, f -> null);
    }

    public static <T> T inject(final T instance, Function<String, Object> injectionContext) throws SecurityException {
        injectMembers(instance.getClass(), instance, injectionContext);
        return instance;
    }

    public static <T> T inject(final T instance) throws SecurityException {
        return inject(instance, f -> null);
    }

    public static <T> T instantiate(Class<T> clazz, Function<String, Object> injectionContext) {
        Object instance = singletons.get(clazz);
        if (instance == null) {
            instance = injectAndInitialize(instanceSupplier.apply(clazz), injectionContext);
            if (clazz.isAnnotationPresent(Singleton.class)) {
                singletons.putIfAbsent(clazz, instance);
            }
        } else {
            // Apply new injection context which may overrides previous contexts
            Field[] fields = clazz.getDeclaredFields();
            for (final Field field : fields) {
                if (field.isAnnotationPresent(Inject.class)) {
                    final String fieldName = field.getName();
                    final Object value = injectionContext.apply(fieldName);
                    if (value != null) {
                        injectIntoField(field, instance, value);
                    }
                }
            }
        }
        return clazz.cast(instance);
    }

    public static <T> T instantiate(Class<T> clazz) {
        return instantiate(clazz, f -> null);
    }

    public static <T> T instantiatePresenter(Class<T> clazz, Function<String, Object> injectionContext) {
        @SuppressWarnings("unchecked")
        T presenter = injectAndInitialize((T) instanceSupplier.apply(clazz), injectionContext);
        presenters.add(presenter);
        return presenter;
    }

    public static <T> T instantiatePresenter(Class<T> clazz) {
        return instantiatePresenter(clazz, f -> null);
    }

    public static void resetConfigurationSource() {
        configurator.forgetAll();
    }

    public static void resetInstanceSupplier() {
        instanceSupplier = getDefaultInstanceSupplier();
    }

    public static void setConfigurationSource(Function<Object, Object> configurationSupplier) {
        configurator.set(configurationSupplier);
    }

    public static void setInstanceSupplier(Function<Class<?>, Object> instanceSupplier) {
        Injector.instanceSupplier = instanceSupplier;
    }

    public static void setLogger(Consumer<String> logger) {
        LOG = logger;
    }

    public static <T> void setModelOrService(Class<T> clazz, T instance) {
        singletons.put(clazz, instance);
    }

    // Package private

    static void destroy(Object instance) {
        invokeMethodWithAnnotation(instance.getClass(), instance, PreDestroy.class);
    }

    static Function<Class<?>, Object> getDefaultInstanceSupplier() {
        return (c) -> {
            try {
                return c.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new IllegalStateException("Cannot instantiate view: " + c, ex);
            }
        };
    }

    static void injectIntoField(final Field field, final Object instance, final Object target) {
        AccessController.doPrivileged((PrivilegedAction<?>) () -> {
            boolean wasAccessible = field.isAccessible();
            try {
                field.setAccessible(true);
                field.set(instance, target);
                return null; // return nothing...
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new IllegalStateException("Cannot set field: " + field + " with value " + target, ex);
            } finally {
                field.setAccessible(wasAccessible);
            }
        });
    }

    static void injectMembers(Class<?> clazz, final Object instance, Function<String, Object> injectionContext) throws SecurityException {
        LOG.accept("Injecting members for class " + clazz + " and instance " + instance);
        Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                LOG.accept("Field annotated with @Inject found: " + field);
                Class<?> type = field.getType();
                String key = field.getName();

                // Try injection context
                Object value = injectionContext.apply(field.getName());

                // Try configurator
                if (value == null) {
                    value = configurator.getProperty(clazz, key);
                }

                // Try instantiating
                if (value == null && isInstantiatable(type)) {
                    value = instantiate(type, injectionContext);
                }

                // Inject value
                if (value != null) {
                    injectIntoField(field, instance, value);
                }
            }
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            LOG.accept("Injecting members of: " + superclass);
            injectMembers(superclass, instance, injectionContext);
        }
    }

    static void invokeMethodWithAnnotation(Class<?> clazz, final Object instance, final Class<? extends Annotation> annotationClass) throws IllegalStateException, SecurityException {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (final Method method : declaredMethods) {
            if (method.isAnnotationPresent(annotationClass)) {
                AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                    boolean wasAccessible = method.isAccessible();
                    try {
                        method.setAccessible(true);
                        return method.invoke(instance);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        throw new IllegalStateException("Problem invoking " + annotationClass + " : " + method, ex);
                    } finally {
                        method.setAccessible(wasAccessible);
                    }
                });
            }
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            invokeMethodWithAnnotation(superclass, instance, annotationClass);
        }
    }

    static boolean isInstantiatable(Class<?> type) {
        return !type.isPrimitive() && !type.isAssignableFrom(String.class);
    }
}
