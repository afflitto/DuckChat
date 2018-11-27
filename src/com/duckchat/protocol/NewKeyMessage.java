package com.duckchat.protocol;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.duckchat.crypto.*;

import javafx.util.Pair;

@SuppressWarnings("restriction")
public class NewKeyMessage extends Message {
	public NewKeyMessage(ArrayList<Pair<String, String>> data) {
		super(data, "key");		
	}
	
	public NewKeyMessage(String channel, DuckySymmetricKey symmetricKey, DuckyPublicKey toPublicKey) {
		super(new ArrayList<Pair<String, String>>(), "key");
		
		this.data.add(new Pair<>("channel", channel));
		try {
			this.data.add(new Pair<>("key", toPublicKey.encrypt(symmetricKey.encodeKey())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.data.add(new Pair<>("timestamp", new Long(System.currentTimeMillis()/1000).toString()));
	}
	
	public DuckySymmetricKey decryptSymmetricKey(DuckyKeyPair pair) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String encryptedKey = "";
		for(Pair<String, String> kvItem : data) {
			if(kvItem.getKey().equals("key")) {
				encryptedKey = kvItem.getValue();
			}
		}
		return new DuckySymmetricKey(pair.decrypt(encryptedKey));
	}
}
