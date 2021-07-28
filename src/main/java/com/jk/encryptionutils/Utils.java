package com.jk.encryptionutils;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class Utils {

	public static void copyToClipboard(String text) {
		ClipboardContent content = new ClipboardContent();
		content.putString(text);
		Clipboard clipboard = Clipboard.getSystemClipboard();
		clipboard.setContent(content);
	}

	public static Label getTitle(String titleText) {
		Label title = new Label(titleText);
		Font font = Font.font(Font.getDefault().getFamily(), FontWeight.MEDIUM, FontPosture.REGULAR,
				Font.getDefault().getSize() * 1.25);
		title.setFont(font);
		return title;
	}

	public static VBox vertFormControl(Label label, Node node) {
		VBox control = new VBox(label, node);
		control.setSpacing(5);
		return control;
	}
}
