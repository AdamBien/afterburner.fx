/*
 * Copyright 2013 Adam Bien.
 *
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
 */
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * invokes a method in respect to Java Security Guidelines inside a
 * {@link PrivilegedAction}.
 *
 * This action is called via it's
 * {@link PrivilegedInvokeMethod#invoke(java.lang.reflect.Method, java.lang.Object, java.lang.Object[])}
 * method and is performend in a
 * {@link AccessController#doPrivileged(java.security.PrivilegedAction)} call.
 *
 * @author rhk
 */
public class PrivilegedInvokeMethod implements PrivilegedAction<Object> {

    private final Method method;
    private final Object instance;
    private final Object[] paramValues;

    private PrivilegedInvokeMethod(Method method, Object instance, Object... paramValues) {
        this.method = method;
        this.instance = instance;
        this.paramValues = paramValues;
    }

    @Override
    public Object run() {
        boolean wasAccessible = method.isAccessible();
        try {
            method.setAccessible(true);
            return method.invoke(instance, paramValues);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new IllegalStateException("Problem invoking : " + method, ex);
        } finally {
            method.setAccessible(wasAccessible);
        }
    }

    public static void invoke(Method method, Object instance, Object... paramValues) {
        AccessController.doPrivileged(new PrivilegedInvokeMethod(method, instance, paramValues));
    }
}
