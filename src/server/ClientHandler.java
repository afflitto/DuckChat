package server;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.duckchat.channel.User;
import com.duckchat.crypto.DuckyPublicKey;
import com.duckchat.protocol.JoinChannelMessage;
import com.duckchat.protocol.Message;

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

		JoinChannelMessage m = new JoinChannelMessage(Message.deserialize(joinMessage).getRawData());

		u = new User(m.getName(), m.getPublicKey());
		return u;
	}

	public void userLeft(String leaveMessage) {

	}

}
