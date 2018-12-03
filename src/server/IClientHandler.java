package server;


public interface IClientHandler {
	void messageRecieved(String messageString);

	void userJoined(String joinMessage);

	void userLeft(String leaveMessage);
	
	

}
