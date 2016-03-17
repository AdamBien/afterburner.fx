package com.airhacks.afterburner.views;

import javafx.fxml.Initializable;

/**
 * Created by pitt on 17.03.16.
 */
public abstract class ViewPresenter<T extends Initializable> extends FXMLView<T> {
    public static final String DEFAULT_ENDING = "ViewPresenter";
}
