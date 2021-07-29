package com.jk.encryptionutils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javafx.geometry.Insets;
import javafx.scene.text.Font;

public final class Constants {

	public static final String SECURITY_PROVIDER = BouncyCastleProvider.PROVIDER_NAME;

	public static final String SYMMETRIC_ENCRYPTION_ALGRORITHM = "AES";
	public static final String SYMMETRIC_ENCRYPTION_TRANSFORMATION = "AES/GCM/NoPadding";
	public static final int SYMMETRIC_ENCRYPTION_NUMBER_OF_VI_BYTES = 12;
	public static final int SYMMETRIC_ENCRYPTION_KEY_SIZE = 256;

	public static final String ASYMMETRIC_ENCRYPTION_ALGORITHM = "RSA";
	public static final String ASYMMETRIC_ENCRYPTION_TRANSFORMATION = "RSA/None/OAEPWITHSHA-384ANDMGF1PADDING";

	public static final String APP_TITLE = "Encryption Utils";

	public static final double UNIT = Font.getDefault().getSize();
	public static final double HALF_UNIT = UNIT / 2;
	public static final double ONE_FOURTH_UNIT = UNIT / 4;
	public static final Insets PADDING_HALF_UNIT = new Insets(HALF_UNIT, HALF_UNIT, HALF_UNIT, HALF_UNIT);
	public static final Insets PADDING_UNIT_LEFT = new Insets(0, 0, 0, UNIT);

	private Constants() {
	}

}
