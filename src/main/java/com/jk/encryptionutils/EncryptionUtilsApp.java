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
import com.jk.encryptionutils.ui.SymCryptionViewCreator;
import com.jk.encryptionutils.ui.SymKeyGenerationViewCreator;
import com.jk.encryptionutils.ui.ViewCreator;
import com.jk.encryptionutils.ui.ViewId;

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
	private static final Map<ViewId, Pane> ID_VIEW_MAP = new EnumMap<>(ViewId.class);

	private final List<ViewCreator> viewCreators;
	private Pane rightPane;
	private ViewId currentViewId;
	private double colWidth;
	private double leftWidth;
	private double rightWidth;
	private double appHeight;

	public EncryptionUtilsApp() {
		viewCreators = new ArrayList<>();
	}

	private void instantiateViewCreators(Stage primaryStage) {
		viewCreators.add(new AboutAppViewCreator(this));
		viewCreators.add(new SymCryptionViewCreator());
		viewCreators.add(new SymKeyGenerationViewCreator());
		viewCreators.add(new AsymEncryptionViewCreator(primaryStage));
		viewCreators.add(new AsymDecryptionViewCreator(primaryStage));
		viewCreators.add(new AsymKeyGenerationViewCreator(primaryStage));
	}

	private static void onLabelClickHandler(MouseEvent event) {
		Label target = (Label) event.getSource();
		target.setBackground(BG_LIGHTGREY);
		EXECUTOR_SERVICE.schedule(() -> target.setBackground(null), 150, TimeUnit.MILLISECONDS);
	}

	private Label clickableLabel(String labelText, ViewId viewId, boolean leftPad) {
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

	private Label clickableLabel(String labelText, ViewId viewId) {
		return clickableLabel(labelText, viewId, true);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		computeWidths();
		instantiateViewCreators(primaryStage);

		HBox appUi = new HBox(createLeftMenu(), createRightPane());
		appUi.setMaxWidth(Double.MAX_VALUE);
		appUi.setPrefWidth(leftWidth + rightWidth);

		showView(ViewId.SYMMETRIC_CRYPTION);

		Scene scene = new Scene(appUi);
		scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle(APP_TITLE);
		primaryStage.show();
		primaryStage.setMinWidth(leftWidth + rightWidth);
		primaryStage.setMinHeight(appHeight);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
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
		Label asymLabel = clickableLabel("Asymmetric", ViewId.ASYMMETRIC_ENCRYPTION, false);
		Label asymEncrypt = clickableLabel("Encryption", ViewId.ASYMMETRIC_ENCRYPTION);
		Label asymDecrypt = clickableLabel("Decryption", ViewId.ASYMMETRIC_DECRYPTION);
		Label asymGenKey = clickableLabel("Generate key", ViewId.ASYMMETRIC_KEY_GENERATION);
		Label symLabel = clickableLabel("Symmetric", ViewId.SYMMETRIC_CRYPTION, false);
		Label symDecrypt = clickableLabel("Cryption", ViewId.SYMMETRIC_CRYPTION);
		Label symGenKey = clickableLabel("Generate key", ViewId.SYMMETRIC_KEY_GENERATION);
		Label aboutApp = clickableLabel("About", ViewId.ABOUT_APP, false);

		VBox leftPane = new VBox(
				asymLabel, asymEncrypt, asymDecrypt, asymGenKey,
				symLabel, symDecrypt, symGenKey,
				aboutApp);
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

	private void showView(ViewId viewId) {
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

	private Pane createView(ViewId viewId) {
		for (ViewCreator viewCreator : viewCreators) {
			if (viewCreator.viewId() == viewId) {
				return viewCreator.createView();
			}
		}
		return null;
	}

}
