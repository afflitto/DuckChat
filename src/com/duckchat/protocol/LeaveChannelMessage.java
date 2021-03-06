package com.duckchat.protocol;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;

import com.duckchat.crypto.DuckyPublicKey;

import javafx.util.*;

@SuppressWarnings("restriction")
public class LeaveChannelMessage extends Message {

	public LeaveChannelMessage(ArrayList<Pair<String, String>> data) {
		super(data, "leave");
	}

	public LeaveChannelMessage(String name, String channel, PublicKey pubKey) {
		super(new ArrayList<>(), "leave");

		this.data.add(new Pair<>("name", name));
		this.data.add(new Pair<>("channel", channel));
		String encoded = Base64.getEncoder().encodeToString(pubKey.getEncoded());
		this.data.add(new Pair<>("pubkey", encoded));
	}

	public String getName() {
		for (Pair<String, String> kvItem : data) {
			if (kvItem.getKey().equals("name")) {
				return kvItem.getValue();
			}
		}
		return null;
	}

	public DuckyPublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String encodedKey = "";
		for (Pair<String, String> kvItem : data) {
			if (kvItem.getKey().equals("pubkey")) {
				encodedKey = kvItem.getValue();
			}
		}

		return new DuckyPublicKey(encodedKey);
	}

}
