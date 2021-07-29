package com.jk.encryptionutils;

import static com.jk.encryptionutils.Constants.APP_TITLE;
import static com.jk.encryptionutils.Constants.HALF_UNIT;
import static com.jk.encryptionutils.Constants.PADDING_HALF_UNIT;
import static com.jk.encryptionutils.Constants.PADDING_UNIT_LEFT;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jk.encryptionutils.ui.AboutAppViewCreator;
import com.jk.encryptionutils.ui.AsymDecryptionViewCreator;
import com.jk.encryptionutils.ui.AsymEncryptionViewCreator;
import com.jk.encryptionutils.ui.AsymKeyGenerationViewCreator;
import com.jk.encryptionutils.ui.SymDecryptionViewCreator;
import com.jk.encryptionutils.ui.SymEncryptionViewCreator;
import com.jk.encryptionutils.ui.SymKeyGenerationViewCreator;
import com.jk.encryptionutils.ui.View;
import com.jk.encryptionutils.ui.ViewCreator;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class EncryptionUtilsApp extends Application {

	private static final Background BG_LIGHTGREY = new Background(new BackgroundFill(Color.LIGHTGREY, null, null));
	private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);
	private static final Map<View, Pane> ID_VIEW_MAP = new EnumMap<>(View.class);

	private final List<ViewCreator> viewCreators;
	private Pane rightPane;
	private View currentViewId;
	private double colWidth;
	private double leftWidth;
	private double rightWidth;
	private double appHeight;

	public EncryptionUtilsApp() {
		viewCreators = new ArrayList<>();
	}

	private void instantiateViewCreators(Stage primaryStage) {
		viewCreators.add(new AboutAppViewCreator(this));
		viewCreators.add(new SymEncryptionViewCreator());
		viewCreators.add(new SymDecryptionViewCreator());
		viewCreators.add(new SymKeyGenerationViewCreator());
		viewCreators.add(new AsymEncryptionViewCreator());
		viewCreators.add(new AsymDecryptionViewCreator());
		viewCreators.add(new AsymKeyGenerationViewCreator(primaryStage));
	}

	private static void onLabelClickHandler(MouseEvent event) {
		Label target = (Label) event.getSource();
		target.setBackground(BG_LIGHTGREY);
		EXECUTOR_SERVICE.schedule(() -> target.setBackground(null), 150, TimeUnit.MILLISECONDS);
	}

	private Label clickableLabel(String labelText, View viewId, boolean leftPad) {
		Label label = new Label(labelText);
		label.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> onLabelClickHandler(e));
		label.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> showView(viewId));
		label.setCursor(Cursor.HAND);
		label.setMaxWidth(Double.MAX_VALUE);
		if (leftPad) {
			label.setPadding(PADDING_UNIT_LEFT);
		}
		return label;
	}

	private Label clickableLabel(String labelText, View viewId) {
		return clickableLabel(labelText, viewId, true);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		computeWidths();
		instantiateViewCreators(primaryStage);

		HBox appUi = new HBox(createLeftMenu(), createRightPane());
		appUi.setMaxWidth(Double.MAX_VALUE);
		appUi.setPrefWidth(leftWidth + rightWidth);

		showView(View.SYMMETRIC_ENCRYPTION);

		Scene scene = new Scene(appUi);
		primaryStage.setScene(scene);
		primaryStage.setTitle(APP_TITLE);
		primaryStage.show();
		primaryStage.setMinWidth(leftWidth + rightWidth);
		primaryStage.setMinHeight(appHeight);
		primaryStage.getIcons().add(new Image(EncryptionUtilsApp.class.getResourceAsStream("/logo.png")));
	}

	private void computeWidths() {
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getBounds();
		colWidth = bounds.getWidth() / 12;
		leftWidth = 2 * colWidth;
		rightWidth = 6 * colWidth;
		appHeight = bounds.getHeight() * 0.75;
	}

	private ScrollPane createRightPane() {
		rightPane = new HBox();
		rightPane.setMaxWidth(Double.MAX_VALUE);
		rightPane.setPrefWidth(rightWidth);

		VBox rightPaneWrapper = new VBox(rightPane);
		rightPaneWrapper.setMaxWidth(Double.MAX_VALUE);
		rightPaneWrapper.setPrefWidth(rightWidth);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(rightPaneWrapper);
		scrollPane.setPrefWidth(rightWidth);
		return scrollPane;
	}

	private Pane createLeftMenu() {
		Label symLabel = clickableLabel("Symmetric", View.SYMMETRIC_ENCRYPTION, false);
		Label symEncrypt = clickableLabel("Encrypt", View.SYMMETRIC_ENCRYPTION);
		Label symDecrypt = clickableLabel("Decrypt", View.SYMMETRIC_DECRYPTION);
		Label symGenKey = clickableLabel("Generate key", View.SYMMETRIC_KEY_GENERATION);
		Label asymLabel = clickableLabel("Asymmetric", View.ASYMMETRIC_KEY_GENERATION, false);
		Label asymGenKey = clickableLabel("Generate key", View.ASYMMETRIC_KEY_GENERATION);
		Label aboutApp = clickableLabel("About", View.ABOUT_APP, false);

		VBox leftPane = new VBox(symLabel, symEncrypt, symDecrypt, symGenKey, asymLabel, asymGenKey, aboutApp);
		leftPane.setSpacing(HALF_UNIT);
		leftPane.setPadding(PADDING_HALF_UNIT);
		leftPane.setMinWidth(leftWidth);
		return leftPane;
	}

	@Override
	public void stop() throws Exception {
		EXECUTOR_SERVICE.shutdownNow();
		super.stop();
	}

	private void showView(View viewId) {
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

	private Pane createView(View viewId) {
		for (ViewCreator viewCreator : viewCreators) {
			if (viewCreator.viewId() == viewId) {
				return viewCreator.createView();
			}
		}
		return null;
	}

}
