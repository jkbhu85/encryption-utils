package com.jk.encryptionutils.ui;

import static com.jk.encryptionutils.Constants.HALF_UNIT;

import java.util.Base64;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;

import com.jk.encryptionutils.crypt.SymEncryptionKeyService.SymmetricKey;
import com.jk.encryptionutils.Constants;
import com.jk.encryptionutils.Utils;
import com.jk.encryptionutils.crypt.SymEncryptionService;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public final class SymEncryptionViewCreator implements ViewCreator {

	@Override
	public Pane createView() {
		Label methodName = new Label("Algorithm: " + Constants.SYMMETRIC_ENCRYPTION_TRANSFORMATION);
		HBox algoInfoBox = new HBox(methodName);

		Label keyLabel = new Label("Key and IV");
		TextField textField = new TextField();
		Node keyIvFormCtrl = Utils.vertFormControl(keyLabel, textField);

		Label textLabel = new Label("Text to encrypt");
		TextField textArea = new TextField();
		Node textFormCtrl = Utils.vertFormControl(textLabel, textArea);

		Label errLabel = new Label();
		errLabel.setTextFill(Color.RED);

		Button submitBtn = new Button("Encrypt Text");
		EventHandler<MouseEvent> handler = (e) -> handleEncryptClick(textField, textArea, errLabel);
		submitBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, handler);

		Pane titlePane = Utils.getTitle("Encrypt");
		VBox vbox = new VBox(titlePane, algoInfoBox, keyIvFormCtrl, textFormCtrl, errLabel, submitBtn);
		vbox.setSpacing(HALF_UNIT);

		Pane parent = Utils.rightPaneWrapper();
		parent.getChildren().add(vbox);
		return parent;
	}

	private static boolean validateInput(String keyAndIv, String text, Label errLabel) {
		String errMsg;

		if (keyAndIv.isEmpty()) {
			errMsg = "Key and IV field is required.";
		}
		else if (keyAndIv.indexOf("::") <= 0 || keyAndIv.indexOf("::") != keyAndIv.lastIndexOf("::")
				|| keyAndIv.indexOf("::") == keyAndIv.length() - 1) {
					errMsg = "Invalid Key and IV.";
				}
		else if (text.isEmpty()) {
			errMsg = "Enter some text to encrypt.";
		}
		else {
			errMsg = "";
		}
		errLabel.setText(errMsg);
		return ("".equals(errMsg));
	}

	private static String encryptText(String keyAndIv, String text) throws Exception {
		//		System.out.println("Encrypt text called");
		String[] parts = keyAndIv.trim().split("::");
		byte[] encodedKey = Base64.getDecoder().decode(parts[0].getBytes());
		byte[] iv = Base64.getDecoder().decode(parts[1]);
		SecretKeySpec secretKeySpec = new SecretKeySpec(encodedKey, Constants.SYMMETRIC_ENCRYPTION_ALGRORITHM);
		SymmetricKey symKey = new SymmetricKey(secretKeySpec, iv);

		SymEncryptionService encryptionService = new SymEncryptionService(Constants.SYMMETRIC_ENCRYPTION_TRANSFORMATION,
				symKey);
		byte[] encrypted = encryptionService.encrypt(text.getBytes());
		return Base64.getEncoder().encodeToString(encrypted);
	}

	private static void handleEncryptClick(TextField textField, TextField textArea, Label errLabel) {
		if (validateInput(textField.getText(), textArea.getText(), errLabel)) {
			String encryptedText;
			boolean error = false;
			try {
				encryptedText = encryptText(textField.getText(), textArea.getText());
			}
			catch (Exception e1) {
				error = true;
				encryptedText = e1.getMessage() == null ? "Error occurred while encrypting text." : e1.getMessage();
			}
			showEncryptedText(encryptedText, error);
		}
	}

	private static void showEncryptedText(String text, boolean error) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle(error ? "Error occurred" : "Encrypted text");
		dialog.setContentText(text);
		if (!error) {
			ButtonType copyBtn = new ButtonType("Copy to Clipboard", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().add(copyBtn);
		}

		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
			Utils.copyToClipboard(text);
		}
	}

	@Override
	public View viewId() {
		return View.SYMMETRIC_ENCRYPTION;
	}

}
