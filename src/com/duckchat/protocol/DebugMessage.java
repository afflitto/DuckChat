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
public class DebugMessage extends Message {
	public DebugMessage(ArrayList<Pair<String, String>> data) {
		super(data, "debug");
	}
	
	
	/**
	 * 
	 * New Debug message:
	 * flags={
	 * 0 : kill
	 * 1 : listUsers
	 * }
	 * 
	 * @param debugFlag
	 * @param channel
	 * @param symmetricKey
	 */
	public DebugMessage(int debugFlag, String channel, DuckySymmetricKey symmetricKey) {
		super(new ArrayList<>(), "debug");

		this.data.add(new Pair<>("flag", "" + debugFlag));
		this.data.add(new Pair<>("channel", channel));
		try {
			this.data.add(new Pair<>("ciphertext", symmetricKey.encryptText("" + debugFlag)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.data.add(new Pair<>("timestamp", new Long(System.currentTimeMillis() / 1000).toString()));
	}

	public String getCipherText() {
		for (Pair<String, String> pair : data) {
			if (pair.getKey().equals("ciphertext")) {
				return pair.getValue();
			}
		}
		return null;
		
	}
	public int getDebugFlag() {
		for (Pair<String, String> pair : data) {
			if (pair.getKey().equals("flag")) {
				return Integer.parseInt(pair.getValue());
			}
		}
		return -1;
	}
}
