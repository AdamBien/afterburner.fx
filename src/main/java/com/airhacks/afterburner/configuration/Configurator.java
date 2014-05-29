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
import java.util.Properties;
import java.util.function.Function;

/**
 *
 * @author airhacks.com
 */
public class Configurator {

    public final static String CONFIGURATION_FILE = "configuration.properties";

    private Properties systemProperties;
    private Function<Object, Object> customConfigurator;

    public Configurator() {
        this.systemProperties = System.getProperties();
    }

    public Configurator set(Function<Object, Object> custom) {
        this.customConfigurator = custom;
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
     *
     * @param clazz
     * @param key
     * @return
     */
    public Object getProperty(Class clazz, Object key) {
        Object value = this.systemProperties.get(key);
        if (value != null) {
            return value;
        }
        if (customConfigurator != null) {
            value = customConfigurator.apply(key);
            if (value != null) {
                return value;
            }
        }
        Properties clazzProperties = this.getProperties(clazz);
        if (clazzProperties != null) {
            value = clazzProperties.get(key);
        }
        return value;
    }

}
