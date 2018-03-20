package com.airhacks.afterburner.demo.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import com.airhacks.afterburner.views.ViewLoader;

public class SimpleDialog extends Dialog<String> {

    @FXML private Label messageLabel;
    @FXML private TextField inputField;

    public SimpleDialog(String message) {
        this.setTitle("Hello message from a simple dialog");

        ViewLoader.view(this)
                  .load()
                  .setAsContent(this.getDialogPane());

        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        this.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return inputField.getText();
            } else {
                return null;
            }
        });

        messageLabel.setText(message);
    }
}
