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
import java.util.function.Function;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author airhacks.com
 */
public class ConfiguratorTest {

    Configurator cut;

    @Before
    public void init() {
        this.cut = new Configurator();
    }

    @Test
    public void customConfigurationOverridesDefault() {
        final String expected = "custom";
        Function<Object, Object> supplier = f -> expected;
        this.cut.set(supplier);
        Object actual = this.cut.getProperty(ConfiguratorTest.class, "NOT-A-EXISTING-SYSPROP");
        assertThat(actual, is(expected));
    }

    @Test
    public void loggerPassed() {
        this.cut.setLogger((l) -> System.out.println(l));
    }

    @Test
    public void systemPropertyWins() {
        final String expected = "custom";
        Function<Object, Object> customSupplier = f -> "something";
        Function<Object, Object> veryCustomSupplier = f -> expected;
        this.cut.set(customSupplier);
        this.cut.set(veryCustomSupplier);
        Object actual = this.cut.getProperty(ConfiguratorTest.class, "java.version");
        assertThat(actual, is(System.getProperty("java.version")));
    }

    @Test
    public void customConfigurationProvidesValue() {
        final String expected = "custom";
        Function<Object, Object> supplier = f -> expected;
        this.cut.set(supplier);
        Object actual = this.cut.getProperty(ConfiguratorTest.class, "NOT-EXISTING");
        assertThat(actual, is(expected));
    }

    @Test
    public void fetchNonExisting() {
        Object value = this.cut.getProperty(ConfiguratorTest.class, "SHOULD-NOT-EXIST");
        assertNull(value);
    }
}
