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
package com.airhacks.afterburner.lifecycle;

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

import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author adam-bien.com
 */
@Singleton
public class ConcreteModel extends AbstractModel {

    private final AtomicInteger initializationCounter = new AtomicInteger(0);
    private final AtomicInteger destructionCounter = new AtomicInteger(0);

    @Override
    public void init() {
        initializationCounter.incrementAndGet();
    }

    @Override
    public void destroy() {
        destructionCounter.incrementAndGet();
    }

    public int getInitializationCount() {
        return this.initializationCounter.get();
    }

    public int getDestructionCount() {
        return this.destructionCounter.get();
    }

}
