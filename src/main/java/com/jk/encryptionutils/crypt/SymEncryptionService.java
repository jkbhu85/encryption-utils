package com.jk.encryptionutils.crypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.jk.encryptionutils.AppContextException;
import com.jk.encryptionutils.Constants;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

public final class SymEncryptionService {

	private static byte[] doOperation(final int mode, CryptionReqeust cryptionRequest) throws Exception {
		Cipher cipher = Cipher.getInstance(cryptionRequest.getTransformation(), Constants.SECURITY_PROVIDER);
		cipher.init(
				mode,
				cryptionRequest.getSecretKey(),
				new IvParameterSpec(cryptionRequest.getNonce()));
		return cipher.doFinal(cryptionRequest.getData());
	}

	public byte[] encrypt(CryptionReqeust cryptionRequest) throws AppContextException {
		try {
			return doOperation(Cipher.ENCRYPT_MODE, cryptionRequest);
		}
		catch (Exception e) {
			throw new AppContextException("Encrypting", e);
		}
	}

	public byte[] decrypt(CryptionReqeust cryptionRequest) throws AppContextException {
		try {
			return doOperation(Cipher.DECRYPT_MODE, cryptionRequest);
		}
		catch (Exception e) {
			throw new AppContextException("Decrypting", e);
		}
	}

	@Getter
	@Builder
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static class CryptionReqeust {

		@NonNull
		String transformation;
		@NonNull
		SecretKey secretKey;
		@NonNull
		byte[] nonce;
		@NonNull
		byte[] data;

	}

}
