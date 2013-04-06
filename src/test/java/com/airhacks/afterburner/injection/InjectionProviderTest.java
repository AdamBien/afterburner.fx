package com.airhacks.afterburner.injection;

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

    @After
    public void reset() {
        InjectionProvider.forgetAll();
    }
}
