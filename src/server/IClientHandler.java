package server;

import com.duckchat.channel.User;

public interface IClientHandler {
	void messageRecieved(String messageString);

	void userJoined(User u);

	void userLeft(String leaveMessage);

}
