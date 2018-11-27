/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duckchat.protocol;

import com.duckchat.crypto.DuckySymmetricKey;
import java.util.ArrayList;
import javafx.util.Pair;

/**
 *
 * @author matthew
 */
@SuppressWarnings("restriction")
public class DebugMessage extends Message{
    public DebugMessage(ArrayList<Pair<String, String>> data) {
		super(data, "debug");
	}
	
	public DebugMessage(int debugMessage, String channel, DuckySymmetricKey symmetricKey) {
		super(new ArrayList<>(), "debug");
		
		this.data.add(new Pair<>("flag", ""+debugMessage));
		this.data.add(new Pair<>("channel", channel));
		try {
			this.data.add(new Pair<>("ciphertext", symmetricKey.encryptText(""+debugMessage)));
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
}
