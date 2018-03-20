package com.airhacks.afterburner.views;

import java.util.Optional;
import java.util.ResourceBundle;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.DialogPane;

public class ViewLoaderResult {
    private final Parent view;
    private final Object controller;
    private ResourceBundle bundle;

    public ViewLoaderResult(Parent view, Object controller, ResourceBundle bundle) {
        this.view = view;
        this.controller = controller;
        this.bundle = bundle;
    }

    /**
     * Scene Builder creates for each FXML document a root container. This
     * method omits the root container (e.g. AnchorPane) and gives you the
     * access to its first child.
     *
     * @return the first child of the AnchorPane
     */
    public Optional<Node> getViewWithoutRootContainer() {
        return getView().getChildrenUnmodifiable().stream().findFirst();
    }

    /**
     * @return an existing resource bundle, or null
     */
    public ResourceBundle getResourceBundle() {
        return bundle;
    }

    public Parent getView() {
        return view;
    }

    public Object getController() {
        return controller;
    }

    public void setAsContent(DialogPane dialogPane) {
        dialogPane.setContent(view);
    }
}
