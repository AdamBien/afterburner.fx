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
package com.airhacks.afterburner.views;

import java.util.List;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The implementation of this interface is fully optional and only needed in
 * case you would like to replaced the default algorithm to find resources.
 * This may be useful if you have a global resource file with every translation key instead of a file per FXML view.
 * <p>
 * Afterburner will discover the implementations of this interface using the
 * Java extension mechanism.
 * </p>
 * <p>
 * You will have to register the an implementation by placing a text file:
 * <code>META-INF/services/com.airhacks.afterburner.views.ResourceLocator</code>
 * containing the fully qualified name of the implementation.
 * </p>
 *
 * @author airhacks.com
 */
@FunctionalInterface
public interface ResourceLocator {

    /**
     * Searches for specified implementations of {@link ResourceLocator} using the {@link ServiceLoader} mechanism.
     *
     * @return the discovered implementation or the default {@link DefaultResourceLocator}
     */
    static ResourceLocator discover() {
        Iterable<ResourceLocator> discoveredLocators = ServiceLoader.load(ResourceLocator.class);
        List<ResourceLocator> factories = StreamSupport.stream(discoveredLocators.spliterator(), false)
                                                       .collect(Collectors.toList());
        if (factories.isEmpty()) {
            return new DefaultResourceLocator();
        }

        if (factories.size() == 1) {
            return factories.get(0);
        } else {
            Logger logger = LoggerFactory.getLogger(ResourceLocator.class);
            factories.forEach(factory -> logger.error(factory.toString()));
            throw new IllegalStateException("More than one ResourceLocator discovered");
        }
    }

    /**
     * This method method replaces the standard afterburner dependency
     * injection.
     * @param  name The name of the bundle
     * @return a fully initialized presenter with injected dependencies.
     */
    ResourceBundle getResourceBundle(String name);
}
