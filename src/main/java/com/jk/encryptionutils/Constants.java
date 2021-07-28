package com.jk.encryptionutils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javafx.geometry.Insets;

public final class Constants {

	public static final String SECURITY_PROVIDER = BouncyCastleProvider.PROVIDER_NAME;

	public static final String SYMMETRIC_ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
	public static final String SYMMETRIC_ENCRYPTION_METHOD = "AES";
	public static final int SYMMETRIC_ENCRYPTION_NUMBER_OF_VI_BYTES = 12;
	public static final int SYMMETRIC_ENCRYPTION_KEY_SIZE = 256;

	public static final String APP_TITLE = "Encryption Utils";

	public static final Insets SPACE_10 = new Insets(10, 10, 10, 10);
	public static final Insets SPACE_20_LEFT = new Insets(0, 0, 0, 20);

	private Constants() {
	}

}
