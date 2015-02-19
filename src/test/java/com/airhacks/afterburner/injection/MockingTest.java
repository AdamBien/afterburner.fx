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
 *//*
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
package com.airhacks.afterburner.injection;

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
import com.airhacks.afterburner.topgun.GunService;
import com.airhacks.afterburner.topgun.TopgunPresenter;
import com.airhacks.afterburner.topgun.TopgunView;
import java.util.function.Function;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author adam-bien.com
 */
public class MockingTest {

    @Test
    public void mockIsActive() {
        Injector.setModelOrService(GunService.class, new GunService() {

            @Override
            public String fireAndForget() {
                return "don't worry, just a mock";
            }

        });
        TopgunView view = new TopgunView();
        TopgunPresenter cut = (TopgunPresenter) view.getPresenter();

        final String messageFromGun = cut.getMessageFromGun();
        Assert.assertNotNull(messageFromGun);
        System.out.println(messageFromGun);
        assertTrue(messageFromGun.startsWith("don't"));
    }

    @Test
    public void setMockViaInstanceSupplier() {
        Function<Class<?>, Object> provider = (t) -> {
            if (t.isAssignableFrom(GunService.class)) {
                return Mockito.mock(t);
            } else {
                try {
                    return t.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    throw new IllegalStateException("Cannot create instance: " + t, ex);
                }
            }
        };
        Injector.setInstanceSupplier(provider);
        TopgunView view = new TopgunView();
        TopgunPresenter cut = (TopgunPresenter) view.getPresenter();
        assertTrue(cut.getGunService().getClass().getName().contains("ByMockito"));

    }

    @After
    public void cleanup() {
        Injector.forgetAll();
    }

}
