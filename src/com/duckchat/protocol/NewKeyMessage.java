package com.duckchat.protocol;

import java.security.PublicKey;
import java.util.ArrayList;

import com.duckchat.crypto.DuckyPublicKey;
import com.duckchat.crypto.DuckySymmetricKey;

import javafx.util.Pair;

public class NewKeyMessage extends Message {
	public NewKeyMessage(ArrayList<Pair<String, String>> data) {
		this.data = data;		
	}
	
	public NewKeyMessage(String channel, DuckySymmetricKey symmetricKey, DuckyPublicKey toPublicKey) {
		this.data = new ArrayList<>();
		
		this.data.add(new Pair<>("channel", channel));
		try {
			this.data.add(new Pair<>("key", toPublicKey.encrypt(symmetricKey.encodeKey())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.data.add(new Pair<>("timestamp", new Long(System.currentTimeMillis()/1000).toString()));
	}
}
