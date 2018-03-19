package com.airhacks.afterburner.demo.control;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import com.airhacks.afterburner.views.ViewLoader;

public class CustomTextField extends VBox {
    @FXML private TextField textField;

    public CustomTextField() {
        ViewLoader.view(this)
                  .root(this)
                  .load();
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
    }

    public StringProperty textProperty() {
        return textField.textProperty();
    }

    @FXML
    protected void doSomething() {
        setText("The button was clicked!");
    }
}
