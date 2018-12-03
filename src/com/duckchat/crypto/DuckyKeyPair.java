package com.duckchat.crypto;
import java.security.*;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DuckyKeyPair {
	
	private KeyPair pair;
	
	public DuckyKeyPair() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		this(1024); //default to 1024 bits
	}
	
	public DuckyKeyPair(int keySize) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
		keygen.initialize(keySize);
				
		pair = keygen.generateKeyPair();
	}
	
	public DuckyKeyPair(KeyPair pair) {
		this.pair = pair;
	}
	
	public String encrypt(String msg) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pair.getPublic());
		return Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes()));
		
	}
	
	public String decrypt(String msg) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());
		byte[] cipherText = Base64.getDecoder().decode(msg);
		return new String(cipher.doFinal(cipherText));
	}
	
	public PublicKey getPublicKey() {
		return pair.getPublic();
	}
	
	
}
