package com.airhacks.afterburner.views;

/*
 * #%L
 * afterburner.fx
 * %%
 * Copyright (C) 2015 Adam Bien
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
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;

import com.airhacks.afterburner.injection.PresenterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author adam-bien.com
 */
public class ViewLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewLoader.class);

    private static final String DEFAULT_ENDING = "View";
    private static final String CSS_FILE_ENDING = ".css";
    private static final String BSS_FILE_ENDING = ".bss";

    protected FXMLLoader fxmlLoader;
    protected String bundleName;
    protected URL resource;
    protected static Executor FX_PLATFORM_EXECUTOR = Platform::runLater;

    protected final static ExecutorService PARENT_CREATION_POOL = getExecutorService();
    protected final Class<?> clazz;

    private ViewLoader(Class<?> clazz) {
        this.clazz = clazz;
        this.resource = this.clazz.getResource(getFXMLName());
        this.bundleName = getBundleName();
        this.fxmlLoader = new FXMLLoader(resource);
        updateControllerFactory(f -> null);
    }

    /**
     * Initialized the {@link ViewLoader} to load the given view.
     * The fxml (or other resources) are not yet loaded.
     */
    public static ViewLoader view(Class<? extends Object> clazz) {
        return new ViewLoader(clazz);
    }

    /**
     * Initialized the {@link ViewLoader} to load the given view.
     * The given object is used as the controller.
     *
     * The fxml (or other resources) are not yet loaded.
     */
    public static ViewLoader view(Object root) {
        return view(root.getClass()).controller(root);
    }

    private static String stripEnding(String clazz) {
        if (!clazz.endsWith(DEFAULT_ENDING)) {
            return clazz;
        }
        int viewIndex = clazz.lastIndexOf(DEFAULT_ENDING);
        return clazz.substring(0, viewIndex);
    }

    private static ExecutorService getExecutorService() {
        return Executors.newCachedThreadPool((r) -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            String name = thread.getName();
            thread.setName("afterburner.fx-" + name);
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Sets the given class as controller.
     *
     *   We don't use {@link FXMLLoader#setController(Object)} since then the {@code fx:controller} attribute
     *         is no longer allowed in the fxml file and we loose IDE support.
     */
    public ViewLoader controller(Object root) {
        PresenterFactory factory = PresenterFactory.discover();
        fxmlLoader.setControllerFactory(type -> {
            if (type == root.getClass()) {
                factory.injectMembers(root, f -> null);
                return root;
            } else {
                try {
                    return factory.instantiatePresenter(type, f -> null);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return this;
    }

    /**
     * Specifies the injection source used to construct the controller.
     * Values matching for the keys are going to be used for injection into the corresponding controller.
     *
     * @param injectionContext the function used as a injection source.
     */
    public ViewLoader inject(Function<String, Object> injectionContext) {
        updateControllerFactory(injectionContext);
        return this;
    }

    /**
     * Sets the root of the object hierarchy. The value passed to this method
     * is used as the value of the {@code  <fx:root>} tag. This method
     * must be called prior to loading the document when using {@code  <fx:root>}.
     *
     * @param root the object used as the root
     */
    public ViewLoader root(Object root) {
        fxmlLoader.setRoot(root);
        return this;
    }

    private void updateControllerFactory(Function<String, Object> injectionContext) {
        PresenterFactory factory = PresenterFactory.discover();
        Callback<Class<?>, Object> controllerFactory = (Class<?> p) -> factory.instantiatePresenter(p, injectionContext);
        fxmlLoader.setControllerFactory(controllerFactory);
    }

    /**
     * Synchronously loads the FXML file and associated resources.
     *
     * @return a composite object giving access to the loaded root node and initialized controller
     * @throws IllegalStateException if an exception occurred during loading and parsing of the FXML file
     */
    public ViewLoaderResult load() throws IllegalStateException {
        ResourceLocator resourceLocator = ResourceLocator.discover();
        ResourceBundle bundle = resourceLocator.getResourceBundle(bundleName);
        fxmlLoader.setResources(bundle);

        try {
            fxmlLoader.load();
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot load " + bundleName, ex);
        }

        Parent parent = fxmlLoader.getRoot();
        addCSSIfAvailable(parent);

        return new ViewLoaderResult(parent, fxmlLoader.getController(), bundle);
    }

    /**
     * Synchronously loads the FXML file and associated resources and
     * invokes the created root node to the given consumer within the FX UI thread.
     *
     * @param consumer the callback that receives the loaded root node
     */
    public void load(Consumer<Parent> consumer) {
        CompletableFuture.completedFuture(load().getView())
                         .thenAcceptAsync(consumer, FX_PLATFORM_EXECUTOR)
                         .exceptionally(this::exceptionReporter);
    }

    /**
     * Asynchronously loads the FXML file and associated resources using an internal thread pool and
     * invokes the created root node to the given consumer within the FX UI thread.
     *
     * @param consumer the callback that receives the loaded root node
     */
    public void loadAsync(Consumer<Parent> consumer) {
        Supplier<Parent> supplier = () -> load().getView();
        CompletableFuture.supplyAsync(supplier, PARENT_CREATION_POOL)
                         .thenAcceptAsync(consumer, FX_PLATFORM_EXECUTOR)
                         .exceptionally(this::exceptionReporter);
    }

    private void addCSSIfAvailable(Parent parent) {
        URL uri = clazz.getResource(getBinaryStyleSheetName());
        if (uri == null) {
            uri = clazz.getResource(getStyleSheetName());
        }
        if (uri == null) {
            return;
        }
        String uriToCss = uri.toExternalForm();
        if (!parent.getStylesheets().contains(uriToCss)){
            parent.getStylesheets().add(uriToCss);
        }
    }

    private String getStyleSheetName() {
        return getResourceCamelOrLowerCase(false, CSS_FILE_ENDING);
    }

    /**
     * @param lowercase indicates whether the simple class name should be
     *                  converted to lowercase of left unchanged
     * @param ending    the suffix to append
     * @return the conventional name with stripped ending
     */
    protected String getConventionalName(boolean lowercase, String ending) {
        return getConventionalName(lowercase) + ending;
    }

    /**
     * .bss files are binary encoded css files which javafx produces.
     * @return the conventional name of the bss file expected.
     */
    private String getBinaryStyleSheetName() {
        return getResourceCamelOrLowerCase(false, BSS_FILE_ENDING);
    }

    /**
     *
     * @return the name of the fxml file derived from the FXML view. e.g. The
     * name for the AirhacksView is going to be airhacks.fxml.
     */
    private String getFXMLName() {
        return getResourceCamelOrLowerCase(true, ".fxml");
    }

    private String getResourceCamelOrLowerCase(boolean mandatory, String ending) {
        String name = getConventionalName(true, ending);
        URL found = clazz.getResource(name);
        if (found != null) {
            return name;
        }
        LOGGER.debug("File: " + name + " not found, attempting with camel case");
        name = getConventionalName(false, ending);
        found = clazz.getResource(name);
        if (mandatory && (found == null)) {
            final String message = "Cannot load file " + name;
            LOGGER.error(message);
            LOGGER.error("Stopping initialization phase...");
            throw new IllegalStateException(message);
        }
        return name;
    }

    /**
     *
     * @param lowercase indicates whether the simple class name should be
     * @return the name of the view without the "View" prefix.
     */
    protected String getConventionalName(boolean lowercase) {
        final String clazzWithEnding = clazz.getSimpleName();
        String clazz = stripEnding(clazzWithEnding);
        if (lowercase) {
            clazz = clazz.toLowerCase();
        }
        return clazz;
    }

    private String getBundleName() {
        String conventionalName = getConventionalName(true);
        return clazz.getPackage().getName() + "." + conventionalName;
    }

    /**
     *
     * @param t exception to report
     * @return nothing
     */
    public Void exceptionReporter(Throwable t) {
        LOGGER.error("Exception thrown in afterburner.fx", t);
        return null;
    }
}
