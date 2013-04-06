package com.airhacks.afterburner.injection;

import javax.annotation.PostConstruct;

/**
 *
 * @author adam-bien.com
 */
public class InitializableProduct {

    private boolean initialized = false;

    @PostConstruct
    public void init() {
        this.initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
