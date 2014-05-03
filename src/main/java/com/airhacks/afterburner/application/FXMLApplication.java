package com.airhacks.afterburner.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.airhacks.afterburner.injection.InjectionProvider;
import com.airhacks.afterburner.views.FXMLView;

/**
 * Base class for JavaFX applications. Initializes the {@link Scene}, and the primary {@link Stage}.
 * Provides several "hooks" so subclasses can do extra initialization if needed/wanted.
 * 
 * @author Dirk
 */
public abstract class FXMLApplication extends Application {

	public static final String DEFAULT_APP_CSS_FILENAME = "app.css";

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLView appView = createAppRootView();
		Scene primaryScene = createPrimaryScene(appView);

		initializePrimaryScene(primaryScene);
		initializePrimaryStage(primaryStage);

		primaryStage.setScene(primaryScene);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		InjectionProvider.forgetAll();
	}

	/**
	 * @return the title of the main window of the application.
	 */
	protected abstract String getTitle();

	/**
	 * @return a new {@link FXMLView}, which will be the main view of the application.
	 */
	protected abstract FXMLView createAppRootView();

	/**
	 * @return a new {@link Scene} with <code>appView</code> as it's root node.
	 */
	protected Scene createPrimaryScene(FXMLView appView) {
		return new Scene(appView.getView());
	}

	/**
	 * Adds the gobal CSS file to the scene. Called by {@link #start(Stage)} before initializing the
	 * primary {@link Stage}. Override this if you want to do more initialization.
	 */
	protected void initializePrimaryScene(Scene scene) {
		scene.getStylesheets().add(getAppCssPath());
	}

	/**
	 * Sets the title of the primary stage (and thus of the main window of the application). Called
	 * by {@link #start(Stage)} before setting the scene and showing the {@link Stage}. Override
	 * this if you want to do more initialization.
	 */
	protected void initializePrimaryStage(Stage primaryStage) {
		primaryStage.setTitle(getTitle());
	}

	/**
	 * Override this if you want to use a global CSS file which is not in the same package as your
	 * application class.
	 * 
	 * @return the absolute path to the file returned by {@link #getAppCssFileName()}.
	 */
	protected String getAppCssPath() {
		return getClass().getResource(getAppCssFileName()).toExternalForm();
	}

	/**
	 * Override this is you want to use a different filename than {@link #DEFAULT_APP_CSS_FILENAME}.
	 * 
	 * @return the filename of the global CSS file.
	 */
	protected String getAppCssFileName() {
		return DEFAULT_APP_CSS_FILENAME;
	}

}
