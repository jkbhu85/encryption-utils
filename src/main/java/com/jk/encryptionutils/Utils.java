package com.jk.encryptionutils;

import static com.jk.encryptionutils.Constants.HALF_UNIT;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public final class Utils {

	public static void copyToClipboard(String text) {
		ClipboardContent content = new ClipboardContent();
		content.putString(text);
		Clipboard clipboard = Clipboard.getSystemClipboard();
		clipboard.setContent(content);
	}

	public static Label secondaryColorText(String text) {
		Label l = new Label(text);
		Font font = Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, FontPosture.REGULAR,
				Font.getDefault().getSize() * .9);
		l.setFont(font);
		l.setTextFill(Color.DARKGRAY);
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
		Font font = Font.font(Font.getDefault().getFamily(), FontWeight.MEDIUM, FontPosture.REGULAR,
				Font.getDefault().getSize() * 1.25);
		title.setFont(font);
		VBox vbox = new VBox(title, new Separator());
		vbox.setSpacing(HALF_UNIT);
		vbox.setMaxWidth(Double.MAX_VALUE);
		return vbox;
	}

	public static Pane rightPaneWrapper() {
		Pane pane = new HBox();
		pane.setPadding(Constants.PADDING_HALF_UNIT);
		pane.setMaxWidth(Double.MAX_VALUE);
		return pane;
	}

	public static VBox vertFormControl(Label label, Node node) {
		VBox control = new VBox(label, node);
		control.setSpacing(HALF_UNIT);
		return control;
	}

	private Utils() {
	}

}
