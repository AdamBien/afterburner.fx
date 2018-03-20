package com.airhacks.afterburner.demo;

import javafx.fxml.FXML;

import com.airhacks.afterburner.demo.control.CustomTextField;
import com.airhacks.afterburner.demo.dialog.SimpleDialog;

public class MainController {
    @FXML private CustomTextField textField;

    public MainController() {
        System.out.println("Main controller constructed.");
    }

    @FXML
    private void openDialog() {
        SimpleDialog dialog = new SimpleDialog("Hello from main controller");
        dialog.showAndWait()
              .ifPresent(message -> textField.setText(message));
    }
}
