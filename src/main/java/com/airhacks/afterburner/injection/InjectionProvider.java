package com.airhacks.afterburner.injection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
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

    public static void injectMembers(Object instance) {
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

    public static void initialize(Object instance) {
        Class<? extends Object> aClass = instance.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                try {
                    method.setAccessible(true);
                    method.invoke(instance, new Object[]{});
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw new IllegalStateException("Problem invoking @PostConstruct: " + method, ex);
                }
            }
        }
    }

    public static void forgetAll() {
        providers.clear();
    }
}
