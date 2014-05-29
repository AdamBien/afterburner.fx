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
import java.util.function.Function;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author adam-bien.com
 */
public class InjectorTest {

    @Test
    public void injection() {
        View view = (View) Injector.instantiatePresenter(View.class);
        Boundary boundary = view.getBoundary();
        assertNotNull(boundary);
        assertThat(boundary.getNumberOfInstances(), is(1));
        AnotherView another = (AnotherView) Injector.instantiatePresenter(AnotherView.class);
        assertThat(boundary.getNumberOfInstances(), is(1));
    }

    @Test
    public void perInstanceInitialization() {
        View first = (View) Injector.instantiatePresenter(View.class);
        View second = (View) Injector.instantiatePresenter(View.class);
        assertNotSame(first, second);
    }

    @Test
    public void forgetAllPresenters() {
        Presenter first = (Presenter) Injector.instantiatePresenter(Presenter.class);
        Injector.forgetAll();
        Presenter second = (Presenter) Injector.instantiatePresenter(Presenter.class);
        assertNotSame(first, second);
    }

    @Test
    public void forgetAllModels() {
        Model first = (Model) Injector.instantiateModelOrService(Model.class);
        Injector.forgetAll();
        Model second = (Model) Injector.instantiateModelOrService(Model.class);
        assertNotSame(first, second);
    }

    @Test
    public void setInstanceSupplier() {
        Function<Class, Object> provider = t -> Mockito.mock(t);
        Injector.setInstanceSupplier(provider);
        Object mock = Injector.instantiateModelOrService(Model.class);
        assertTrue(mock.getClass().getName().contains("ByMockito"));
        Injector.resetInstanceSupplier();
    }

    @Test
    public void productInitialization() {
        InitializableProduct product = (InitializableProduct) Injector.instantiatePresenter(InitializableProduct.class);
        assertTrue(product.isInitialized());
    }

    @Test
    public void existingPresenterInitialization() {
        InitializableProduct product = (InitializableProduct) Injector.registerExistingAndInject(new InitializableProduct());
        assertTrue(product.isInitialized());
    }

    @Test
    public void productDestruction() {
        DestructibleProduct product = (DestructibleProduct) Injector.instantiatePresenter(DestructibleProduct.class);
        assertFalse(product.isDestroyed());
        Injector.forgetAll();
        assertTrue(product.isDestroyed());
    }

    @Test
    public void systemPropertiesInjectionOfExistingProperty() {
        final String expected = "42";
        System.setProperty("shouldExist", expected);
        SystemProperties systemProperties = (SystemProperties) Injector.injectAndInitialize(new SystemProperties());
        String actual = systemProperties.getShouldExist();
        assertThat(actual, is(expected));
    }

    @Test
    public void systemPropertiesInjectionOfNotExistingProperty() {
        SystemProperties systemProperties = (SystemProperties) Injector.injectAndInitialize(new SystemProperties());
        String actual = systemProperties.getDoesNotExists();
        assertNull(actual);
    }

    @After
    public void reset() {
        Injector.forgetAll();
    }
}
