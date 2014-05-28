/*
 * Copyright 2014 Adam Bien.
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
package com.airhacks.afterburner.configuration;

/*
 * #%L
 * afterburner.fx
 * %%
 * Copyright (C) 2013 - 2014 Adam Bien
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

/**
 *
 * @author airhacks.com
 */
public class Configurator {

    public final static String CONFIGURATION_FILE = "configuration.properties";

    private Function<Object, Object> defaultSupplier;
    private List<Function<Object, Object>> customConfigurators;

    public Configurator() {
        this.customConfigurators = new ArrayList<>();
        this.defaultSupplier = getSystemPropertiesSupplier();
    }

    public final Function<Object, Object> getSystemPropertiesSupplier() {
        return k -> System.getProperty(k.toString());
    }

    public Configurator add(Function<Object, Object> custom) {
        this.customConfigurators.add(custom);
        return this;
    }

    Properties getProperties(Class clazz) {
        Properties configuration = new Properties();
        try (InputStream stream = clazz.getResourceAsStream(CONFIGURATION_FILE)) {
            if (stream == null) {
                return null;
            }
            configuration.load(stream);
        } catch (IOException ex) {
            //a property file does not have to exist...
        }
        return configuration;
    }

    /**
     * Properties defined in a class-specific file have priority
     *
     * @param clazz
     * @param key
     * @return
     */
    public Object getProperty(Class clazz, Object key) {
        Properties clazzProperties = this.getProperties(clazz);
        Object value;
        if (clazzProperties != null) {
            value = clazzProperties.get(key);
            if (value != null) {
                return value;
            }
        } else {
            value = this.customConfigurators.stream().map(f -> f.apply(key)).findFirst().
                    orElse(this.defaultSupplier.apply(key));
        }
        return value;
    }

}
