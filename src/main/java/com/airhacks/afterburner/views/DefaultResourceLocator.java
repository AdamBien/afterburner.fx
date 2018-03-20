package com.airhacks.afterburner.views;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DefaultResourceLocator implements ResourceLocator {
    @Override
    public ResourceBundle getResourceBundle(String name) {
        try {
            return ResourceBundle.getBundle(name);
        } catch (MissingResourceException ex) {
            return null;
        }
    }
}
