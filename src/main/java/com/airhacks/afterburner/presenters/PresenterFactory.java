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
package com.airhacks.afterburner.presenters;

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
import com.airhacks.afterburner.injection.Injector;
import static com.airhacks.afterburner.views.FXMLView.getResourceBundle;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.util.Callback;

/**
 *
 * @author Jeandeson O. Merelis
 */
public class PresenterFactory {

    /**
     * Load FXML file and inject members and call @PosConstruct methods.
     *
     * Inject members and method of booting only happen after work of the FXMLLoader. This way you can use fields annotated with @ FXML in PosConstruct methods.
     *
     * @param presenterInstance
     * @return
     */
    public static Object injectAndInitialize(Object presenterInstance) {
        ResourceBundle bundle = getResourceBundle(getBundleName(presenterInstance.getClass()));
        return create(presenterInstance.getClass(), (Class<?> param) -> presenterInstance, null, null, bundle);
    }

    /**
     * Creates a presenter.
     *
     * It is not necessary to implement the class FXMLView.
     * You need not specify a controller in fxml file, when this occurs, the presenter is created and fxml is loaded based on the
     * presenter.
     *
     * Inject members and method of booting only happen after work of the FXMLLoader. This way you can use fields annotated with @ FXML in PosConstruct methods
     *
     * @param <T>
     * @param controllerClass
     * @return
     */
    public static <T> T create(Class<T> controllerClass) {
        ResourceBundle bundle = getResourceBundle(getBundleName(controllerClass));
        return create(controllerClass, null, null, null, bundle);
    }

    /**
     * Creates a presenter.
     *
     * It is not necessary to implement the class FXMLView.
     * You need not specify a controller in fxml file, when this occurs, the presenter is created and fxml is loaded based on the
     * presenter.
     *
     * Inject members and method of booting only happen after work of the FXMLLoader. This way you can use fields annotated with @ FXML in PosConstruct methods
     *
     * @param <T>
     * @param controllerClass
     * @param controllerFactory
     * @return
     */
    public static <T> T create(Class<T> controllerClass, Callback<Class<?>, Object> controllerFactory) {
        ResourceBundle bundle = getResourceBundle(getBundleName(controllerClass));
        return create(controllerClass, controllerFactory, null, null, bundle);
    }

    /**
     * Creates a presenter.
     *
     * It is not necessary to implement the class FXMLView.
     * You need not specify a controller in fxml file, when this occurs, the presenter is created and fxml is loaded based on the
     * presenter.
     *
     * Inject members and method of booting only happen after work of the FXMLLoader. This way you can use fields annotated with @ FXML in PosConstruct methods
     *
     * @param <T>
     * @param controllerClass
     * @param controllerFactory
     * @param bundle
     * @return
     */
    public static <T> T create(Class<T> controllerClass, Callback<Class<?>, Object> controllerFactory,
            ResourceBundle bundle) {
        return create(controllerClass, controllerFactory, null, null, bundle);
    }

    /**
     * Creates a presenter. 
     *
     * It is not necessary to implement the class FXMLView.
     * You need not specify a controller in fxml file, when this occurs, the presenter is created and fxml is loaded based on the
     * presenter.
     *
     * Inject members and method of booting only happen after work of the FXMLLoader. This way you can use fields annotated with @ FXML in PosConstruct methods
     *
     * @param <T>
     * @param controllerClass
     * @param controllerFactory
     * @param fxmlResource
     * @param cssResource
     * @param bundle
     * @return
     */
    public static <T> T create(Class<T> controllerClass, Callback<Class<?>, Object> controllerFactory,
            final URL fxmlResource,
            final URL cssResource,
            ResourceBundle bundle) {
        String fxmlURI = "";
        String cssURI = "";
        if (controllerClass.isAnnotationPresent(Presenter.class)) {
            Presenter ann = (Presenter) controllerClass.getAnnotation(Presenter.class);
            fxmlURI = ann.fxmlURI();
            cssURI = ann.cssURI();
        }

        URL fxmlUrl;
        URL cssUrl;

        if (fxmlResource != null) {
            fxmlUrl = fxmlResource;
        } else {
            if ("".equals(fxmlURI)) {
                fxmlURI = getConventionalName(controllerClass, ".fxml");
            }
            fxmlUrl = controllerClass.getResource(fxmlURI);
        }

        if (cssResource != null) {
            cssUrl = cssResource;
        } else {
            if ("".equals(cssURI)) {
                cssURI = getConventionalName(controllerClass, ".css");
            }
            cssUrl = controllerClass.getResource(cssURI);
        }

        try {
            FXMLLoader loader;
            try {

                //controller from fxml. fx:controller
                loader = new FXMLLoader(fxmlUrl, bundle);

                if (controllerFactory == null) {
                    loader.setControllerFactory((Class<?> p) -> createObjectPresenter(p));
                } else {
                    loader.setControllerFactory(controllerFactory);
                }
                loader.load();
                if (loader.getController() == null) {
                    throw new LoadException("Controller cannot load");
                }
            } catch (LoadException ex) {
                //if no controller specified
                InputStream in;
                if (fxmlUrl == null) {
                  
                    in = new FileInputStream(new File(fxmlURI));
                } else {
                   
                    in = controllerClass.getResourceAsStream(fxmlURI);
                }

                if (in == null) {
                    throw ex;
                }

                loader = new FXMLLoader(null, bundle);

                Object o;
                if (controllerFactory == null) {
                    o = createObjectPresenter(controllerClass);
                } else {
                    o = controllerFactory.call(controllerClass);
                }

                loader.setController(o);
                try {
                    loader.load(in);
                } catch (Exception e) {
                    throw new IllegalStateException("Cannot load " + controllerClass, e);
                }
            } catch (Exception ex) {
                throw new IllegalStateException("Cannot load " + fxmlUrl, ex);
            }
            Parent root = loader.getRoot();
            if (cssUrl != null) {
                root.getStylesheets().add(cssUrl.toExternalForm());
            }

            //after inject JavaFX
            T controller = (T) Injector.registerExistingAndInject(loader.getController());
            return controller;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not create presenter", ex);
        }
    }

    private static Object createObjectPresenter(Class clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Cannot create presenter: " + clazz, ex);
        }
    }

    private static boolean conventionalNameLowerCase = false;

    public static boolean isConventionalNameLowerCase() {
        return conventionalNameLowerCase;
    }

    public static void setConventionalNameLowerCase(boolean conventionalNameLowerCase) {
        PresenterFactory.conventionalNameLowerCase = conventionalNameLowerCase;
    }

    static String getConventionalName(Class clazz, String ending) {
        String name;
        String c;
        String p;
        if (conventionalNameLowerCase) {
            name = clazz.getSimpleName().toLowerCase();
            c = "controller";
            p = "presenter";
        } else {
            name = clazz.getSimpleName();
            c = "Controller";
            p = "Presenter";
        }

        if (name.endsWith(c)) {
            int viewIndex = name.lastIndexOf(c);
            name = name.substring(0, viewIndex);
        } else if (name.endsWith(p)) {
            int viewIndex = name.lastIndexOf(p);
            name = name.substring(0, viewIndex);
        }
        return name + ending;
    }

    static String getBundleName(Class clazz) {
        String conventionalName = getConventionalName(clazz, "");
        return clazz.getPackage().getName() + "." +conventionalName;
    }
}
