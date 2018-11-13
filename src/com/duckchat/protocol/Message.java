package com.duckchat.protocol;

import java.util.ArrayList;

import javafx.util.*;

public abstract class Message {
	protected ArrayList<Pair<String, String>> data;
	protected String type;	
	
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
		String type = message.split("[")[0];
		String payload = message.split("[")[1];
		
		String[] pairs = payload.split(" && ");
		ArrayList<Pair<String, String>> pairList = new ArrayList<>();
		
		for(String pair : pairs) {
			String key = pair.split(":")[0];
			String value = pair.split(":")[1];
			
			pairList.add(new Pair<>(key, value));
		}
		
		if(type == "join") {
			return new JoinChannelMessage(pairList);
		} else if(type == "text") {
			return new TextMessage(pairList);
		}
		
		return null;
	}
	
}
