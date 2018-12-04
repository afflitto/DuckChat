package com.duckchat.protocol;

import com.duckchat.channel.User;
import com.duckchat.crypto.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import javafx.util.Pair;

@SuppressWarnings("restriction")
public class UserListMessage extends Message {

	public UserListMessage(ArrayList<Pair<String, String>> data) {
		super(data, "list");
	}

	public UserListMessage(String name, String channel, User[] users) {
		super(new ArrayList<>(), "list");

		this.data.add(new Pair<>("users", "" + users.length));
		for (int i = 0; i < users.length; i++) {
			this.data.add(new Pair<>("user#" + i, users[i].getName() + "|" + users[i].getPubKey().encode()));
		}

	}

	public int getNumberOfUsers() {
		for (Pair<String, String> pair : data) {
			if (pair.getKey().equals("users")) {
				return Integer.valueOf(pair.getValue());
			}
		}
		return -1;
	}

	public User findUser(int i) {
		if (i >= 0 && i < getNumberOfUsers()) {
			for (Pair<String, String> pair : data) {
				if (pair.getKey().equals("user#" + i)) {
					String username = pair.getValue().split("|")[0];
					String key = pair.getValue().split("|")[1];

					try {
						return new User(username, new DuckyPublicKey(key));
					} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	public User[] getUserList() {
		User[] list = new User[getNumberOfUsers()];
		for (int i = 0; i < list.length; i++) {
			list[i] = findUser(i);
		}
		return list;

	}

	public String getName() {
		for (Pair<String, String> pair : data) {
			if (pair.getKey().equals("name")) {
				return pair.getValue();
			}
		}
		return null;
	}
}
