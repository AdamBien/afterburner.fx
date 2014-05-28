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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

/**
 *
 * @author adam-bien.com
 */
public class InjectionProvider {

    private static final Map<Class, Object> modelsAndServices = new WeakHashMap<>();
    private static final Set<Object> presenters = Collections.newSetFromMap(new WeakHashMap<>());

    private static Function<Class, Object> instanceSupplier = getDefaultInstanceSupplier();

    private static final Configurator configurator = new Configurator();

    public static Object instantiatePresenter(Class clazz) {
        return registerExistingAndInject(instanceSupplier.apply(clazz));
    }

    public static void setInstanceSupplier(Function<Class, Object> instanceSupplier) {
        InjectionProvider.instanceSupplier = instanceSupplier;
    }

    public static void addConfigurator(Function<Object, Object> configurationSupplier) {
        configurator.add(configurationSupplier);
    }

    public static void resetInstanceSupplier() {
        instanceSupplier = getDefaultInstanceSupplier();
    }

    /**
     * Caches the passed presenter internally and injects all fields
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
            product = injectAndInitialize(instanceSupplier.apply(clazz));
            modelsAndServices.putIfAbsent(clazz, product);
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

    static void injectMembers(final Object instance) {
        Class<? extends Object> clazz = instance.getClass();
        injectMembers(clazz, instance);
    }

    public static void injectMembers(Class<? extends Object> clazz, final Object instance) throws SecurityException {
        Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                Class<?> type = field.getType();
                String key = field.getName();
                Object value = configurator.getProperty(clazz, key);
                if (value == null && !"java.lang".equals(type.getPackage().getName())) {
                    value = instantiateModelOrService(type);
                }
                injectIntoField(field, instance, value);
            }
        }
        Class<? extends Object> superclass = clazz.getSuperclass();
        if (superclass
                != null) {
            injectMembers(superclass, instance);
        }
    }

    static void injectIntoField(final Field field, final Object instance, final Object target) {
        AccessController.doPrivileged((PrivilegedAction) () -> {
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
        });
    }

    static void initialize(Object instance) {
        Class<? extends Object> clazz = instance.getClass();
        invokeMethodWithAnnotation(clazz, instance, PostConstruct.class
        );
    }

    static void destroy(Object instance) {
        Class<? extends Object> clazz = instance.getClass();
        invokeMethodWithAnnotation(clazz, instance, PreDestroy.class
        );
    }

    static void invokeMethodWithAnnotation(Class clazz, final Object instance, final Class<? extends Annotation> annotationClass) throws IllegalStateException, SecurityException {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (final Method method : declaredMethods) {
            if (method.isAnnotationPresent(annotationClass)) {
                AccessController.doPrivileged((PrivilegedAction) () -> {
                    boolean wasAccessible = method.isAccessible();
                    try {
                        method.setAccessible(true);
                        return method.invoke(instance, new Object[]{});
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        throw new IllegalStateException("Problem invoking " + annotationClass + " : " + method, ex);
                    } finally {
                        method.setAccessible(wasAccessible);
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
        values.stream().forEach((object) -> {
            destroy(object);
        });
        presenters.stream().forEach((object) -> {
            destroy(object);
        });
        presenters.clear();
        modelsAndServices.clear();
        resetInstanceSupplier();
    }

    static Function<Class, Object> getDefaultInstanceSupplier() {
        return (c) -> {
            try {
                return c.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new IllegalStateException("Cannot instantiate view: " + c, ex);
            }
        };
    }
}
