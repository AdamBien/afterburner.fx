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
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author adam-bien.com
 */
public class InjectionProviderTest {

    @Test
    public void injection() {
        View view = (View) InjectionProvider.instantiatePresenter(View.class);
        Boundary boundary = view.getBoundary();
        assertNotNull(boundary);
        assertThat(boundary.getNumberOfInstances(), is(1));
        AnotherView another = (AnotherView) InjectionProvider.instantiatePresenter(AnotherView.class);
        assertThat(boundary.getNumberOfInstances(), is(1));
    }

    @Test
    public void perInstanceInitialization() {
        View first = (View) InjectionProvider.instantiatePresenter(View.class);
        View second = (View) InjectionProvider.instantiatePresenter(View.class);
        assertNotSame(first, second);
    }

    @Test
    public void forgetAllPresenters() {
        Presenter first = (Presenter) InjectionProvider.instantiatePresenter(Presenter.class);
        InjectionProvider.forgetAll();
        Presenter second = (Presenter) InjectionProvider.instantiatePresenter(Presenter.class);
        assertNotSame(first, second);
    }

    @Test
    public void forgetAllModels() {
        Model first = (Model) InjectionProvider.instantiateModel(Model.class);
        InjectionProvider.forgetAll();
        Model second = (Model) InjectionProvider.instantiateModel(Model.class);
        assertNotSame(first, second);
    }

    @Test
    public void productInitialization() {
        InitializableProduct product = (InitializableProduct) InjectionProvider.instantiatePresenter(InitializableProduct.class);
        assertTrue(product.isInitialized());
    }

    @Test
    public void existingPresenterInitialization() {
        InitializableProduct product = (InitializableProduct) InjectionProvider.registerExistingAndInject(new InitializableProduct());
        assertTrue(product.isInitialized());
    }

    @Test
    public void productDestruction() {
        DestructibleProduct product = (DestructibleProduct) InjectionProvider.instantiatePresenter(DestructibleProduct.class);
        assertFalse(product.isDestroyed());
        InjectionProvider.forgetAll();
        assertTrue(product.isDestroyed());
    }

    @After
    public void reset() {
        InjectionProvider.forgetAll();
    }
}
