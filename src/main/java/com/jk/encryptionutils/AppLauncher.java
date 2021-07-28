package com.jk.encryptionutils;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javafx.application.Application;

public class AppLauncher {

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		Application.launch(EncryptionUtilsApp.class, args);
	}

}
