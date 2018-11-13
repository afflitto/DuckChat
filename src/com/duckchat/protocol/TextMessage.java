package com.duckchat.protocol;

import com.duckchat.crypto.*;

import java.util.ArrayList;
import javafx.util.Pair;

public class TextMessage extends Message {

	public TextMessage(ArrayList<Pair<String, String>> data) {
		this.type = "text";
		this.data = data;		
	}
	
	public TextMessage(String name, String channel, String plainText, DuckySymmetricKey symmetricKey) {
		this.type = "text";
		this.data = new ArrayList<>();
		
		this.data.add(new Pair<>("name", name));
		this.data.add(new Pair<>("channel", channel));
		try {
			this.data.add(new Pair<>("ciphertext", symmetricKey.encryptText(plainText)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.data.add(new Pair<>("timestamp", new Long(System.currentTimeMillis()/1000).toString()));
	}
}
