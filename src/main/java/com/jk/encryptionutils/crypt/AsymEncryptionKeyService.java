package com.jk.encryptionutils.crypt;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.jk.encryptionutils.Constants;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

public class AsymEncryptionKeyService {

	public static KeyPair generateKeyPair(KeyPairGenerateRequest keyPairGenerateRequest) throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyPairGenerateRequest.getAlgorithm(),
				Constants.SECURITY_PROVIDER);
		keyPairGenerator.initialize(keyPairGenerateRequest.getKeySize());
		return keyPairGenerator.generateKeyPair();
	}

	public static KeyPair createKeyPair(KeyPairCreateRequest keyPairCreateRequest) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(keyPairCreateRequest.getAlgorithm());
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(
				Base64.getDecoder().decode(keyPairCreateRequest.getPublicKey()));
		PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

		// load private key
		PKCS8EncodedKeySpec pcksKeySpec = new PKCS8EncodedKeySpec(
				Base64.getDecoder().decode(keyPairCreateRequest.getPrivateKey()));
		PrivateKey privateKey = keyFactory.generatePrivate(pcksKeySpec);

		return new KeyPair(publicKey, privateKey);
	}

	@Getter
	@Builder
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static class KeyPairCreateRequest {

		@NonNull
		String publicKey;
		@NonNull
		String privateKey;
		@NonNull
		String algorithm;

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
