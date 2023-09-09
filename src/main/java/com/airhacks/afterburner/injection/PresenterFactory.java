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

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public interface PresenterFactory {

    /**
     *
     * @return the discovered implementations of PresenterFactory using the
     * {@link java.util.ServiceLoader} mechanism
     */
    static PresenterFactory discover() {
        Iterable<PresenterFactory> discoveredFactories = ServiceLoader.load(PresenterFactory.class);
        List<PresenterFactory> factories = StreamSupport.stream(discoveredFactories.spliterator(), false)
                                                        .collect(Collectors.toList());
        if (factories.isEmpty()) {
            return new PresenterFactory() {
                @Override
                public <T> T instantiatePresenter(Class<T> clazz, Function<String, Object> injectionContext) {
                    return Injector.instantiatePresenter(clazz, injectionContext);
                }

                @Override
                public void injectMembers(Object controller, Function<String, Object> injectionContext) {
                    Injector.injectMembers(controller, injectionContext);
                }
            };
        }

        if (factories.size() == 1) {
            return factories.get(0);
        } else {
            Logger logger = LoggerFactory.getLogger(PresenterFactory.class);
            factories.forEach(factory -> logger.error(factory.toString()));
            throw new IllegalStateException("More than one PresenterFactories discovered");
        }
    }

    /**
     * This method method replaces the standard afterburner dependency
     * injection.
     *
     * @param <T>              the type of the presenter
     * @param clazz            presenter class containing the default constructor
     * @param injectionContext a cache of already instantiated and initialized instances
     * @return a fully initialized presenter with injected dependencies.
     */
    <T> T instantiatePresenter(Class<T> clazz, Function<String, Object> injectionContext);

    /**
     * Populate the given object.
     * For example, set all fields annotated with {@link jakarta.inject.Inject}.
     *
     * @param instance         the object to inject members into
     * @param injectionContext a cache of already instantiated and initialized instances
     */
    void injectMembers(Object instance, Function<String, Object> injectionContext);
}
