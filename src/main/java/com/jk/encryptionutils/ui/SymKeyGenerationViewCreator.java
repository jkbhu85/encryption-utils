package com.jk.encryptionutils.ui;

import static com.jk.encryptionutils.Constants.HALF_UNIT;
import static com.jk.encryptionutils.Constants.UNIT;

import java.util.Base64;
import java.util.Optional;

import com.jk.encryptionutils.Constants;
import com.jk.encryptionutils.Utils;
import com.jk.encryptionutils.crypt.SymEncryptionKeyService;
import com.jk.encryptionutils.crypt.SymEncryptionKeyService.SymmetricKey;
import com.jk.encryptionutils.crypt.SymEncryptionKeyService.SymmetricKeyRequest;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public final class SymKeyGenerationViewCreator implements ViewCreator {

	@Override
	public Pane createView() {
		ObservableList<Integer> list = FXCollections.observableArrayList(256, 512, 1024, 2048);
		ChoiceBox<Integer> keyLengthChoicBox = new ChoiceBox<>(list);
		keyLengthChoicBox.setValue(list.get(0));

		GridPane gridPane = new GridPane();
		gridPane.setHgap(UNIT);
		gridPane.setVgap(HALF_UNIT);
		gridPane.add(new Label("Length of the key"), 0, 0);
		gridPane.add(keyLengthChoicBox, 1, 0);

		Button btn = new Button();
		btn.setText("Generate Symmetric Key");
		btn.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> onClickGenKeyAndIv(keyLengthChoicBox));

		Pane titlePane = Utils.getTitle("Generate Key");
		VBox vbox = new VBox(titlePane, gridPane, btn);
		vbox.setSpacing(UNIT);

		Pane parent = Utils.rightPaneWrapper();
		parent.getChildren().add(vbox);
		return parent;
	}

	public static void onClickGenKeyAndIv(ChoiceBox<Integer> keyLengthChoicBox) {
		boolean error = false;
		String keyAndIv = null;
		String errorMsg = null;

		try {
			keyAndIv = getKeyAndIv(keyLengthChoicBox);
		}
		catch (Exception e) {
			error = true;
			errorMsg = e.getMessage() == null ? "Error occurred while generating key." : e.getMessage();
		}

		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle(error ? "Error occurred" : "Key and IV");
		if (!error) {
			ButtonType copyBtn = new ButtonType("Copy to Clipboard", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().add(copyBtn);
			dialog.setContentText(keyAndIv);
		}
		else {
			dialog.setContentText(errorMsg);
		}

		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
			Utils.copyToClipboard(keyAndIv);
		}
	}

	private static String getKeyAndIv(ChoiceBox<Integer> keyLengthChoicBox) {
		SymmetricKeyRequest symKeyReq = SymmetricKeyRequest.builder()
				.method(Constants.SYMMETRIC_ENCRYPTION_ALGRORITHM)
				.keySize(keyLengthChoicBox.getValue())
				.numberOfIvBytes(Constants.SYMMETRIC_ENCRYPTION_NUMBER_OF_VI_BYTES)
				.build();

		SymmetricKey symKey = new SymEncryptionKeyService().createNewKey(symKeyReq);
		String key = Base64.getEncoder().encodeToString(symKey.getSecretKey().getEncoded());
		String iv = Base64.getEncoder().encodeToString(symKey.getIv());
		return key + "::" + iv;
	}

	@Override
	public View viewId() {
		return View.SYMMETRIC_KEY_GENERATION;
	}

}
