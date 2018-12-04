package server;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.duckchat.channel.User;
import com.duckchat.protocol.JoinChannelMessage;
import com.duckchat.protocol.LeaveChannelMessage;
import com.duckchat.protocol.Message;

public class ClientHandler {

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
		} else if (message.contains("leave")) {
			User u = null;
			try {
				u = getUserFromLeave(message);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				e.printStackTrace();
			}
			users.remove(u);

		} else if (message.contains("text")) {
			// nothing special
		}

		updateUsers(message);

	}

	public User getUserFromJoin(String joinMessage) throws NoSuchAlgorithmException, InvalidKeySpecException {
		User u;

		JoinChannelMessage m = new JoinChannelMessage(Message.deserialize(joinMessage).getRawData());

		u = new User(m.getName(), m.getPublicKey());
		return u;
	}

	public User getUserFromLeave(String leaveMessage) throws NoSuchAlgorithmException, InvalidKeySpecException {
		User u;

		LeaveChannelMessage m = new LeaveChannelMessage(Message.deserialize(leaveMessage).getRawData());

		u = new User(m.getName(), m.getPublicKey());
		return u;
	}

	public void userLeft(String leaveMessage) {

	}

	public void sendUserList(EchoServer clientStub) {
		for (User u : users.keySet()) {
			clientStub.newMessageQueue.add(u.getName() + "|" + u.getPubKey().encode());
			clientStub.flushMessageQueue();
		}
	}

}
