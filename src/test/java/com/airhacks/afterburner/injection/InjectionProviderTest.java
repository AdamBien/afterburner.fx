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
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.After;

/**
 *
 * @author adam-bien.com
 */
public class InjectionProviderTest {

    @Test
    public void injection() {
        View view = (View) InjectionProvider.instantiateAndInject(View.class);
        Boundary boundary = view.getBoundary();
        assertNotNull(boundary);
        assertThat(boundary.getNumberOfInstances(), is(1));
        AnotherView another = (AnotherView) InjectionProvider.instantiateAndInject(AnotherView.class);
        assertThat(boundary.getNumberOfInstances(), is(1));
    }

    @Test
    public void singletonInstantiation() {
        View first = (View) InjectionProvider.instantiateAndInject(View.class);
        View second = (View) InjectionProvider.instantiateAndInject(View.class);
        assertSame(first, second);
    }

    @Test
    public void productInitialization() {
        InitializableProduct product = (InitializableProduct) InjectionProvider.instantiateAndInject(InitializableProduct.class);
        assertTrue(product.isInitialized());
    }

    @Test
    public void productDestruction() {
        DestructibleProduct product = (DestructibleProduct) InjectionProvider.instantiateAndInject(DestructibleProduct.class);
        assertFalse(product.isDestroyed());
        InjectionProvider.forgetAll();
        assertTrue(product.isDestroyed());
    }

    @After
    public void reset() {
        InjectionProvider.forgetAll();
    }
}
