package com.airhacks.afterburner.views;

import com.airhacks.afterburner.injection.InjectionProvider;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;

/**
 *
 * @author adam-bien.com
 */
public abstract class FXMLView {

    public static final String DEFAULT_ENDING = "view";
    protected FXMLLoader loader;

    public FXMLView() {
        this.init(getClass(), getFXMLName());
    }

    private void init(Class clazz, String conventionalName) {
        final URL resource = clazz.getResource(conventionalName);
        this.loader = new FXMLLoader(resource);
        this.loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> p) {
                return InjectionProvider.instantiateAndInject(p);
            }
        });
        try {
            loader.load();
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot load " + conventionalName, ex);
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
}
