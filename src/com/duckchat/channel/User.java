package com.duckchat.channel;

import com.duckchat.crypto.DuckyPublicKey;

public class User {
	private DuckyPublicKey pubKey;
	private String name;
	
	public User(String name, DuckyPublicKey pubKey) {
		this.pubKey = pubKey;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public DuckyPublicKey getPubKey() {
		return pubKey;
	}

}
