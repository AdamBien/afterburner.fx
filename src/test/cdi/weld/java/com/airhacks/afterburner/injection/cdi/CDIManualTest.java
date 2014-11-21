package com.airhacks.afterburner.injection.cdi;

/*
 * #%L
 * afterburner.fx
 * %%
 * Copyright (C) 2013 - 2014 Adam Bien
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
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airhacks.afterburner.injection.Injector;

public class CDIManualTest {
    private static Weld weld;
    private static WeldContainer container;
    
	@BeforeClass
    public static void initializeWeld() {
        weld = new Weld();
        container = weld.initialize();
    }
    
    @AfterClass
    public static void shutdownWeld() {
        weld.shutdown();
    }	

	@Before
	public void initCDIInAfterBurner() {
		Injector.setInstanceSupplier(new Injector.InstanceProvider() {
			@Override
			public boolean isInjectionAware() {
				// CDI realizes injections
				return true;
			}
			@Override
			public boolean isScopeAware() {
				// CDI knows about scopes
				return true;
			}
			@Override
			public Object instanciate(Class<?> c) {
				return container.instance().select(c).get();
			}
		});
	}
	
	@After
	public void resetCDITests() {
		Injector.forgetAll();
	}
	
	@Test
	public void checkCDIRespectsSingletonScope() {
		NormalCDIPresenter p = (NormalCDIPresenter) new NormalCDIView().getPresenter();
		
		// Let's as the DI container for some objects injected in the presenter
        Sun theSun = container.instance().select(Sun.class).get();
        Star aStar = container.instance().select(Star.class).get();

        // The sun is a Singleton and has to be unique
		assertThat(p.getTheSun(), is(theSun));
		
        // The sun is a Singleton and has to be unique
		assertThat(p.getaStar(), not(is(aStar)));
	}
}
