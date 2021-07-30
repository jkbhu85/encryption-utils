package com.jk.encryptionutils;

import static com.jk.encryptionutils.Constants.HALF_UNIT;

import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public final class Utils {

	public static void copyToClipboard(String text) {
		ClipboardContent content = new ClipboardContent();
		content.putString(text);
		Clipboard clipboard = Clipboard.getSystemClipboard();
		clipboard.setContent(content);
	}

	public static Label secondaryColorText(String text) {
		Label l = new Label(text);
		l.getStyleClass().add("secondary-text");
		return l;
	}

	public static Label getSuccessLabel(String msg) {
		Label label = new Label(msg);
		label.setTextFill(Color.GREEN);
		return label;
	}

	public static Label getErrorLabel(String msg) {
		Label label = new Label(msg);
		label.setTextFill(Color.RED);
		return label;
	}

	public static Pane getTitle(String titleText) {
		Label title = new Label(titleText);
		title.getStyleClass().add("view-title");
		VBox vbox = new VBox(title, new Separator());
		vbox.setSpacing(HALF_UNIT);
		vbox.setMaxWidth(Double.MAX_VALUE);
		return vbox;
	}

	public static Pane rightPaneWrapper() {
		Pane pane = new HBox();
		pane.setPadding(Constants.PADDING_UNIT);
		pane.setMaxWidth(Double.MAX_VALUE);
		return pane;
	}

	public static VBox vertFormControl(Label label, Node node) {
		VBox control = new VBox(label, node);
		control.setSpacing(HALF_UNIT);
		return control;
	}

	public static void copyToClipboardDialog(String title, String contentText) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle(title);
		ButtonType copyBtn = new ButtonType("Copy to Clipboard", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(copyBtn);
		dialog.setContentText(contentText);

		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
			Utils.copyToClipboard(contentText);
		}
	}

	public static Button createCopyToClipboardBtn(TextInputControl inp, String btnLabel) {
		Button btn = new Button(btnLabel);
		btn.getStyleClass().add("btn-sm");
		bindButtonToTextField(btn, inp);
		btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> copyToClipboard(inp.getText()));
		return btn;
	}

	public static void bindButtonToTextField(Button btn, TextInputControl inp) {
		inp.textProperty().addListener((v, s1, s2) -> btn.setDisable(s2 == null || s2.isEmpty()));
		String currentVal = inp.getText();

		// fire first event
		if (currentVal != null && currentVal.length() > 0) {
			inp.setText("");
		}
		else {
			inp.setText(" ");
		}
		inp.setText(currentVal);
	}

	private Utils() {
	}

}
