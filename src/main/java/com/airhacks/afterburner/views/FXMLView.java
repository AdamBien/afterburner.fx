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
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Callback;

import com.airhacks.afterburner.injection.InjectionProvider;

import static java.util.ResourceBundle.getBundle;

/**
 * This class is the base class for an FXML view. It maps the name of the
 * derived class to a lowercase name and will automatically load the FXML file
 * from the same package.
 * 
 * e.g. com.airhacks.view.ConfigurationView.java ->
 * com/airhacks/view/configuration.fxml
 * 
 * For every view a corresponding Presenter should exists. The presenter is the
 * FX controller http://docs.oracle.com/javafx/2/api/javafx/fxml/doc-files/
 * introduction_to_fxml.html#controllers and must be set manually in the fxml
 * file (com/airhacks/view/configuration.fxml)
 * 
 * 
 * @author adam-bien.com, Manuel Blechschmidt <blechschmidt@apaxo.de>
 */
public abstract class FXMLView {

    /**
     * Logger for some debugging messages
     */
    private static final Logger log = Logger
            .getLogger(FXMLView.class.getName());
    /**
     * Default ending for a FXML view
     */
    public final static String DEFAULT_ENDING = "view";

    /**
     * The loader which is available by default and will be set in the init
     * method.
     */
    protected FXMLLoader fxmlLoader;

    /**
     * An optional ressource bundle for localization.
     * http://docs.oracle.com/javafx/scenebuilder/1/user_guide/i18n-support.htm
     */
    private ResourceBundle bundle;

    /**
     * Constructs a new XML view. This should be called automatically if no
     * constructore is defined by the derived class.
     */
    public FXMLView() {
        this.init(getClass(), getFXMLName());
    }

    /**
     * Inits the FXML view
     * 
     * @param clazz
     *            the class that should be use normally this.getClass()
     * @param conventionalName
     *            the name for the view
     */
    private void init(Class<?> clazz, final String conventionalName) {
        final URL resource = clazz.getResource(conventionalName);
        String bundleName = getBundleName();
        this.bundle = getResourceBundle(bundleName);
        this.fxmlLoader = loadSynchronously(resource, bundle, conventionalName);
    }

    /**
     * Loads the corresponding view and instantiate the presenter.
     * 
     * The presenter also gets processed by the InjectionProvider to inject the
     * necessary objects.
     * 
     * @param resource
     *            a resource url for loading a bundle
     * @param bundle
     *            the bundle name
     * @param conventionalName
     *            the name of the view
     * @return an FXMLoader that already loaded the view
     * @throws IllegalStateException
     */
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

    /**
     * Returns the root view of the given fxml file by default Scene Builder
     * will create an AnchorPane
     * 
     * @return
     */
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

    /**
     * If a css file is found it will be added to the view
     * 
     * @param parent
     */
    void addCSSIfAvailable(Parent parent) {
        String styleSheetName = getStyleSheetName();
        URL uri = getClass().getResource(styleSheetName);
        if (uri == null) {
            log.fine("Could not find " + styleSheetName);
            return;
        }
        String uriToCss = uri.toExternalForm();
        parent.getStylesheets().add(uriToCss);
    }

    /**
     * Get the stylesheet name based on the conventions. ConfigurationView.java
     * -> configuration.css
     * 
     * @return the name of the stylesheet
     */
    String getStyleSheetName() {
        return getConventionalName(".css");
    }

    /**
     * Gets the presenter for this view a.k.a JavaFX controller.
     * 
     * @return
     */
    public Object getPresenter() {
        return this.getLoader().getController();
    }

    /**
     * Generates a conventional name for the corresponding suffix.
     * 
     * @param ending
     *            the suffix to append e.g. .css or .fxml
     * @return
     */
    String getConventionalName(String ending) {
        return getConventionalName() + ending;
    }

    /**
     * Get the conventional name from the simple class name e.g.
     * ConfigurationView -> configuration
     * 
     * @return a string with the conventional name
     */
    String getConventionalName() {
        String clazz = this.getClass().getSimpleName().toLowerCase();
        return stripEnding(clazz);
    }

    /**
     * Gets the name of the bundle com.airhacks.view.ConfigurationView.java ->
     * com/airhacks/view/configuration.properties
     * 
     * The system will automatically add the current locale
     * (Locale.getDefault()) if the file is not found it will fallback to the
     * default
     * 
     * http://docs.oracle.com/javase/8/docs/api/java/util/ResourceBundle.html
     * 
     * @return
     */
    String getBundleName() {
        String conventionalName = getConventionalName();
        return this.getClass().getPackage().getName() + "." + conventionalName;
    }

    /**
     * The FXML will by default strip the View as ending
     * 
     * @param clazz
     * @return
     */
    static String stripEnding(String clazz) {
        if (!clazz.endsWith(DEFAULT_ENDING)) {
            return clazz;
        }
        int viewIndex = clazz.lastIndexOf(DEFAULT_ENDING);
        return clazz.substring(0, viewIndex);
    }

    /**
     * Return the name for the fxml file for this view.
     * 
     * e.g. e.g. com.airhacks.view.ConfigurationView.java ->
     * com/airhacks/view/configuration.fxml
     * 
     * @return
     */
    final String getFXMLName() {
        return getConventionalName(".fxml");
    }

    /**
     * Returns the resource bundle for the corresponding view.
     * 
     * @param name
     *            the name of the bundle (normally set by
     *            convention-over-configuration)
     * @return the resource bundle
     */
    public static ResourceBundle getResourceBundle(String name) {
        try {
            return getBundle(name);
        } catch (MissingResourceException ex) {
            log.fine("Was not able to find resource bundle: " + name);
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

    /**
     * Get the loader that already loaded the view and the presenter
     * 
     * @return the fxml loader
     */
    FXMLLoader getLoader() {
        return this.fxmlLoader;
    }
}
