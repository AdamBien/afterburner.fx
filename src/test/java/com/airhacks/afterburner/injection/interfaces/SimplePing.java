package com.airhacks.afterburner.injection.interfaces;

public class SimplePing implements Ping {
	@Override
	public String ping() {
		return "ping: " + System.currentTimeMillis();
	}
}
