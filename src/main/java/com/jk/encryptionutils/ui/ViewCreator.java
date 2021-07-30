package com.jk.encryptionutils.ui;

import javafx.scene.layout.Pane;

public interface ViewCreator {

	/**
	 * Creates the view.
	 * 
	 * @return returns the created, it can be null
	 */
	Pane createView();
	
	ViewId viewId();

}
