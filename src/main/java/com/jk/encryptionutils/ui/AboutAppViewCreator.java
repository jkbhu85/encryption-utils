package com.jk.encryptionutils.ui;

import static com.jk.encryptionutils.Constants.APP_TITLE;
import static com.jk.encryptionutils.Constants.HALF_UNIT;
import static com.jk.encryptionutils.Constants.UNIT;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import com.jk.encryptionutils.Utils;

import javafx.application.Application;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public final class AboutAppViewCreator implements ViewCreator {

	private final Application application;

	public AboutAppViewCreator(Application application) {
		this.application = application;
	}

	private void openInBrowser(String url) {
		application.getHostServices().showDocument(url);
	}

	@Override
	public Pane createView() {
		GridPane gridPane = new GridPane();
		gridPane.setHgap(UNIT);
		gridPane.setVgap(HALF_UNIT);

		int row = 0;
		gridPane.add(new Label("Version"), 0, row);
		gridPane.add(new Label(getAppVersion()), 1, row);

		row = 1;
		gridPane.add(new Label("Author"), 0, row);
		gridPane.add(new Label("Jitendra Kumar"), 1, row);

		row = 2;
		gridPane.add(new Label("Link"), 0, row);
		Hyperlink linkToApp = new Hyperlink("https://github.com/optimus29/encryption-utils");
		linkToApp.setOnAction(e -> openInBrowser(linkToApp.getText()));
		gridPane.add(linkToApp, 1, row);

		Pane titlePane = Utils.getTitle(APP_TITLE);
		VBox vbox = new VBox(titlePane, gridPane);
		vbox.setMaxWidth(Double.MAX_VALUE);
		vbox.setSpacing(UNIT);

		Pane parent = Utils.rightPaneWrapper();
		parent.getChildren().add(vbox);
		return parent;
	}

	private static String getAppVersion() {
		try {
			InputStream in = AboutAppViewCreator.class.getClassLoader()
					.getResourceAsStream("META-INF/maven/com.jk/encryption-utils/pom.properties");
			if (in == null) {
				throw new FileNotFoundException("File pom.properties not found.");
			}
			Properties props = new Properties();
			props.load(in);
			String version = props.getProperty("version");
			if (version != null && !version.isEmpty()) {
				return version;
			}
		}
		catch (Exception e) {
			System.err.println("Error occurred while finding application version. Error: " + e.getMessage());
		}
		return "Unknown";
	}

	@Override
	public ViewId viewId() {
		return ViewId.ABOUT_APP;
	}

}
