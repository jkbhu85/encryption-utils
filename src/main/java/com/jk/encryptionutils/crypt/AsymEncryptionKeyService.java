package com.jk.encryptionutils.crypt;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.jk.encryptionutils.Constants;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

public final class AsymEncryptionKeyService {

	private AsymEncryptionKeyService() {
	}

	public static KeyPair generateKeyPair(KeyPairGenerateRequest keyPairGenerateRequest) throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyPairGenerateRequest.getAlgorithm(),
				Constants.SECURITY_PROVIDER);
		keyPairGenerator.initialize(keyPairGenerateRequest.getKeySize());
		return keyPairGenerator.generateKeyPair();
	}

	public static PublicKey getPublicKey(String algorithm, byte[] publicKeyData) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyData);
		return keyFactory.generatePublic(x509KeySpec);
	}

	public static PrivateKey getPrivateKey(String algorithm, byte[] privateKeyData) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		PKCS8EncodedKeySpec pcks8KeySpec = new PKCS8EncodedKeySpec(privateKeyData);
		return keyFactory.generatePrivate(pcks8KeySpec);
	}

	@Getter
	@Builder
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static class KeyPairGenerateRequest {

		@NonNull
		String algorithm;
		int keySize;

	}

}
