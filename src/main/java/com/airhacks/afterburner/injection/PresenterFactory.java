/*
 * Copyright 2016 Adam Bien.
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
import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * The implementation of this interface is fully optional and only needed in
 * case you would like to replaced the default Dependency Injection mechanism.
 * <p>
 * Afterburner will discover the implementations of this interface using the
 * Java extension mechanism.
 * </p>
 * <p>
 * You will have to register the an implementation by placing a text file:
 * <code>META-INF/services/com.airhacks.afterburner.injection.PresenterFactory</code>
 * containing the fully qualified name of the implementation.
 * </p>
 *
 * @author airhacks.com
 */
@FunctionalInterface
public interface PresenterFactory {

    /**
     * This method method replaces the standard afterburner dependency
     * injection.
     *
     * @param <T> the type of the presenter
     * @param clazz presenter class containing the default constructor.
     * @param injectionContext a cache of already instantiated and initialized
     * instances.
     * @return a fully initialized presenter with injected dependencies.
     */
    <T> T instantiatePresenter(Class<T> clazz, Function<String, Object> injectionContext);

    /**
     *
     * @return all discovered implementations of PresenterFactory using the
     * {@link java.util.ServiceLoader} mechanism
     */
    static Iterable<PresenterFactory> discover() {
        return ServiceLoader.load(PresenterFactory.class);
    }

}
