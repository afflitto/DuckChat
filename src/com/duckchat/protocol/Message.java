package com.duckchat.protocol;

import java.util.ArrayList;


import javafx.util.*;

@SuppressWarnings("restriction")
public class Message {
	protected ArrayList<Pair<String, String>> data;
	protected String type = "";
	
	public Message(ArrayList<Pair<String, String>> pairs, String type) {
		this.data = pairs;
		this.type = type;
	}
	
	public String serialize() {
		String message = type;
		message += "[";
		for(Pair<String, String> pair : data) {
			message += pair.getKey();
			message += ":";
			message += pair.getValue();
			message += " && ";
		}
		message += "]";
		return message;
	}
	
	public static Message deserialize(String message) {
		String s = "join[name:Matt && channel:chan && pubkey:MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCOWKPx1vItEWPub9tg3Cm2re7lcZJj7ZdkdEWwARLyLo6BN/mfAcpbBd9XGDYQ9du1FEtwyjemATJ9q/xB80Eo2A02efreIfvi+ALbUMAk1UkURbJ9HxFN8OjhS7XmOTbe2i6/BXuITCU+O++lVrFj0adzNcZ0mUPEKjoLZ79etwIDAQAB && ]";
		System.out.println("DEBUG - deserializing: " + message);
		System.out.println(s.length());
		System.out.println("count: " + message.length());
		
		String type = message.split("\\[")[0];
		String payload = message.split("\\[")[1];
		
		String[] pairs = payload.split(" && ");
		ArrayList<Pair<String, String>> pairList = new ArrayList<>();
		
		for(String pair : pairs) {
			if(pair.split(":").length > 1) {
				String key = pair.split(":")[0];
				String value = pair.split(":")[1];
				
				pairList.add(new Pair<>(key, value));
			}
		}
		
		return new Message(pairList, type);
	}
	
	public String getType() {
		return this.type;
	}
	
	public ArrayList<Pair<String, String>> getRawData() {
		return data;
	}
	
}
