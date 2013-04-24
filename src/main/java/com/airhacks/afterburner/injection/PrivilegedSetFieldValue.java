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

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * set's a field's value in respect to Java Security Guidelines inside a
 * {@link PrivilegedAction}.
 *
 * This action is called via it's
 * {@link PrivilegedSetFieldValue#setValue(java.lang.Object, java.lang.Object)}
 * method and is performend in a
 * {@link AccessController#doPrivileged(java.security.PrivilegedAction)} call.
 *
 * @author rhk
 */
public class PrivilegedSetFieldValue implements PrivilegedAction<Object> {

    private final Field field;
    private final Object instance;
    private final Object value;

    private PrivilegedSetFieldValue(Field field, Object instance, Object value) {
        this.field = field;
        this.instance = instance;
        this.value = value;
    }

    @Override
    public Object run() {
        boolean wasAccessible = field.isAccessible();
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException("Cannot set field: " + field, ex);
        } finally {
            field.setAccessible(wasAccessible);
        }
        return null;
    }

    public static void setValue(Field field, Object instance, Object value) {
        AccessController.doPrivileged(new PrivilegedSetFieldValue(field, instance, value));
    }
}
