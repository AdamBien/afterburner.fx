package com.airhacks.afterburner.injection;

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
