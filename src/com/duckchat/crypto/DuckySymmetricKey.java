package com.duckchat.crypto;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.*;

public class DuckySymmetricKey {
	private SecretKey secretKey;
	private Cipher cipher;
	
	public DuckySymmetricKey() {
		SecureRandom random = new SecureRandom();
		byte[] key = new byte[16];
		random.nextBytes(key);
		secretKey = new SecretKeySpec(key, "AES");
	}
	
	public DuckySymmetricKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}
	
	public DuckySymmetricKey(String encodedKey) {
		byte[] key = Base64.getDecoder().decode(encodedKey.getBytes());
		this.secretKey = new SecretKeySpec(key, "AES");
	}
	
	public SecretKey getKey() {
		return secretKey;
	}
	
	public String encryptText(String msg) throws Exception {
		byte[] iv = new byte[12];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);
		
		try {
			cipher = Cipher.getInstance("AES/GCM/NoPadding");
			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
			byte[] cipherBytes = cipher.doFinal(msg.getBytes());
			return encode(cipherBytes, iv);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			return "";
		}		
	}
	
	public String decryptText(String cipherText) {
		try {
			cipher = Cipher.getInstance("AES/GCM/NoPadding");
			ByteBuffer ivAndCipher = decode(cipherText);
			byte[] iv = new byte[12];
			ivAndCipher.get(iv);
			
			byte[] cipherBytes = new byte[ivAndCipher.remaining()];
			ivAndCipher.get(cipherBytes);
			
			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
			return new String(cipher.doFinal(cipherBytes));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			System.out.println("unable to decrypt message");
			return "--encrypted--";
		}	
	}
	
	private String encode(byte[] cipherBytes, byte[] iv) throws Exception {
		if(iv.length != 12) {
			throw new Exception("wrong IV length!");
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(12 + cipherBytes.length);
		byteBuffer.put(iv);
		byteBuffer.put(cipherBytes);
		return new String(Base64.getEncoder().encodeToString(byteBuffer.array()));
	}
	
	private ByteBuffer decode(String cipherText) {
		byte[] cipherBytes = Base64.getDecoder().decode(cipherText.getBytes());
		return ByteBuffer.wrap(cipherBytes);
	}
	
	public String encodeKey() {
		return Base64.getEncoder().encodeToString(secretKey.getEncoded());
	}
}
