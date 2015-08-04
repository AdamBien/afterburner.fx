package com.airhacks.afterburner.injection.context;

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

import com.airhacks.afterburner.injection.Injector;
import com.airhacks.afterburner.injection.PresenterWithField;
import org.junit.After;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

/**
 * @author Mewes Kochheim
 */
public class InjectorContextTest {

    /**
     * This will test inherited injection contexts
     */
    @Test
    public void testInject() {
        Service service = new Service();

        Map<String, Object> injectionContext = new HashMap<>();
        injectionContext.put("service", service);

        InitializableEntity entity = new InitializableEntity();
        Injector.inject(entity, injectionContext::get);

        assertSame(entity.getService(), service);
        assertSame(entity.getEntity().getService(), service);
    }

    @After
    public void reset() {
        Injector.forgetAll();
    }
}
