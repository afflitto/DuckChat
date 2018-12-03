package server;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.duckchat.channel.User;
import com.duckchat.crypto.DuckyPublicKey;

public class ClientHandler implements IClientHandler {

	public Map<User, EchoServer> users = new HashMap<User, EchoServer>();

	public ArrayList<String> messageQueue = new ArrayList<String>(); // holds all messages in the server starting with

	public ClientHandler() {

	}

	public EchoServer[] getStubs() {
		EchoServer[] clientStub = new EchoServer[users.size()];
		int i = 0;
		for (User u : users.keySet()) {
			clientStub[i] = users.get(u);
			i++;
		}
		return clientStub;
	}

	public synchronized void updateUsers(String message) {
		messageQueue.add(message);
		for (EchoServer s : getStubs()) {
			s.newMessageQueue.add(message);
			s.flushMessageQueue();
		}

	}

	public void parseMessage(String message, EchoServer from) {
		if (message.contains("join")) {
			User u = null;
			try {
				u = getUserFromJoin(message);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				e.printStackTrace();
			}
			users.put(u, from);
		} else if (message.contains("text")) {
			// nothing special
		}

		messageRecieved(message);

	}

	public void userJoined(User u) {
		System.out.println("User Joined: " + u.getName());
	}

	public void messageRecieved(String messageString) {
		updateUsers(messageString);
	}

	public User getUserFromJoin(String joinMessage) throws NoSuchAlgorithmException, InvalidKeySpecException {
		User u;
		String username = joinMessage.substring(joinMessage.indexOf(":") + 1, joinMessage.indexOf("&&")).trim();
		String key = joinMessage.substring(joinMessage.indexOf("pubkey:") + new String("pubkey:").length() + 1,
				joinMessage.indexOf("&&")).trim();
		u = new User(username, new DuckyPublicKey(key));
		return u;
	}

	public void userLeft(String leaveMessage) {

	}

}
