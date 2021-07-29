package com.jk.encryptionutils.crypt;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.Cipher;

import com.jk.encryptionutils.Constants;

public class AsymEncryptionService {

	private static byte[] doOperation(final int mode, String transformation, Key key, final byte[] data)
			throws Exception {
		Cipher cipher = Cipher.getInstance(transformation, Constants.SECURITY_PROVIDER);
		cipher.init(mode, key);
		final int blockSize = cipher.getBlockSize();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int start = 0;
		while (start < data.length) {
			byte[] dataBlock;
			if (start + blockSize > data.length) {
				dataBlock = Arrays.copyOfRange(data, start, data.length);
			}
			else {
				dataBlock = Arrays.copyOfRange(data, start, start + blockSize);
				start += blockSize;
			}
			out.write(cipher.doFinal(dataBlock));
		}
		return out.toByteArray();
	}

	public static byte[] encrypt(String transformation, PublicKey publicKey, byte[] data) throws Exception {
		return doOperation(Cipher.ENCRYPT_MODE, transformation, publicKey, data);
	}

	public static byte[] decrypt(String transformation, PrivateKey privateKey, byte[] data) throws Exception {
		return doOperation(Cipher.DECRYPT_MODE, transformation, privateKey, data);
	}

}
