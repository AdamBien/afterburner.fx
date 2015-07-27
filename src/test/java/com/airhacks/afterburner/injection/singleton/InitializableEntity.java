package com.airhacks.afterburner.injection.singleton;

import javax.inject.Inject;

/**
 * @author Mewes Kochheim
 */
public class InitializableEntity {

    @Inject
    private Service service;

    @Inject
    private ServiceSingleton serviceSingleton;

    public Service getService() {
        return service;
    }

    public ServiceSingleton getServiceSingleton() {
        return serviceSingleton;
    }
}
