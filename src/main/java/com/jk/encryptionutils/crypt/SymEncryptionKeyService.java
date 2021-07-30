package com.jk.encryptionutils.crypt;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SymEncryptionKeyService {

	private static final Object LOCK = new int[0];
	private final SecureRandom secureRandom;

	public SymEncryptionKeyService() {
		this.secureRandom = new SecureRandom();
	}

	private byte[] getRandom(final int numberOfBytes) {
		byte[] random = new byte[numberOfBytes];
		synchronized (LOCK) {
			secureRandom.nextBytes(random);
		}
		return random;
	}

	public byte[] generateNonce(int numberOfBytes) {
		return getRandom(numberOfBytes);
	}

	public SecretKey createNewKey(SymmetricKeyRequest symKeyReq) throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(symKeyReq.getMethod());
		keyGenerator.init(symKeyReq.getKeySize(), SecureRandom.getInstanceStrong());
		return keyGenerator.generateKey();
	}

	public SecretKey getSecretKey(String algorithm, byte[] keyData) {
		return new SecretKeySpec(keyData, algorithm);
	}

	public static class SymmetricKeyRequest {

		private final String method;
		private final int keySize;

		private SymmetricKeyRequest(SymmetricKeyRequestBuilder builder) {
			this.method = builder.method;
			this.keySize = builder.keySize;
		}

		public String getMethod() {
			return method;
		}

		public int getKeySize() {
			return keySize;
		}

		public static class SymmetricKeyRequestBuilder {

			private String method;
			private int keySize;

			private SymmetricKeyRequestBuilder() {
			}

			/**
			 * Sets the method to be used (e.g. "AES"). Method must be non-null and non-blank string.
			 * 
			 * @param method the algorithm to be used
			 * @return reference to this instance
			 * @throws IllegalArgumentException if the algorithm null or blank
			 */
			public SymmetricKeyRequestBuilder method(String method) {
				if (method == null || method.trim().isEmpty()) {
					throw new IllegalArgumentException("Algorithm must not be null or blank string.");
				}
				this.method = method.trim();
				return this;
			}

			/**
			 * Sets size of the key. This value must be a positive integer.
			 * 
			 * @param keySize size of the key
			 * @return reference to this instance
			 * @throws IllegalArgumentException if the size of the key is non-positive integer
			 */
			public SymmetricKeyRequestBuilder keySize(int keySize) {
				this.keySize = keySize;
				return this;
			}

			public SymmetricKeyRequest build() {
				return new SymmetricKeyRequest(this);
			}

		}

		public static SymmetricKeyRequestBuilder builder() {
			return new SymmetricKeyRequestBuilder();
		}

	}

}
