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
package com.airhacks.afterburner.injection;

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

import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author adam-bien.com
 */
public class SystemProperties {

    public final static String SYSTEM_PROPERTY_WITH_DOTS = "system.property.with.dots";
    
    @Inject
    private String shouldExist;

    @Inject
    private String doesNotExists;
    
    @Inject
    @Named(SYSTEM_PROPERTY_WITH_DOTS)
    private String systemPropertyWithDots;

    public String getShouldExist() {
        return shouldExist;
    }

    public String getDoesNotExists() {
        return doesNotExists;
    }
    
    public String getSystemPropertyWithDots() {
        return systemPropertyWithDots;
    }

}
