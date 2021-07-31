package com.jk.encryptionutils.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.PrivateKey;
import java.util.Base64;

import com.jk.encryptionutils.Constants;
import com.jk.encryptionutils.Utils;
import com.jk.encryptionutils.crypt.AsymEncryptionKeyService;
import com.jk.encryptionutils.crypt.AsymEncryptionService;
import com.jk.encryptionutils.crypt.AsymEncryptionService.DecryptionRequest;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AsymDecryptionViewCreator implements ViewCreator {

	private final ToggleGroup keySelGroup = new ToggleGroup();
	private final FileChooser fileChooser = new FileChooser();
	private final Pane notiArea = new VBox();
	private final Stage stage;
	private RadioButton rbPublicKeyBase64;
	private RadioButton rbPublicKeyFile;
	private TextArea base64KeyInput;
	private File keySourceFile;
	private Label filePathLabel;
	private Button btnFileChoose;
	private TextArea inputTextArea;

	private Button btnSubmit;
	private Button btnReset;

	public AsymDecryptionViewCreator(Stage stage) {
		this.stage = stage;
	}

	@Override
	public Pane createView() {
		base64KeyInput = new TextArea();
		base64KeyInput.setPrefRowCount(3);
		HBox base64KeyInputWrapper = new HBox(base64KeyInput);
		base64KeyInputWrapper.setPadding(Constants.PADDING_DOUBLE_LEFT);
		rbPublicKeyBase64 = new RadioButton("Base64 private key");
		rbPublicKeyBase64.setToggleGroup(keySelGroup);
		rbPublicKeyBase64.setOnAction(ae -> handleKeySourceSelection());
		VBox keyBase64Wrapper = new VBox(rbPublicKeyBase64, base64KeyInputWrapper);
		keyBase64Wrapper.setSpacing(Constants.ONE_FOURTH_UNIT);

		rbPublicKeyFile = new RadioButton("Path to private key");
		rbPublicKeyFile.setToggleGroup(keySelGroup);
		rbPublicKeyFile.setOnAction(ae -> handleKeySourceSelection());

		btnFileChoose = new Button("...");
		btnFileChoose.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> chooseFile());
		filePathLabel = new Label();
		HBox fileBtnLabelBox = new HBox(btnFileChoose, filePathLabel);
		fileBtnLabelBox.setSpacing(Constants.HALF_UNIT);
		fileBtnLabelBox.setPadding(Constants.PADDING_DOUBLE_LEFT);
		VBox fileChooseWrapper = new VBox(rbPublicKeyFile, fileBtnLabelBox);
		fileChooseWrapper.setSpacing(Constants.ONE_FOURTH_UNIT);

		Label inputLabel = new Label("Text to decrypt");
		inputTextArea = new TextArea();
		inputTextArea.setPrefRowCount(3);
		Pane textFc = Utils.vertFormControl(inputLabel, inputTextArea);

		btnSubmit = new Button("Decrypt Text");
		btnSubmit.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> onEncrypt());

		btnReset = new Button("Reset");
		btnReset.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> reset());
		HBox buttonBar = new HBox(btnSubmit, btnReset);
		buttonBar.setSpacing(Constants.UNIT);

		Pane title = Utils.getTitle("Asymmetric Decryption");
		Label transformationLabel = new Label("Transformation: " + Constants.ASYMMETRIC_ENCRYPTION_TRANSFORMATION);
		VBox vbox = new VBox(title, transformationLabel, fileChooseWrapper, keyBase64Wrapper, textFc, notiArea,
				buttonBar);
		vbox.setSpacing(Constants.UNIT);
		Pane parent = Utils.rightPaneWrapper();
		parent.getChildren().add(vbox);

		reset();
		return parent;
	}

	void reset() {
		rbPublicKeyFile.setSelected(true);
		base64KeyInput.setText("");
		filePathLabel.setText("");
		keySourceFile = null;
		handleKeySourceSelection();
		inputTextArea.setText("");
		notiArea.getChildren().clear();
	}

	void handleKeySourceSelection() {
		if (rbPublicKeyBase64.isSelected()) {
			base64KeyInput.setDisable(false);
		}
		else {
			base64KeyInput.setDisable(true);
		}

		if (rbPublicKeyFile.isSelected()) {
			btnFileChoose.setDisable(false);
			filePathLabel.setDisable(false);
		}
		else {
			btnFileChoose.setDisable(true);
			filePathLabel.setDisable(true);
		}
	}

	private void chooseFile() {
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			keySourceFile = file;
			onChooseFile();
		}
	}

	private void onChooseFile() {
		if (keySourceFile != null) {
			filePathLabel.setText(keySourceFile.getAbsolutePath());
		}
		else {
			filePathLabel.setText("");
		}
	}

	private void beforeProcessStart() {
		btnSubmit.setDisable(true);
		btnReset.setDisable(true);
	}

	private void afterProcessEnd() {
		btnSubmit.setDisable(false);
		btnReset.setDisable(false);
	}

	private String validate() {
		if (!rbPublicKeyBase64.isSelected() && !rbPublicKeyFile.isSelected()) {
			return "Select key source as either base64 or key file.";
		}
		if (rbPublicKeyBase64.isSelected()) {
			if (base64KeyInput.getText() == null || base64KeyInput.getText().isBlank()) {
				return "Base64 private key can not be blank.";
			}
		}
		if (rbPublicKeyFile.isSelected()) {
			if (keySourceFile == null) {
				return "Select a file for private key.";
			}
			if (!keySourceFile.exists() || !keySourceFile.canRead()) {
				return "File does not exists or permission to read the file is denied.";
			}
		}
		if (inputTextArea.getText() == null || inputTextArea.getText().isEmpty()) {
			return "Enter some text to decrypt.";
		}
		return "";
	}

	private void onEncrypt() {
		beforeProcessStart();
		startProcess();
		afterProcessEnd();
	}

	private void startProcess() {
		notiArea.getChildren().clear();
		String error = validate();
		if (!error.isEmpty()) {
			notiArea.getChildren().add(Utils.getErrorLabel(error));
			return;
		}

		PrivateKey privateKey;
		try {
			privateKey = getPrivateKey();
		}
		catch (Exception e) {
			String errMsg = e.getMessage() != null
					? "Reading private key: " + e.getMessage()
					: "Error occurred while parsing private key.";
			notiArea.getChildren().add(Utils.getErrorLabel(errMsg));
			return;
		}
		byte[] data;
		try {
			data = Base64.getDecoder().decode(inputTextArea.getText());
		}
		catch (Exception e) {
			String errMsg = e.getMessage() != null
					? "Parsing encrypted text: " + e.getMessage()
					: "Error occurred while parsing private key.";
			notiArea.getChildren().add(Utils.getErrorLabel(errMsg));
			return;
		}

		try {
			DecryptionRequest decryptionRequest = DecryptionRequest.builder()
					.data(data)
					.transformation(Constants.ASYMMETRIC_ENCRYPTION_TRANSFORMATION)
					.privateKey(privateKey)
					.build();
			byte[] encrypted = AsymEncryptionService.decrypt(decryptionRequest);
			String str = new String(encrypted);
			Utils.copyToClipboardDialog("Decrypted Text", str);
		}
		catch (Exception e) {
			String errMsg = e.getMessage() != null
					? "Decrypting text: " + e.getMessage()
					: "Error occurred while decrypting data.";
			notiArea.getChildren().add(Utils.getErrorLabel(errMsg));
			e.printStackTrace();
			return;
		}
	}

	private PrivateKey getPrivateKey() throws Exception {
		byte[] keyData;
		if (rbPublicKeyBase64.isSelected()) {
			keyData = Base64.getDecoder().decode(base64KeyInput.getText());
		}
		else {
			FileInputStream in = new FileInputStream(keySourceFile);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) != -1) {
				bout.write(buffer, 0, len);
			}
			in.close();
			keyData = bout.toByteArray();
		}
		return AsymEncryptionKeyService.getPrivateKey(Constants.ASYMMETRIC_ENCRYPTION_ALGORITHM, keyData);
	}

	@Override
	public ViewId viewId() {
		return ViewId.ASYMMETRIC_DECRYPTION;
	}

}
