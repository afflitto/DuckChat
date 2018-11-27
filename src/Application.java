import java.util.ArrayList;

import com.duckchat.channel.User;
import com.duckchat.crypto.*;
import com.duckchat.protocol.Message;

public class Application {
	
	private static ArrayList<Message> messages = new ArrayList<Message>();
	public static ArrayList<User> users = new ArrayList<User>();
	private static DuckyKeyPair keyPair;
	private static DuckySymmetricKey groupKey;

	
	public static void addMessage(Message message) {
		messages.add(message);
	}
	
	public static ArrayList<Message> getMessages() {
		return Application.messages;
	}
	
	public static void addUser(User user) {
		users.add(user);
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