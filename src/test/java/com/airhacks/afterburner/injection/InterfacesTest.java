package com.airhacks.afterburner.injection;

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

import org.junit.Test;

import com.airhacks.afterburner.injection.Injector;
import com.airhacks.afterburner.injection.interfaces.InterfacePresenter;
import com.airhacks.afterburner.injection.interfaces.InterfaceView;
import com.airhacks.afterburner.injection.interfaces.Ping;
import com.airhacks.afterburner.injection.interfaces.Pong;
import com.airhacks.afterburner.injection.interfaces.SimplePing;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class InterfacesTest {
	@Test
	public void canBuildAnInterfaceObjectDeclaredInServiceLoader() {
		Object builtObject = Injector.getDefaultInstanceSupplier().apply(Ping.class);
		
		assertThat(builtObject, notNullValue());
		assertThat(builtObject, instanceOf(SimplePing.class));
	}
	
	@Test (expected=IllegalStateException.class)
	public void failWhenNoInterfaceImplementationIsFound() {
		Injector.getDefaultInstanceSupplier().apply(Pong.class);
	}

	@Test
	public void canInjectAnInterfaceInAPresenter() {
		InterfaceView iv = new InterfaceView();
		InterfacePresenter p = (InterfacePresenter) iv.getPresenter();
		
		assertThat(p.getPing(), notNullValue());
		assertThat(p.getPing(), instanceOf(SimplePing.class));
	}
}
