package com.airhacks.afterburner.injection.interfaces;

import javax.inject.Inject;

public class InterfacePresenter {
	@Inject
	private Ping p;
	
	public Ping getPing() {
		return p;
	}
}
