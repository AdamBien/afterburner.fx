package com.airhacks.afterburner.injection.singleton;

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
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

/**
 * @author Mewes Kochheim
 */
public class InjectorSingletonTest {

    /**
     * This will test singleton and regular injection
     */
    @Test
    public void testInject() {
        InitializableEntity entity1 = new InitializableEntity();
        Injector.inject(entity1);

        InitializableEntity entity2 = new InitializableEntity();
        Injector.inject(entity2);

        assertSame(entity1.getServiceSingleton(), entity2.getServiceSingleton());
        assertNotSame(entity1.getService(), entity2.getService());
    }

    @After
    public void reset() {
        Injector.forgetAll();
    }
}
