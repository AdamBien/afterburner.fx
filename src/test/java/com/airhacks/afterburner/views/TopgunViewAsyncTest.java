package com.airhacks.afterburner.views;

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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author adam-bien.com
 */
public class TopgunViewAsyncTest {

    private TopgunView view;
    private TopgunPresenter presenter;
    private Parent parent;

    @Before
    public void initialize() {
        new JFXPanel();
        this.view = new TopgunView();
    }

    @Test
    public void getViewAsync() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(1);
        this.view.getPresenter(this::setPresenter);
        this.view.getViewAsync(c -> {
            setParent(c);
            lock.countDown();
        });
        lock.await(2, TimeUnit.SECONDS);
        assertNotNull(presenter);
        assertNotNull(parent);
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public void setPresenter(Object presenter) {
        this.presenter = (TopgunPresenter) presenter;
    }

    @After
    public void cleanUp() {
        this.parent = null;
        this.presenter = null;
        Injector.forgetAll();
    }
}
