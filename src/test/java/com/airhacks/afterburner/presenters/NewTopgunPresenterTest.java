package com.airhacks.afterburner.presenters;

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

import com.airhacks.afterburner.injection.Injector;
import com.airhacks.afterburner.topgun.TopgunPresenter;
import com.airhacks.afterburner.topgun.TopgunView;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author adam-bien.com
 */
public class NewTopgunPresenterTest {
    
    private NewTopgunPresenter presenter;

    @Before
    public void initialize() {
        PresenterFactory.setConventionalNameLowerCase(true);
        this.presenter = PresenterFactory.create(NewTopgunPresenter.class);
    }

    @Test
    public void loadViewWithConfiguration() {
        Assert.assertNotNull(presenter);
        String host = presenter.getHost();
        Assert.assertNotNull(host);
        final String expected = "tower"; //from configuration.properties
        assertThat(host, is(expected));
    }

    @Test
    public void loadResourceBundle() {
        ResourceBundle bundle = this.presenter.getResourceBundle();
        assertNotNull(bundle);
        String value = bundle.getString("top");
        //value is fetched from the topgun.properties file
        assertThat(value, is("gun"));
    }

    @Test
    public void systemPropertyOverridesConfigurationProperties() {
        final String expected = "ivory tower";
        System.setProperty("host", expected);
        TopgunView newView = new TopgunView();
        TopgunPresenter newPresenter = (TopgunPresenter) newView.getPresenter();
        String actual = newPresenter.getHost();
        Assert.assertNotNull(actual);
        assertThat(actual, is(expected));
    }

    @Test
    public void getEmbeddedPresenter() {
        TopgunHackPresenter topgunHackPresenter = this.presenter.getTopgunHackPresenter();
        assertNotNull(topgunHackPresenter);
        Parent root = topgunHackPresenter.getRoot();
        assertNotNull(root);
    }

    @Test
    public void getView() {
        Parent parent = this.presenter.getRoot();
        assertNotNull(parent);
    }

    @Test
    public void accessConventionalResourceBundle() {        
        ResourceBundle bundle = this.presenter.getResourceBundle();
        assertNotNull(bundle);
    }

    @After
    public void cleanUp() {
        Injector.forgetAll();
        System.clearProperty("host");
    }

}
