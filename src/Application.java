import java.util.ArrayList;

import com.duckchat.channel.User;
import com.duckchat.crypto.*;
import com.duckchat.protocol.Message;

public class Application {

	private static ArrayList<Message> messages = new ArrayList<Message>(); //list to hold messages
	public static ArrayList<User> users = new ArrayList<User>(); //list to hold users and pubkeys
	private static DuckyKeyPair keyPair; //this client's public/private key pair
	private static DuckySymmetricKey groupKey; //current group symmetric key

	public static void addMessage(Message message) {
		messages.add(message);
	}

	public static ArrayList<Message> getMessages() {
		return Application.messages;
	}

	public static void addUser(User user) {
		users.add(user);
	}

	public static void removeUser(String username) {
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getName().equals(username)) {
				users.remove(i);
				return;
			}
		}
	}

	public static void setKeyPair(DuckyKeyPair keyPair) {
		Application.keyPair = keyPair;
	}

	public static DuckyKeyPair getKeyPair() {
		return Application.keyPair;
	}

	public static void setGroupKey(DuckySymmetricKey groupKey) {
		Application.groupKey = groupKey;
	}

	public static DuckySymmetricKey getGroupKey() {
		return Application.groupKey;
	}

}
