package com.jk.encryptionutils.crypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import com.jk.encryptionutils.Constants;
import com.jk.encryptionutils.crypt.SymEncryptionKeyService.SymmetricKey;

public final class SymEncryptionService {

	private final String encryptionMethod;
	private final SymmetricKey symmetricKey;

	public SymEncryptionService(String encryptionMethod, SymmetricKey symmetricKey) {
		this.encryptionMethod = encryptionMethod;
		this.symmetricKey = symmetricKey;
	}

	private byte[] doOperation(final int mode, byte[] data) throws Exception {
		Cipher cipher = Cipher.getInstance(encryptionMethod, Constants.SECURITY_PROVIDER);
		cipher.init(mode, symmetricKey.getSecretKey(), new IvParameterSpec(symmetricKey.getIv()));
		return cipher.doFinal(data);
	}

	public byte[] encrypt(byte[] data) throws Exception {
		return doOperation(Cipher.ENCRYPT_MODE, data);
	}

	public byte[] decrypt(byte[] data) throws Exception {
		return doOperation(Cipher.DECRYPT_MODE, data);
	}

}
