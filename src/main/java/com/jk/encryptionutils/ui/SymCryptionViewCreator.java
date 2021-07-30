package com.jk.encryptionutils.ui;

import java.util.Base64;

import javax.crypto.SecretKey;

import com.jk.encryptionutils.AppContextException;
import com.jk.encryptionutils.Constants;
import com.jk.encryptionutils.Utils;
import com.jk.encryptionutils.crypt.SymEncryptionKeyService;
import com.jk.encryptionutils.crypt.SymEncryptionService;
import com.jk.encryptionutils.crypt.SymEncryptionService.CryptionReqeust;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public final class SymCryptionViewCreator implements ViewCreator {

	private final Pane notiArea = new VBox();
	private final ToggleGroup TG_CRYPT = new ToggleGroup();
	private final SymEncryptionKeyService symEncryptionKeyService;
	private final SymEncryptionService encryptionService;
	private TextArea keyInput;
	private TextArea nonceInput;
	private TextArea plainTextInput;
	private TextArea encryptedTextInput;
	private Button btnSubmit;
	private Button btnReset;
	private RadioButton rbEncrypt;
	private RadioButton rbDecrypt;

	public SymCryptionViewCreator() {
		this.symEncryptionKeyService = new SymEncryptionKeyService();
		this.encryptionService = new SymEncryptionService();
	}

	@Override
	public Pane createView() {
		Pane keyNonceGridPane = prepareKeyNonceGrid();
		Pane textGridPane = prepareTextGrid();
		Pane choiceFormCtrl = prepareOprationChoice();
		Pane buttonBar = prepareButtonBar();

		HBox algoInfoBox = new HBox(new Label("Algorithm: " + Constants.SYMMETRIC_ENCRYPTION_TRANSFORMATION));
		Pane titlePane = Utils.getTitle("Symmetric Encryption/Decryption");
		VBox vbox = new VBox(titlePane, algoInfoBox,
				keyNonceGridPane, new Separator(),
				textGridPane, new Separator(),
				choiceFormCtrl, new Separator(),
				notiArea, buttonBar);
		vbox.setSpacing(Constants.UNIT);

		reset();

		Pane parent = Utils.rightPaneWrapper();
		parent.getChildren().add(vbox);
		return parent;
	}

	private Pane prepareButtonBar() {
		btnSubmit = new Button("Perform Operation");
		btnSubmit.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> onDecrypt());
		btnReset = new Button("Reset");
		btnReset.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> reset());
		HBox buttonBar = new HBox(btnSubmit, btnReset);
		buttonBar.setSpacing(Constants.UNIT);
		return buttonBar;
	}

	private Pane prepareKeyNonceGrid() {
		keyInput = new TextArea();
		keyInput.setPrefRowCount(3);
		nonceInput = new TextArea();
		nonceInput.setPrefRowCount(3);

		Button btnGenerateNonce = new Button("Generate Nonce");
		btnGenerateNonce.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> onGenerateNonce());
		btnGenerateNonce.getStyleClass().add("btn-sm");

		Button btnCtcNonce = Utils.createCopyToClipboardBtn(nonceInput, "Copy nonce to clipboard");

		HBox buttonBar = new HBox(btnGenerateNonce, btnCtcNonce);
		buttonBar.setSpacing(Constants.UNIT);

		GridPane keyNonceGridPane = new GridPane();
		keyNonceGridPane.setVgap(Constants.UNIT);
		keyNonceGridPane.setHgap(Constants.UNIT);
		keyNonceGridPane.addRow(0, new Label("Secret key (Base64)"), new Label("Nonce or IV (Base64)"));
		keyNonceGridPane.addRow(1, keyInput, nonceInput);
		keyNonceGridPane.addRow(2, new Label(), buttonBar);
		return keyNonceGridPane;
	}

	private Pane prepareTextGrid() {
		plainTextInput = new TextArea();
		plainTextInput.setPrefRowCount(3);
		encryptedTextInput = new TextArea();
		encryptedTextInput.setPrefRowCount(3);

		Button btnCtcPlainText = Utils.createCopyToClipboardBtn(plainTextInput, "Copy plain text to clipboard");
		Button btnCtcEncryptedText = Utils.createCopyToClipboardBtn(encryptedTextInput,
				"Copy encrypted text to clipboard");

		GridPane textGridPane = new GridPane();
		textGridPane.setHgap(Constants.UNIT);
		textGridPane.setVgap(Constants.UNIT);
		textGridPane.addRow(0, new Label("Plain text"), new Label("Encrypted text (Base64)"));
		textGridPane.addRow(1, plainTextInput, encryptedTextInput);
		textGridPane.addRow(2, btnCtcPlainText, btnCtcEncryptedText);
		return textGridPane;
	}

	private Pane prepareOprationChoice() {
		rbEncrypt = new RadioButton("Encrypt");
		rbDecrypt = new RadioButton("Decrypt");
		HBox choiceBox = new HBox(rbEncrypt, rbDecrypt);
		choiceBox.setSpacing(Constants.UNIT);
		rbEncrypt.setOnAction((ae) -> onCryptChange());
		rbDecrypt.setOnAction((ae) -> onCryptChange());
		rbEncrypt.setToggleGroup(TG_CRYPT);
		rbDecrypt.setToggleGroup(TG_CRYPT);
		return Utils.vertFormControl(new Label("Select operation"), choiceBox);
	}

	private void onGenerateNonce() {
		byte[] nonce = symEncryptionKeyService.generateNonce(Constants.SYMMETRIC_ENCRYPTION_NUMBER_OF_VI_BYTES);
		String nonceBase64 = Base64.getEncoder().encodeToString(nonce);
		nonceInput.setText(nonceBase64);
	}

	private void onCryptChange() {
		encryptedTextInput.setText("");
		plainTextInput.setText("");
		if (rbEncrypt.isSelected()) {
			encryptedTextInput.setDisable(true);
			plainTextInput.setDisable(false);
		}
		if (rbDecrypt.isSelected()) {
			encryptedTextInput.setDisable(false);
			plainTextInput.setDisable(true);
		}
	}

	private String validate() {
		if (keyInput.getText() == null || keyInput.getText().isEmpty()) {
			return "Key can not be empty.";
		}
		if (nonceInput.getText() == null || nonceInput.getText().isEmpty()) {
			return "Nonce can not be empty.";
		}
		if (!rbEncrypt.isSelected() && !rbDecrypt.isSelected()) {
			return "Select operation encrypt/decrypt.";
		}
		if (rbEncrypt.isSelected() && (plainTextInput.getText() == null || plainTextInput.getText().isEmpty())) {
			return "Enter some text to encrypt.";
		}
		if (rbDecrypt.isSelected()
				&& (encryptedTextInput.getText() == null || encryptedTextInput.getText().isEmpty())) {
			return "Enter some text to decrypt.";
		}
		return "";
	}

	private void reset() {
		rbEncrypt.setSelected(true);
		onCryptChange();
		keyInput.setText("");
		nonceInput.setText("");
		plainTextInput.setText("");
		encryptedTextInput.setText("");
		notiArea.getChildren().clear();
	}

	private void onDecrypt() {
		beforeProcessStart();
		startProcess();
		afterProcessEnd();
	}

	private void beforeProcessStart() {
		notiArea.getChildren().clear();
		btnSubmit.setDisable(true);
		btnReset.setDisable(true);
	}

	private void afterProcessEnd() {
		btnSubmit.setDisable(false);
		btnReset.setDisable(false);
	}

	private void startProcess() {
		String error = validate();
		if (!error.isEmpty()) {
			notiArea.getChildren().add(Utils.getErrorLabel(error));
			return;
		}

		CryptionReqeust cryptionReqeust = createCryptionRequest(Constants.SYMMETRIC_ENCRYPTION_TRANSFORMATION);
		try {
			if (rbEncrypt.isSelected()) {
				byte[] encrypted = encryptionService.encrypt(cryptionReqeust);
				String base64 = Base64.getEncoder().encodeToString(encrypted);
				encryptedTextInput.setText(base64);
			}
			else {
				byte[] decrypted = encryptionService.decrypt(cryptionReqeust);
				String text = new String(decrypted);
				plainTextInput.setText(text);
			}
		}
		catch (AppContextException e) {
			showError(e.getMessage());
		}
	}

	private CryptionReqeust createCryptionRequest(String transformation) {
		try {
			return CryptionReqeust.builder()
					.transformation(transformation)
					.secretKey(getSecretKey())
					.nonce(getNonce())
					.data(getData())
					.build();
		}
		catch (Exception e) {
			showError(e.getMessage());
			return null;
		}
	}

	private SecretKey getSecretKey() throws AppContextException {
		try {
			return symEncryptionKeyService.getSecretKey(Constants.SYMMETRIC_ENCRYPTION_ALGRORITHM,
					Base64.getDecoder().decode(keyInput.getText().getBytes()));
		}
		catch (Exception e) {
			throw new AppContextException("Parsing secret key: ", e);
		}
	}

	private byte[] getNonce() throws AppContextException {
		try {
			return Base64.getDecoder().decode(nonceInput.getText().getBytes());
		}
		catch (Exception e) {
			throw new AppContextException("Parsing nonce: ", e);
		}
	}

	private byte[] getData() throws AppContextException {
		try {
			return rbDecrypt.isSelected()
					? Base64.getDecoder().decode(encryptedTextInput.getText().getBytes())
					: plainTextInput.getText().getBytes();
		}
		catch (Exception e) {
			throw new AppContextException("Parsing data", e);
		}
	}

	private void showError(String errorMessage) {
		notiArea.getChildren().add(Utils.getErrorLabel(errorMessage));
	}

	@Override
	public ViewId viewId() {
		return ViewId.SYMMETRIC_CRYPTION;
	}

}
