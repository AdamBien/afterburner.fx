package com.airhacks.afterburner.injection.weld;

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

import com.airhacks.afterburner.injection.weld.topgun.GunService;
import com.airhacks.afterburner.injection.weld.topgun.TopgunPresenter;
import com.airhacks.afterburner.injection.weld.topgun.TopgunView;
import javafx.scene.Parent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 *
 * @author adam-bien.com
 * @author Mewes Kochheim
 */
public class TopgunViewWeldTest {

    private TopgunView view;

    @Before
    public void initialize() {
        this.view = new TopgunView();
    }

    @Test
    public void loadResourceBundle() {
        ResourceBundle bundle = this.view.getResourceBundle();
        assertNotNull(bundle);
        String value = bundle.getString("top");
        //value is fetched from the topgun.properties file
        assertThat(value, is("gun"));
    }

    @Test
    public void getView() {
        Parent parent = this.view.getView();
        assertNotNull(parent);
    }

    @Test
    public void getPresenter() {
        Object object = this.view.getPresenter();
        assertNotNull(object);
    }

    @Test
    public void getGunService() {
        Object object = ((com.airhacks.afterburner.injection.weld.topgun.TopgunPresenter) this.view.getPresenter()).getGunService();
        assertTrue(GunService.class.isInstance(object));
    }

    @Test
    public void accessConventionalResourceBundle() {
        TopgunPresenter topPresenter = (TopgunPresenter) this.view.getPresenter();
        ResourceBundle bundle = topPresenter.getResourceBundle();
        assertNotNull(bundle);
    }

    @Test
    public void getPresengetViewWithoutRootContainerter() {
        Object object = this.view.getViewWithoutRootContainer();
        assertNotNull(object);
    }

    @Test
    public void singlePresenterPerView() {
        TopgunPresenter first = (TopgunPresenter) this.view.getPresenter();
        TopgunPresenter second = (TopgunPresenter) this.view.getPresenter();
        assertSame(first, second);
    }

    @After
    public void cleanUp() {
        System.clearProperty("host");
    }
}
