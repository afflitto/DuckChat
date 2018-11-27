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
		System.out.println("DEBUG - deserializing: " + message);
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
