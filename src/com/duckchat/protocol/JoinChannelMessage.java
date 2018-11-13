package com.duckchat.protocol;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;

import javafx.util.*;

public class JoinChannelMessage extends Message {

	public JoinChannelMessage(ArrayList<Pair<String, String>> data) {
		super(data, "join");	
	}
	
	public JoinChannelMessage(String name, String channel, PublicKey pubKey) {
		super(new ArrayList<>(), "join");
		
		this.data.add(new Pair<>("name", name));
		this.data.add(new Pair<>("channel", channel));
		String encoded = Base64.getEncoder().encodeToString(pubKey.getEncoded());
		this.data.add(new Pair<>("pubkey", encoded));
	}

}
