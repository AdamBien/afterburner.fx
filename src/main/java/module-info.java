
open module afterburner.fx {

	exports com.airhacks.afterburner.injection;
	exports com.airhacks.afterburner.configuration;
	exports com.airhacks.afterburner.views;

	uses com.airhacks.afterburner.injection.PresenterFactory;
	uses com.airhacks.afterburner.views.ResourceLocator;

	requires jakarta.annotation;
	requires org.slf4j;
	requires jakarta.inject;
	requires transitive javafx.fxml;
	requires transitive javafx.controls;
}