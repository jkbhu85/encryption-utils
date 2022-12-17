package com.jk.encryptionutils.ui;

import com.jk.encryptionutils.Constants;
import com.jk.encryptionutils.Utils;
import com.jk.encryptionutils.crypt.AsymEncryptionKeyService;
import com.jk.encryptionutils.crypt.AsymEncryptionKeyService.KeyPairGenerateRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Properties;

import static com.jk.encryptionutils.Constants.HALF_UNIT;
import static com.jk.encryptionutils.Constants.UNIT;

public final class AsymKeyGenerationViewCreator implements ViewCreator {

	private static final String UNIX_LINE_SEPARATOR = "\n";

	private static final ObservableList<Integer> KEY_SIZES =
			FXCollections.observableArrayList(1024, 1024 * 2, 1024 * 4, 1024 * 8);
	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

	private final DirectoryChooser dirChooser;
	private final Pane notiArea;
	private final Label filePathLabel;
	private final Stage stage;
	private File keySaveLocation;

	public AsymKeyGenerationViewCreator(Stage stage) {
		this.dirChooser = new DirectoryChooser();
		this.notiArea = new VBox();
		this.stage = stage;
		this.filePathLabel = new Label();
	}

	@Override
	public Pane createView() {
		ChoiceBox<Integer> keyLengthChoiceBox = new ChoiceBox<>(KEY_SIZES);
		keyLengthChoiceBox.setValue(KEY_SIZES.get(0));
		keyLengthChoiceBox.setPrefWidth(UNIT * 8);

		ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setMinWidth(UNIT * 15);

		ColumnConstraints cc2 = new ColumnConstraints();
		cc2.setMinWidth(UNIT * 8);

		GridPane gridPane = new GridPane();
		gridPane.setHgap(UNIT);
		gridPane.setVgap(UNIT);
		gridPane.getColumnConstraints().addAll(cc1, cc2);

		int row = 0;
		gridPane.addRow(row++, new Label("Algorithm"), new Label(Constants.ASYMMETRIC_ENCRYPTION_ALGORITHM));
		gridPane.addRow(row++, new Label("Length of the key"), keyLengthChoiceBox);

		Button selectFolderBtn = new Button("...");

		HBox folderView = new HBox(selectFolderBtn, filePathLabel);
		folderView.setMaxWidth(Double.MAX_VALUE);
		folderView.setSpacing(HALF_UNIT);

		setupFileChoose(selectFolderBtn, stage);

		Label folderChooseLabel = new Label("Select folder to save keys");
		Label folderChooseHelpText = Utils.secondaryColorText("Files with the same name will be overwritten.");
		folderChooseHelpText.setWrapText(true);

		VBox folderChooseLabelWrapper = new VBox(folderChooseLabel, folderChooseHelpText);
		gridPane.addRow(row++, folderChooseLabelWrapper, folderView);

		Label saveKeyInFileLabel = new Label("Save keys in files");
		Label saveKeyInFileHelpText = Utils
				.secondaryColorText("Name of the files will be 'public.key' and 'private.key'.");
		saveKeyInFileHelpText.setWrapText(true);
		VBox saveKeyInFileLabelWrapper = new VBox(saveKeyInFileLabel, saveKeyInFileHelpText);
		CheckBox cbKeyInFiles = new CheckBox();
		gridPane.addRow(row++, saveKeyInFileLabelWrapper, cbKeyInFiles);

		Label cbKeyInPropFileLabel = new Label("Save keys in properties file");
		Label cbKeyInPropFileHelpText = Utils.secondaryColorText("Name of the file will be 'keys.properties'.");
		cbKeyInPropFileHelpText.setWrapText(true);
		VBox cbKeyInPropFileLabelWrapper = new VBox(cbKeyInPropFileLabel, cbKeyInPropFileHelpText);
		CheckBox cbKeyInPropFile = new CheckBox();
		gridPane.addRow(row++, cbKeyInPropFileLabelWrapper, cbKeyInPropFile);

		Button btnGenKey = new Button();
		btnGenKey.setText("Generate Key Pair");
		btnGenKey.addEventFilter(MouseEvent.MOUSE_CLICKED,
				e -> handleGenerateKeyPair(keyLengthChoiceBox, cbKeyInFiles, cbKeyInPropFile));

		Button btnReset = new Button();
		btnReset.setText("Reset");
		btnReset.addEventFilter(MouseEvent.MOUSE_CLICKED,
				e -> resetForm(keyLengthChoiceBox, cbKeyInFiles, cbKeyInPropFile));

		HBox buttonBar = new HBox(btnGenKey, btnReset);
		buttonBar.setSpacing(UNIT);

		Pane title = Utils.getTitle("Generate Key Pair");

		VBox vbox = new VBox();
		vbox.setSpacing(UNIT);
		vbox.getChildren().addAll(title, gridPane, notiArea, buttonBar);

		Pane parent = Utils.rightPaneWrapper();
		parent.getChildren().add(vbox);
		return parent;
	}

