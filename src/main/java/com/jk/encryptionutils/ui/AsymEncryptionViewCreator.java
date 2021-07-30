package com.jk.encryptionutils.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Optional;

import com.jk.encryptionutils.Constants;
import com.jk.encryptionutils.Utils;
import com.jk.encryptionutils.crypt.AsymEncryptionKeyService;
import com.jk.encryptionutils.crypt.AsymEncryptionService;
import com.jk.encryptionutils.crypt.AsymEncryptionService.EncryptionRequest;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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

public class AsymEncryptionViewCreator implements ViewCreator {

	private final ToggleGroup keySelGroup = new ToggleGroup();
	private final FileChooser fileChooser = new FileChooser();
	private final Pane notiArea = new VBox();
	private final Stage stage;
	private RadioButton rbKeyBase64;
	private TextArea base64KeyInput;
	private RadioButton rbKeyFile;
	private File keySourceFile;
	private Label filePathLabel;
	private Button btnFileChoose;
	private TextArea inputTextArea;
	private Button btnSubmit;
	private Button btnReset;

	public AsymEncryptionViewCreator(Stage stage) {
		this.stage = stage;
	}

	@Override
	public Pane createView() {
		base64KeyInput = new TextArea();
		base64KeyInput.setPrefRowCount(3);
		HBox base64KeyInputWrapper = new HBox(base64KeyInput);
		base64KeyInputWrapper.setPadding(Constants.PADDING_DOUBLE_LEFT);
		rbKeyBase64 = new RadioButton("Base64 public key");
		rbKeyBase64.setToggleGroup(keySelGroup);
		rbKeyBase64.setOnAction(ae -> handleKeySourceSelection());
		VBox keyBase64Wrapper = new VBox(rbKeyBase64, base64KeyInputWrapper);
		keyBase64Wrapper.setSpacing(Constants.ONE_FOURTH_UNIT);

		rbKeyFile = new RadioButton("Path to public key");
		rbKeyFile.setToggleGroup(keySelGroup);
		rbKeyFile.setOnAction(ae -> handleKeySourceSelection());

		btnFileChoose = new Button("...");
		btnFileChoose.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> chooseFile());
		filePathLabel = new Label();
		HBox fileBtnLabelBox = new HBox(btnFileChoose, filePathLabel);
		fileBtnLabelBox.setSpacing(Constants.HALF_UNIT);
		fileBtnLabelBox.setPadding(Constants.PADDING_DOUBLE_LEFT);
		VBox fileChooseWrapper = new VBox(rbKeyFile, fileBtnLabelBox);
		fileChooseWrapper.setSpacing(Constants.ONE_FOURTH_UNIT);

		Label inputLabel = new Label("Text to encrypt");
		inputTextArea = new TextArea();
		inputTextArea.setPrefRowCount(3);
		Pane textFc = Utils.vertFormControl(inputLabel, inputTextArea);

		btnSubmit = new Button("Encrypt Text");
		btnSubmit.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> onEncrypt());

		btnReset = new Button("Reset");
		btnReset.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> reset());
		HBox buttonBar = new HBox(btnSubmit, btnReset);
		buttonBar.setSpacing(Constants.UNIT);

		Pane title = Utils.getTitle("Asymmetric Encryption");
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
		rbKeyFile.setSelected(true);
		base64KeyInput.setText("");
		filePathLabel.setText("");
		keySourceFile = null;
		handleKeySourceSelection();
		inputTextArea.setText("");
		notiArea.getChildren().clear();
	}

	void handleKeySourceSelection() {
		if (rbKeyBase64.isSelected()) {
			base64KeyInput.setDisable(false);
		}
		else {
			base64KeyInput.setDisable(true);
		}

		if (rbKeyFile.isSelected()) {
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
		if (!rbKeyBase64.isSelected() && !rbKeyFile.isSelected()) {
			return "Select key source as either base64 or key file.";
		}
		if (rbKeyBase64.isSelected()) {
			if (base64KeyInput.getText() == null || base64KeyInput.getText().isBlank()) {
				return "Base64 public key can not be blank.";
			}
		}
		if (rbKeyFile.isSelected()) {
			if (keySourceFile == null) {
				return "Select a file for public key.";
			}
			if (!keySourceFile.exists() || !keySourceFile.canRead()) {
				return "File does not exists or permission to read the file is denied.";
			}
		}
		if (inputTextArea.getText() == null || inputTextArea.getText().isEmpty()) {
			return "Enter some text to encrypt.";
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

		PublicKey publicKey;
		try {
			publicKey = getPublicKey();
		}
		catch (Exception e) {
			String errMsg = e.getMessage() != null
					? "Reading public key: " + e.getMessage()
					: "Error occurred while parsing public key.";
			notiArea.getChildren().add(Utils.getErrorLabel(errMsg));
			e.printStackTrace();
			return;
		}

		try {
			EncryptionRequest encryptionRequest = EncryptionRequest.builder()
					.data(inputTextArea.getText().getBytes())
					.transformation(Constants.ASYMMETRIC_ENCRYPTION_TRANSFORMATION)
					.publicKey(publicKey)
					.build();
			byte[] encrypted = AsymEncryptionService.encrypt(encryptionRequest);
			String base64 = Base64.getEncoder().encodeToString(encrypted);
			showEncryptedText(base64);
		}
		catch (Exception e) {
			String errMsg = e.getMessage() != null
					? "Encrypting text: " + e.getMessage()
					: "Error occurred while encrypting data.";
			notiArea.getChildren().add(Utils.getErrorLabel(errMsg));
			e.printStackTrace();
			return;
		}
	}

	private void showEncryptedText(String text) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Encrypted Text");
		ButtonType copyBtn = new ButtonType("Copy to Clipboard", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(copyBtn);
		dialog.setContentText(text);

		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
			Utils.copyToClipboard(text);
		}
	}

	private PublicKey getPublicKey() throws Exception {
		byte[] keyData;
		if (rbKeyBase64.isSelected()) {
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
		return AsymEncryptionKeyService.getPublicKey(Constants.ASYMMETRIC_ENCRYPTION_ALGORITHM, keyData);
	}

	@Override
	public ViewId viewId() {
		return ViewId.ASYMMETRIC_ENCRYPTION;
	}

}
