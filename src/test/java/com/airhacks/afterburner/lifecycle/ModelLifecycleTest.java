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
package com.airhacks.afterburner.lifecycle;

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
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author adam-bien.com
 */
public class ModelLifecycleTest {

    private ConcreteModel cut;

    @Before
    public void initialize() {
        final LifecycleView lifecycleView = new LifecycleView();
        LifecyclePresenter presenter = (LifecyclePresenter) lifecycleView.getPresenter();
        this.cut = presenter.concreteModel;
    }

    @Test
    public void abstractPostConstructCalled() {
        int initializationCount = this.cut.getInitializationCount();
        assertThat(initializationCount, is(1));
        int destructionCount = this.cut.getDestructionCount();
        assertThat(destructionCount, is(0));
        Injector.forgetAll();
        destructionCount = this.cut.getDestructionCount();
        assertThat(destructionCount, is(1));
    }

    @Test
    public void serviceInjectedInAbstractModel() {
        ConcreteService injectedService = this.cut.getServiceInjectedInSuperclass();
        assertNotNull(injectedService);
    }

    @After
    public void cleanUp() {
        Injector.forgetAll();
    }
}
