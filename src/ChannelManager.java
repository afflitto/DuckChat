import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JTextArea;

import com.duckchat.channel.User;
import com.duckchat.crypto.DuckyKeyPair;
import com.duckchat.crypto.DuckySymmetricKey;
import com.duckchat.protocol.DebugMessage;
import com.duckchat.protocol.JoinChannelMessage;
import com.duckchat.protocol.LeaveChannelMessage;
import com.duckchat.protocol.Message;
import com.duckchat.protocol.NewKeyMessage;
import com.duckchat.protocol.TextMessage;
import com.duckchat.protocol.UserListMessage;

/**
 * Handles the incoming messages from the server
 *
 * @author matthew
 */
public class ChannelManager {

	private ArrayList<String> messages = new ArrayList<String>();

	private JTextArea messageArea = null;

	private DuckySymmetricKey symmetricKey;

	public DuckySymmetricKey getSymmetricKey() {
		return symmetricKey;
	}

	public void setSymmetricKey(DuckySymmetricKey symmetricKey) {
		this.symmetricKey = symmetricKey;
	}

	private DuckyKeyPair pair;

	public DuckyKeyPair getPair() {
		return pair;
	}

	public void setDuckyKeyPair(DuckyKeyPair pair) {
		this.pair = pair;
	}

	public ChannelManager(JTextArea messageArea) {
		this.messageArea = messageArea;
	}

	public void parseResponse(ServerConnection connection) {
		String response = "";
		try {
			if (connection.available()) { // message available
				System.out.println("New message available");
				Message m = Message.deserialize(connection.readRaw());

				if (m != null) {
					// "Text" message
					if (m.getType().equals("text")) {
						TextMessage tm = new TextMessage(m.getRawData());
						response = symmetricKey.decryptText(tm.getCipherText());
						System.out.println("msg: " + response);
						response = tm.getName() + ": " + response;

						// "Key" message
					} else if (m.getType().equals("key")) {
						try {
							NewKeyMessage km = new NewKeyMessage(m.getRawData());
							symmetricKey = km.decryptSymmetricKey(pair);
							System.out.println("--New Group Key-- (" + symmetricKey.encodeKey() + ")");
							response = "--New Group Key-- (" + symmetricKey.encodeKey() + ")";
						} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
								| IllegalBlockSizeException | BadPaddingException e) {
							System.out.println("unable to decrypt key, ignoring");
						}

						// "Join" message
					} else if (m.getType().equals("join")) {
						try {
							JoinChannelMessage jm = new JoinChannelMessage(m.getRawData());
							User joinedUser = new User(jm.getName(), jm.getPublicKey());
							Application.addUser(joinedUser);
							response = joinedUser.getName() + " has joined.";
						} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
							response = "Error!";
							e.printStackTrace();
						}

						// "Leave" message
					} else if (m.getType().equals("leave")) {
						try {
							LeaveChannelMessage lm = new LeaveChannelMessage(m.getRawData());
							User leftUser = new User(lm.getName(), lm.getPublicKey());
							Application.removeUser(lm.getName());
							response = leftUser.getName() + " has left.";
						} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
							response = "Error!";
							e.printStackTrace();
						}
					} else if (m.getType().equals("list")) {
						UserListMessage ulm = new UserListMessage(m.getRawData());
						User[] list = ulm.getUserList();
						Application.setUserList(list);
						response = "NULL";

						// "Debug" message
					} else if (m.getType().equals("debug")) {
						DebugMessage dm = new DebugMessage(m.getRawData());
						if (dm.getDebugFlag() == 0) {
							System.out.println("Server closed successfully");
							// Don't expect more messages
							System.exit(0);
						}

						// unknown type, print message literal
					} else {
						response = "debug: " + m.serialize();
					}
				}
				if (!response.equals("")) {
					messages.add(response);
					messageArea.append(response + "\n");
				}
			}
		} catch (

		IOException ex) {
			messages.add("Error: " + ex.getMessage());
			messageArea.append("Error: " + ex.getMessage());
		}
	}
}
