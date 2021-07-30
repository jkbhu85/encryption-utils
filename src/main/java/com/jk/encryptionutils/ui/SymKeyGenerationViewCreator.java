package com.jk.encryptionutils.ui;

import static com.jk.encryptionutils.Constants.UNIT;

import java.util.Base64;

import javax.crypto.SecretKey;

import com.jk.encryptionutils.Constants;
import com.jk.encryptionutils.Utils;
import com.jk.encryptionutils.crypt.SymEncryptionKeyService;
import com.jk.encryptionutils.crypt.SymEncryptionKeyService.SymmetricKeyRequest;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public final class SymKeyGenerationViewCreator implements ViewCreator {

	private static final ObservableList<Integer> KEY_SIZE_OPTIONS = FXCollections.observableArrayList(
			256, 512, 1024, 2048);
	private final VBox notiArea = new VBox();
	private final SymEncryptionKeyService symEncryptionKeyService;
	private ChoiceBox<Integer> keyLengthChoicBox;
	private Button btnSubmit;

	public SymKeyGenerationViewCreator() {
		this.symEncryptionKeyService = new SymEncryptionKeyService();
	}

	@Override
	public Pane createView() {
		keyLengthChoicBox = new ChoiceBox<>(KEY_SIZE_OPTIONS);
		keyLengthChoicBox.setValue(KEY_SIZE_OPTIONS.get(0));

		GridPane gridPane = new GridPane();
		gridPane.setHgap(UNIT);
		gridPane.setVgap(UNIT);
		gridPane.add(new Label("Length of the key"), 0, 0);
		gridPane.add(keyLengthChoicBox, 1, 0);

		btnSubmit = new Button("Generate Symmetric Key");
		btnSubmit.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> generateKey());
		HBox buttonBar = new HBox(btnSubmit);
		buttonBar.setSpacing(UNIT);

		Pane titlePane = Utils.getTitle("Generate Symmetric Key");
		VBox vbox = new VBox(titlePane, gridPane, buttonBar);
		vbox.setSpacing(UNIT);

		Pane parent = Utils.rightPaneWrapper();
		parent.getChildren().add(vbox);
		return parent;
	}

	private void generateKey() {
		beforeProcessStart();
		startProcess();
		afterProcessEnd();
	}

	private void beforeProcessStart() {
		notiArea.getChildren().clear();
		btnSubmit.setDisable(true);
	}

	private void afterProcessEnd() {
		btnSubmit.setDisable(false);
	}

	private void startProcess() {
		String key;
		try {
			SymmetricKeyRequest symKeyReq = SymmetricKeyRequest.builder()
					.method(Constants.SYMMETRIC_ENCRYPTION_ALGRORITHM)
					.keySize(keyLengthChoicBox.getValue())
					.build();

			SecretKey secretKey = symEncryptionKeyService.createNewKey(symKeyReq);
			key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		}
		catch (Exception e) {
			showError("Generating key", e);
			return;
		}

		Utils.copyToClipboardDialog("Secret key (Base64)", key);
	}

	private void showError(String context, Exception e) {
		String errMsg = e.getMessage() != null
				? e.getMessage()
				: "Unknown error occurred.";
		notiArea.getChildren().add(Utils.getErrorLabel(context + ": " + errMsg));
	}

	@Override
	public ViewId viewId() {
		return ViewId.SYMMETRIC_KEY_GENERATION;
	}

}
