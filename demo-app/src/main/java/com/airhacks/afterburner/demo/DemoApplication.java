package com.airhacks.afterburner.demo;

import javafx.application.Application;
import javafx.stage.Stage;

public class DemoApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainView mainView = new MainView();
        primaryStage.setScene(mainView);
        primaryStage.setTitle("Demo Application");
        primaryStage.show();
    }
}
