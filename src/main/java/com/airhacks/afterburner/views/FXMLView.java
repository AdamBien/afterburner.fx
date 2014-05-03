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
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Callback;

/**
 * @author adam-bien.com
 */
public abstract class FXMLView {

    private static final Logger log = Logger
            .getLogger(FXMLView.class.getName());

    public final static String DEFAULT_ENDING = "view";
    protected FXMLLoader fxmlLoader;
    private static final ExecutorService THREAD_POOL = Executors
            .newCachedThreadPool();
    private ResourceBundle bundle;

    public FXMLView() {
        this.init(getClass(), getFXMLName());
    }

    private void init(Class clazz, final String conventionalName) {
        final URL resource = clazz.getResource(conventionalName);
        String bundleName = getBundleName();
        this.bundle = getResourceBundle(bundleName);
        this.fxmlLoader = loadSynchronously(resource, bundle, conventionalName);
    }

    FXMLLoader loadSynchronously(final URL resource, ResourceBundle bundle,
            final String conventionalName) throws IllegalStateException {
        final FXMLLoader loader = new FXMLLoader(resource, bundle);
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> p) {
                return InjectionProvider.instantiatePresenter(p);
            }
        });
        try {
            loader.load();
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot load " + conventionalName,
                    ex);
        }
        return loader;
    }

    public Parent getView() {
        Parent parent = getLoader().getRoot();
        addCSSIfAvailable(parent);
        return parent;
    }

    /**
     * Scene Builder creates for each FXML document a root container. This
     * method omits the root container (e.g. AnchorPane) and gives you the
     * access to its first child.
     * 
     * @return the first child of the AnchorPane
     */
    public Node getViewWithoutRootContainer() {
        final ObservableList<Node> children = getView()
                .getChildrenUnmodifiable();
        if (children.isEmpty()) {
            return null;
        }
        return children.listIterator().next();
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
        return this.getLoader().getController();
    }

    String getConventionalName(String ending) {
        return getConventionalName() + ending;
    }

    String getConventionalName() {
        String clazz = this.getClass().getSimpleName().toLowerCase();
        return stripEnding(clazz);
    }

    String getBundleName() {
        String conventionalName = getConventionalName();
        return this.getClass().getPackage().getName() + "." + conventionalName;
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

    public static ResourceBundle getResourceBundle(String name) {
        try {
            return getBundle(name);
        } catch (MissingResourceException ex) {
            return null;
        }
    }

    /**
     * 
     * @return an existing resource bundle, or null
     */
    public ResourceBundle getResourceBundle() {
        return this.bundle;
    }

    FXMLLoader getLoader() {
        return this.fxmlLoader;
    }

    /**
     * This static method returns a view for a corresponding presenter.
     * 
     * @param clazz
     *            the class we want to create a view for
     */
    public static FXMLView getViewForPresenterClass(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz for presenter is null");
        }
        if (!clazz.getSimpleName().matches(".*Presenter$")) {
            throw new IllegalArgumentException(
                    "Preseneter class name must end in Presenter but is: "
                            + clazz.getSimpleName());
        }
        String classNameForPresenter = clazz.getPackage().getName() + "."
                + clazz.getSimpleName().replaceAll("Presenter$", "View");
        try {
            return (FXMLView) Class.forName(classNameForPresenter)
                    .newInstance();
        } catch (InstantiationException e) {
            log.log(Level.WARNING, "Exception was thrown", e);
        } catch (IllegalAccessException e) {
            log.log(Level.WARNING, "Exception was thrown", e);
        } catch (ClassNotFoundException e) {
            log.log(Level.WARNING, "Presenter for view does not exists."
                    + classNameForPresenter, e);
        }
        return null;
    }
}
