package com.duckchat.crypto;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DuckyPublicKey {
	
	private PublicKey pubKey;
	
	public DuckyPublicKey(PublicKey pubKey) {
		this.pubKey = pubKey;
	}
	
	public DuckyPublicKey(String encodedPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] pubKeyBytes = Base64.getDecoder().decode(encodedPublicKey.getBytes());
		X509EncodedKeySpec spec = new X509EncodedKeySpec(pubKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		this.pubKey = keyFactory.generatePublic(spec);
	}
	
	public String encrypt(String msg) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		return Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes()));
		
		//Cipher cipher = Cipher.getInstance("RSA");
//		cipher.init(Cipher.ENCRYPT_MODE, pair.getPublic());
//		return Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes()));
	}

	public PublicKey getPublicKey() {
		return pubKey;
	}
	
	public String encode() {
		return Base64.getEncoder().encodeToString(pubKey.getEncoded());
	}
	
}
