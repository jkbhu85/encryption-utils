package com.jk.encryptionutils.crypt;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.Cipher;

import com.jk.encryptionutils.Constants;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

public final class AsymEncryptionService {

	private AsymEncryptionService() {
	}

	private static byte[] doOperation(int operation, byte[] data, Key key, String transformation) throws Exception {
		Cipher cipher = Cipher.getInstance(transformation, Constants.SECURITY_PROVIDER);
		cipher.init(operation, key);
		final int blockSize = cipher.getBlockSize();
		int start = 0;
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		while (start < data.length) {
			byte[] dataBlock = start + blockSize > data.length
					? Arrays.copyOfRange(data, start, data.length)
					: Arrays.copyOfRange(data, start, start + blockSize);
			start += blockSize;
			out.write(cipher.doFinal(dataBlock));
		}
		return out.toByteArray();
	}

	public static byte[] encrypt(EncryptionRequest encryptionRequest) throws Exception {
		return doOperation(
				Cipher.ENCRYPT_MODE,
				encryptionRequest.getData(),
				encryptionRequest.getPublicKey(),
				encryptionRequest.getTransformation());
	}

	public static byte[] decrypt(DecryptionRequest decryptionRequest) throws Exception {
		return doOperation(
				Cipher.DECRYPT_MODE,
				decryptionRequest.getData(),
				decryptionRequest.getPrivateKey(),
				decryptionRequest.getTransformation());
	}

	@Getter
	@Builder
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static class EncryptionRequest {

		@NonNull
		byte[] data;
		@NonNull
		PublicKey publicKey;
		@NonNull
		String transformation;

	}

	@Getter
	@Builder
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static class DecryptionRequest {

		@NonNull
		byte[] data;
		@NonNull
		PrivateKey privateKey;
		@NonNull
		String transformation;

	}

}
