package com.jk.encryptionutils;

import java.util.Base64;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;

import com.jk.encryptionutils.crypt.SymEncryptionService;
import com.jk.encryptionutils.crypt.SymEncryptionKeyService.SymmetricKey;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SymDecryption {

	public static Pane createSymDecryptView() {
		Label methodName = new Label("Algorithm: " + Constants.SYMMETRIC_ENCRYPTION_ALGORITHM);
		HBox algoInfoBox = new HBox(methodName);

		Label keyLabel = new Label("Key and IV");
		TextField textField = new TextField();
		Node keyIvFormCtrl = Utils.vertFormControl(keyLabel, textField);

		Label textLabel = new Label("Text to decrypt");
		TextField textArea = new TextField();
		Node textFormCtrl = Utils.vertFormControl(textLabel, textArea);

		Label errLabel = new Label();
		errLabel.setTextFill(Color.RED);

		Button submitBtn = new Button("Decrypt Text");
		EventHandler<MouseEvent> handler = (e) -> handleDecryptClick(textField, textArea, errLabel);
		submitBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, handler);

		Label title = Utils.getTitle("Decrypt");
		VBox vbox = new VBox(title, new Separator(), algoInfoBox, keyIvFormCtrl, textFormCtrl, errLabel, submitBtn);
		vbox.setSpacing(10);
		vbox.setMaxWidth(Double.MAX_VALUE);
		return vbox;
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
			errMsg = "Enter some text to decrypt.";
		}
		else {
			errMsg = "";
		}
		errLabel.setText(errMsg);
		return ("".equals(errMsg));
	}

	private static String decryptText(String keyAndIv, String encryptedText) throws Exception {
//		System.out.println("Decrypt text called");
		String[] parts = keyAndIv.trim().split("::");
		byte[] encodedKey = Base64.getDecoder().decode(parts[0].getBytes());
		byte[] iv = Base64.getDecoder().decode(parts[1]);
		SecretKeySpec secretKeySpec = new SecretKeySpec(encodedKey, Constants.SYMMETRIC_ENCRYPTION_METHOD);
		SymmetricKey symKey = new SymmetricKey(secretKeySpec, iv);

		SymEncryptionService encryptionService = new SymEncryptionService(Constants.SYMMETRIC_ENCRYPTION_ALGORITHM,
				symKey);
		byte[] encryptedData = Base64.getDecoder().decode(encryptedText);
		byte[] decrypted = encryptionService.decrypt(encryptedData);
		return new String(decrypted);
	}

	private static void handleDecryptClick(TextField textField, TextField textArea, Label errLabel) {
		try {
			if (validateInput(textField.getText(), textArea.getText(), errLabel)) {
				String decryptedText;
				boolean error = false;
				try {
					decryptedText = decryptText(textField.getText(), textArea.getText());
				}
				catch (Exception e) {
					System.out.println("exception in handleDecryptClick");
					e.printStackTrace();
					error = true;
					decryptedText = e.getMessage() == null ? "Error occurred while decrypting text." : e.getMessage();
				}
				showEncryptedText(decryptedText, error);
			}
		}
		catch (Throwable e) {
			System.out.println("uncaught in handleDecryptClick");
			e.printStackTrace();
		}
	}

	private static void showEncryptedText(String text, boolean error) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle(error ? "Error occurred" : "Decrypted text");
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

}
