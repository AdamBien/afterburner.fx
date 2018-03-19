package com.airhacks.afterburner.demo;

import javafx.scene.Parent;
import javafx.scene.Scene;

import com.airhacks.afterburner.views.ViewLoader;

public class MainView extends Scene {
    public MainView() {
        super(loadContent());
    }

    private static Parent loadContent() {
        return ViewLoader.view(MainView.class)
                         .load()
                         .getView();
    }
}
