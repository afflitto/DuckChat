package server;

import java.util.ArrayList;

public class ClientHandler implements IClientHandler {

	public ArrayList<EchoServer> users = new ArrayList<EchoServer>();
	public ArrayList<String> messageQueue = new ArrayList<String>();

	public ClientHandler() {

	}

	public synchronized void updateUsers(String message) {
		for (EchoServer s : users) {
			s.newMessageQueue.add(message);
		}
	}

	public void parseMessage(String message, EchoServer from) {
		if (message.contains("join")) {
			users.add(from);
			userJoined(message);
		} else if (message.contains("text")) {
			messageRecieved(message);
		}
	}

	public void messageRecieved(String messageString) {
		updateUsers(messageString);
	}

	public void userJoined(String joinMessage) {
		String username = joinMessage.substring(joinMessage.indexOf(":") + 1, joinMessage.indexOf("&&")).trim();
		System.out.println(username + " Joined!");
		updateUsers(joinMessage);
	}

	public void userLeft(String leaveMessage) {

	}

}
