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

import com.airhacks.afterburner.injection.InjectionProvider;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;

/**
 *
 * @author
 * adam-bien.com
 */
public abstract class FXMLView {

    public static final String DEFAULT_ENDING = "view";
    protected FXMLLoader loader;
    protected ResourceBundle resourceBundle;

    public FXMLView() {
        this.init(getClass(), getFXMLName());
    }

    public FXMLView(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.init(getClass(), getFXMLName());
    }

    private void init(Class clazz, String conventionalName) {
        final URL resource = clazz.getResource(conventionalName);
        this.loader = new FXMLLoader(resource);
        addResourceBundleIfAvailable();
        this.loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> p) {
                return InjectionProvider.instantiatePresenter(p);
            }
        });
        try {
            loader.load();
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot load " + conventionalName, ex);
        }
    }

    void addResourceBundleIfAvailable() {
        if (resourceBundle != null) {
            this.loader.setResources(resourceBundle);
        }
    }

    public Parent getView() {
        Parent parent = this.loader.getRoot();
        addCSSIfAvailable(parent);
        return parent;
    }

    void addCSSIfAvailable(Parent parent) {
        URL uri = getClass().getResource(getStyleSheetName());
        if (uri == null) {
            return;
        }
        String uriToCss = uri.toExternalForm();
        parent.getStylesheets().add(uriToCss);
    }

    String getStyleSheetName() {
        return getConventionalName(".css");
    }

    public Object getPresenter() {
        Object controller = this.loader.getController();
        return controller;
    }

    String getConventionalName(String ending) {
        String clazz = this.getClass().getSimpleName().toLowerCase();
        return stripEnding(clazz) + ending;
    }

    static String stripEnding(String clazz) {
        if (!clazz.endsWith(DEFAULT_ENDING)) {
            return clazz;
        }
        int viewIndex = clazz.lastIndexOf(DEFAULT_ENDING);
        return clazz.substring(0, viewIndex);
    }

    final String getFXMLName() {
        return getConventionalName(".fxml");
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    } 
}