	private void resetForm(ChoiceBox<Integer> keyLengthChoiceBox, CheckBox cbKeyInFiles,
			CheckBox cbKeyInPropFile) {
		keySaveLocation = null;
		updateFilePathLabel();

		keyLengthChoiceBox.setValue(KEY_SIZES.get(0));
		cbKeyInFiles.setSelected(false);
		cbKeyInPropFile.setSelected(false);

		notiArea.getChildren().clear();
	}

	private void updateFilePathLabel() {
		if (keySaveLocation == null) {
			filePathLabel.setText("");
		}
		else {
			filePathLabel.setText(keySaveLocation.getAbsolutePath());
		}
	}

	private void onFileChooseClick(Stage stage) {
		dirChooser.setTitle("Choose folder to save keys");
		File selectedFile = dirChooser.showDialog(stage);
		if (selectedFile != null) {
			keySaveLocation = selectedFile;
			updateFilePathLabel();
		}
	}

	private void setupFileChoose(Button btn, Stage stage) {
		btn.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> onFileChooseClick(stage));
	}

	private String validate(CheckBox cbKeyInFiles,
			CheckBox cbKeyInPropFile) {
		if (keySaveLocation == null) {
			return "Select a folder to save files.";
		}
		if (!keySaveLocation.exists()) {
			return "Folder to save keys does not exists.";
		}
		if (!keySaveLocation.canWrite()) {
			return "Permission denied while writing files.";
		}
		if (!cbKeyInFiles.isSelected() && !cbKeyInPropFile.isSelected()) {
			return "Select at least one way to save keys (either in files or properties).";
		}
		return null;
	}

	private void handleGenerateKeyPair(ChoiceBox<Integer> keyLengthChoiceBox, CheckBox cbKeyInFiles,
			CheckBox cbKeyInPropFile) {
		notiArea.getChildren().clear();
		String error = validate(cbKeyInFiles, cbKeyInPropFile);
		if (error == null) {
			generateKeyPair(keyLengthChoiceBox.getValue(), cbKeyInFiles.isSelected(), cbKeyInPropFile.isSelected());
		}
		else {
			notiArea.getChildren().add(Utils.getErrorLabel(error));
		}
	}

	private void generateKeyPair(int keySize, boolean saveInFiles, boolean saveInProp) {
		KeyPair keyPair = null;
		try {
			KeyPairGenerateRequest req = KeyPairGenerateRequest.builder()
					.algorithm(Constants.ASYMMETRIC_ENCRYPTION_ALGORITHM)
					.keySize(keySize)
					.build();
			keyPair = AsymEncryptionKeyService.generateKeyPair(req);
		}
		catch (Exception e) {
			String errMsg = e.getMessage() != null ? e.getMessage() : "Error occurred while generating key pair.";
			notiArea.getChildren().add(Utils.getErrorLabel(errMsg));
		}

		if (keyPair != null) {
			String error1 = saveInFiles ? writeToFiles(keyPair, keySize) : "";
			String error2 = saveInProp ? writeToPropertiesFile(keyPair, keySize) : "";
			if (!error1.isBlank() || !error2.isBlank()) {
				if (!error1.isBlank()) {
					notiArea.getChildren().add(Utils.getErrorLabel(error1));
				}
				if (!error2.isBlank()) {
					notiArea.getChildren().add(Utils.getErrorLabel(error2));
				}
			}
			else {
				notiArea.getChildren().add(Utils.getSuccessLabel("Keys were written to file(s) successfully."));
			}
		}
	}

	private static String wrapKeyWithUnixEndings(String str) {
		final int wrapLength = 60;
		String lineSeparator = UNIX_LINE_SEPARATOR;
		int numLineBreakChars = (str.length() / wrapLength) * lineSeparator.length();
		StringBuilder sb = new StringBuilder(str.length() + numLineBreakChars);
		for (int i = 0; i < str.length();) {
			final int k = Math.min(i + wrapLength, str.length());
			if (i > 0) {
				sb.append(lineSeparator);
			}
			sb.append(str, i, k);
			i = k;
		}
		return sb.toString();
	}

	private static String rsaPrivateKeyToFileFormat(PrivateKey rsaPrivateKey) {
		String encodedKey = Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded());
		return "-----BEGIN RSA PRIVATE KEY-----"
				+ UNIX_LINE_SEPARATOR
				+ wrapKeyWithUnixEndings(encodedKey)
				+ UNIX_LINE_SEPARATOR
				+ "-----END RSA PRIVATE KEY-----";
	}

	private static String rsaPublicKeyToFileFormat(PublicKey rsaPublicKey) {
		String encodedKey = Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded());
		return "-----BEGIN RSA PUBLIC KEY-----"
				+ UNIX_LINE_SEPARATOR
				+ wrapKeyWithUnixEndings(encodedKey)
				+ UNIX_LINE_SEPARATOR
				+ "-----END RSA PUBLIC KEY-----";
	}

	private String writeToFiles(KeyPair keyPair, int keySize) {
		String suffix = LocalDateTime.now().format(DTF);
		File privateKeyFile = new File(keySaveLocation, "private-" + keySize + "-" + suffix + ".pem");
		File publicKeyFile = new File(keySaveLocation, "public-" + keySize + "-" + suffix + ".pem");
		String privateKeyValue = rsaPrivateKeyToFileFormat(keyPair.getPrivate());
		String publicKeyValue = rsaPublicKeyToFileFormat(keyPair.getPublic());

		String error = "";
		try (FileOutputStream out = new FileOutputStream(publicKeyFile)) {
			out.write(publicKeyValue.getBytes(StandardCharsets.UTF_8));
		}
		catch (Exception e) {
			error += e.getMessage() != null ? e.getMessage() : "Error occurred while writing public key.";
		}

		try (FileOutputStream out = new FileOutputStream(privateKeyFile)) {
			out.write(privateKeyValue.getBytes(StandardCharsets.UTF_8));
		}
		catch (Exception e) {
			if (!error.isEmpty()) {
				error += " ";
			}
			error += e.getMessage() != null ? e.getMessage() : "Error occurred while writing private key.";
		}
		return error;
	}

	private String writeToPropertiesFile(KeyPair keyPair, int keySize) {
		Properties props = new Properties();
		String privateKeyValue = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
		String publicKeyValue = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
		props.put("rsa.public.key", publicKeyValue);
		props.put("rsa.private.key", privateKeyValue);
		LocalDateTime dateTime = LocalDateTime.now();

		String jdk = System.getProperty("java.runtime.name") + "(" + System.getProperty("java.specification.version")
				+ ")";
		String comment = "RSA public and private keys" + UNIX_LINE_SEPARATOR
				+ "RSA key size: " + keySize + UNIX_LINE_SEPARATOR
				+ "Generated by: " + Constants.APP_TITLE + " - " + jdk + UNIX_LINE_SEPARATOR
				+ "Time: " + dateTime.format(DateTimeFormatter.ISO_DATE_TIME);

		String suffix = dateTime.format(DTF);
		File file = new File(keySaveLocation, "keys-" + keySize + "-" + suffix + ".properties");
		try (FileOutputStream out = new FileOutputStream(file)) {
			props.store(out, comment);
		}
		catch (Exception e) {
			return e.getMessage() != null ? e.getMessage()
					: "Error occurred while writing key pair to properties file.";
		}
		return "";
	}

	@Override
	public ViewId viewId() {
		return ViewId.ASYMMETRIC_KEY_GENERATION;
	}

}
