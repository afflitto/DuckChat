package com.duckchat.protocol;

import com.duckchat.crypto.*;

import java.util.ArrayList;
import javafx.util.Pair;

@SuppressWarnings("restriction")
public class TextMessage extends Message {

	public TextMessage(ArrayList<Pair<String, String>> data) {
		super(data, "text");
	}
	
	public TextMessage(String name, String channel, String plainText, DuckySymmetricKey symmetricKey) {
		super(new ArrayList<>(), "text");
		
		this.data.add(new Pair<>("name", name));
		this.data.add(new Pair<>("channel", channel));
		try {
			this.data.add(new Pair<>("ciphertext", symmetricKey.encryptText(plainText)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.data.add(new Pair<>("timestamp", new Long(System.currentTimeMillis()/1000).toString()));
	}
	
	public String getCipherText() {
		for(Pair<String, String> pair : data) {
			if(pair.getKey().equals("ciphertext")) {
				return pair.getValue();
			}
		}
		return null;
	}
	
	public String getName() {
		for(Pair<String, String> pair : data) {
			if(pair.getKey().equals("name")) {
				return pair.getValue();
			}
		}
		return null;
	}
}
