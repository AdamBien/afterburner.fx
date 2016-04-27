package com.airhacks.afterburner.injection;

/*
 * #%L
 * afterburner.fx
 * %%
 * Copyright (C) 2013 - 2016 Adam Bien
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

import java.util.function.Function;

/**
 *
 * @author airhacks.com
 */
public class DebuggingInjector implements PresenterFactory {

    @Override
    public <T> T instantiatePresenter(Class<T> clazz, Function<String, Object> injectionContext) {
        System.out.println("--- clazz " + clazz + " context " + injectionContext);
        return Injector.instantiatePresenter(clazz, injectionContext);
    }

}
