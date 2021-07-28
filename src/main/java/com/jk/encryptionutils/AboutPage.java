package com.jk.encryptionutils;

import static com.jk.encryptionutils.Constants.APP_TITLE;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class AboutPage {

	public static Pane createAboutPage() {
		VBox titleBox = new VBox(Utils.getTitle(APP_TITLE), new Separator());

		GridPane gridPane = new GridPane();
		gridPane.setHgap(20);
		gridPane.setVgap(10);

		int row = 0;
		gridPane.add(new Label("Version"), 0, row);
		gridPane.add(new Label(getAppVersion()), 1, row);

		row = 1;
		gridPane.add(new Label("Author"), 0, row);
		gridPane.add(new Label("Jitendra Kumar"), 1, row);

		row = 2;
		gridPane.add(new Label("Link"), 0, 2);

		Hyperlink linkToApp = new Hyperlink("https://github.com/optimus29");
		linkToApp.setOnAction(e -> EncryptionUtilsApp.openInBrowser(linkToApp.getText()));
		gridPane.add(linkToApp, 1, row);

		VBox vbox = new VBox(titleBox, gridPane);
		vbox.setMaxWidth(Double.MAX_VALUE);
		vbox.setSpacing(20);
		return vbox;
	}

	private static String getAppVersion() {
		try {
			InputStream in = AboutPage.class.getClassLoader()
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

}
