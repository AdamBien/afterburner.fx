package com.airhacks.afterburner.injection;

import javax.inject.Inject;
import javax.inject.Named;

public class NamedInjection {
	@Inject
	@Named("java.version")
	private String javaVersion;
	
	@Inject
	@Named("user.home")
	private String userHome;

	public String getUserHome() {
		return userHome;
	}

	public String getJavaVersion() {
		return javaVersion;
	}
}
