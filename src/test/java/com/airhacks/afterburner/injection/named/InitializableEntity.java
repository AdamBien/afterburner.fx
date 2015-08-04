package com.airhacks.afterburner.injection.named;

/*
 * #%L
 * afterburner.fx
 * %%
 * Copyright (C) 2013 - 2015 Adam Bien
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

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Mewes Kochheim
 */
public class InitializableEntity {

    /**
     * Injected by field name
     */
    @Inject
    private Service service;

    /**
     * Injected by name
     */
    @Inject
    @Named("a")
    private Service serviceA;

    /**
     * Injected by name
     */
    @Inject
    @Named("b")
    private Service serviceB;

    /**
     * Injected with new instance
     */
    @Inject
    private Service serviceNew;

    public Service getService() {
        return service;
    }

    public Service getServiceA() {
        return serviceA;
    }

    public Service getServiceB() {
        return serviceB;
    }

    public Service getServiceNew() {
        return serviceNew;
    }
}
