package com.jk.encryptionutils;

import static com.jk.encryptionutils.Constants.APP_TITLE;
import static com.jk.encryptionutils.Constants.SPACE_10;
import static com.jk.encryptionutils.Constants.SPACE_20_LEFT;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class EncryptionUtilsApp extends Application {

	private static final Background BG_LIGHTGREY = new Background(new BackgroundFill(Color.LIGHTGREY, null, null));
	private static final int VIEW_ID_SYM_ENCRYPTION = 1;
	private static final int VIEW_ID_SYM_DECRYPTION = 2;
	private static final int VIEW_ID_SYM_GEN_KEY = 3;
	private static final int VIEW_ID_ABOUT_APP = 4;

	private static final EventHandler<MouseEvent> EH_BG_CHANGE = (MouseEvent event) -> {
		onLabelClickHandler(event);
	};

	private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);
	private static Application thisApp;

	public EncryptionUtilsApp() {
		thisApp = this;
	}

	private static final Map<Integer, Pane> ID_VIEW_MAP = new HashMap<>();

	private Pane rightPane;
	private int currentViewId;

	private static void onLabelClickHandler(MouseEvent event) {
		Label target = (Label) event.getSource();
		target.setBackground(BG_LIGHTGREY);
		EXECUTOR_SERVICE.schedule(() -> target.setBackground(null), 150, TimeUnit.MILLISECONDS);
	}

	private Label clickableLabel(String labelText, int viewId, boolean leftPad) {
		Label label = new Label(labelText);
		label.addEventHandler(MouseEvent.MOUSE_CLICKED, EH_BG_CHANGE);
		label.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> showView(viewId));
		label.setCursor(Cursor.HAND);
		if (leftPad) {
			label.setPadding(SPACE_20_LEFT);
		}
		return label;
	}

	private Label clickableLabel(String labelText, int viewId) {
		return clickableLabel(labelText, viewId, true);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		rightPane = new HBox();
		rightPane.setMinWidth(500);
		rightPane.setMaxWidth(Double.MAX_VALUE);
		rightPane.setPadding(SPACE_10);

		Label symLabel = clickableLabel("Symmetric", VIEW_ID_SYM_ENCRYPTION, false);
		Label symEncrypt = clickableLabel("Encrypt", VIEW_ID_SYM_ENCRYPTION);
		Label symDecrypt = clickableLabel("Decrypt", VIEW_ID_SYM_DECRYPTION);
		Label symGenKey = clickableLabel("Generate key", VIEW_ID_SYM_GEN_KEY);
		Label aboutApp = clickableLabel("About", VIEW_ID_ABOUT_APP, false);

		VBox leftPane = new VBox(symLabel, symEncrypt, symDecrypt, symGenKey, aboutApp);
		leftPane.setSpacing(10);
		leftPane.setPadding(SPACE_10);
		leftPane.setMinWidth(150);

		Separator sep = new Separator();
		sep.setOrientation(Orientation.VERTICAL);

		HBox hbox = new HBox(leftPane, sep, rightPane);
		hbox.setPrefHeight(350);
		hbox.setMaxWidth(Double.MAX_VALUE);

		showView(VIEW_ID_SYM_ENCRYPTION);

		Scene scene = new Scene(hbox);
		primaryStage.setScene(scene);
		primaryStage.setTitle(APP_TITLE);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		EXECUTOR_SERVICE.shutdownNow();
		super.stop();
	}

	private void showView(int viewId) {
		if (currentViewId == viewId) {
			return;
		}
		currentViewId = viewId;
		if (!ID_VIEW_MAP.containsKey(viewId)) {
			Pane pane = createView(viewId);
			if (pane != null) {
				ID_VIEW_MAP.put(viewId, pane);
			}
		}
		if (ID_VIEW_MAP.containsKey(viewId)) {
			rightPane.getChildren().clear();
			rightPane.getChildren().add(ID_VIEW_MAP.get(viewId));
		}
	}

	private Pane createView(int viewId) {
		switch (viewId) {
		case VIEW_ID_SYM_ENCRYPTION:
			return SymEncryption.createSymEncryptView();
		case VIEW_ID_SYM_DECRYPTION:
			return SymDecryption.createSymDecryptView();
		case VIEW_ID_SYM_GEN_KEY:
			return SymIdGenerator.createSymGenKeyView();
		case VIEW_ID_ABOUT_APP:
			return AboutPage.createAboutPage();
		default:
			return null;
		}
	}

	public static void openInBrowser(final String url) {
		thisApp.getHostServices().showDocument(url);
	}

}
