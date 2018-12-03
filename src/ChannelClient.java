import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.duckchat.channel.User;
import com.duckchat.crypto.*;
import com.duckchat.protocol.JoinChannelMessage;
import com.duckchat.protocol.LeaveChannelMessage;
import com.duckchat.protocol.NewKeyMessage;
import com.duckchat.protocol.TextMessage;
import com.duckchat.protocol.DebugMessage;

/**
 * A simple Swing-based client for the chat server. It has a main frame window
 * with a text field for entering strings and a text area to see the results of
 * capitalizing them.````````
 */

public class ChannelClient implements Runnable {

	private ServerConnection connection;
	private ChannelManager manager;

	private String username = "";
	private String channel = "";

	private JFrame frame = new JFrame("Channel Client");
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menu = new JMenu("Admin");
	private JMenuItem keyGenItem = new JMenuItem("Generate new Group Key");
	private JMenuItem closeServerItem = new JMenuItem("Close Server");
	private JMenuItem listUsersItem = new JMenuItem("List users");
	private JTextField dataField = new JTextField(40);
	private JTextArea messageArea = new JTextArea(60, 120);

	/**
	 * Constructs the client by laying out the GUI and registering a listener with
	 * the textfield so that pressing Enter in the listener sends the textfield
	 * contents to the server.
	 */
	public ChannelClient() {
//gather user info
		username = JOptionPane.showInputDialog("What is your name?");
		channel = JOptionPane.showInputDialog("What Channel would you like to join?");

		// Layout GUI
		messageArea.setEditable(false);
		frame.getContentPane().add(dataField, "South");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");

		menuBar.add(menu);
		menu.add(keyGenItem);
		menu.add(closeServerItem);
		menu.add(listUsersItem);
		frame.setJMenuBar(menuBar);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		manager = new ChannelManager(messageArea);

		keyGenItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DuckySymmetricKey newKey = new DuckySymmetricKey();
				System.out.println("new sym key: " + newKey.encodeKey());
				for (User user : Application.users) {
					NewKeyMessage message = new NewKeyMessage(channel, newKey, user.getPubKey());
					connection.send(message);
				}
				// connection.send(new NewKeyMessage(channel, newKey, new
				// DuckyPublicKey(manager.getPair().getPublicKey())));
			}
		});
		closeServerItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connection.send(new DebugMessage(0, channel, manager.getSymmetricKey()));
			}
		});
		listUsersItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Users:");
				messageArea.append("Users:");
				for (User user : Application.users) {
					System.out.println("\t" + user.getName() + ":" + user.getPubKey().encode());
					messageArea.append("\t" + user.getName() + ":" + user.getPubKey().encode() + "");
				}
				System.out.println();
				messageArea.append("\n");
			}
		});

		// Add Listeners
		dataField.addActionListener(new ActionListener() {
			/**
			 * Responds to pressing the enter key in the textfield by sending the contents
			 * of the text field to the server and displaying the response from the server
			 * in the text area. If the response is "." we exit the whole application, which
			 * closes all sockets, streams and windows.
			 */

			public void actionPerformed(ActionEvent e) {
				try {
					connection.send(new TextMessage(username, channel, dataField.getText(), manager.getSymmetricKey()));
					dataField.setText("");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to close this window?", "Close Window?",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					connection.send(new LeaveChannelMessage(username, channel, manager.getPair().getPublicKey()));
				}
			}
		});
	}

	/**
	 * Implements the connection logic by prompting the end user for the server's IP
	 * address, connecting, setting up streams, and consuming the welcome messages
	 * from the server. The Channel protocol says that the server sends three lines
	 * of text to the client immediately after establishing a connection.
	 */
	public void connectToServer() throws IOException {
		// InetAddress serverAddress = InetAddress.getLocalHost();
		// String addr = JOptionPane.showInputDialog(frame, "address", "duckchat",
		// JOptionPane.QUESTION_MESSAGE);
		//connection = new ServerConnection("192.168.1.251", 2003);
		connection = new ServerConnection("35.196.228.4", 2003);
		// 35.196.228.4

		try {
			manager.setDuckyKeyPair(new DuckyKeyPair(1024));
			manager.setSymmetricKey(new DuckySymmetricKey());
			connection.send(new JoinChannelMessage(username, channel, manager.getPair().getPublicKey()));
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Runs the client application.
	 */
	public static void main(String[] args) throws Exception {
		ChannelClient client = new ChannelClient();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.pack();
		client.frame.setVisible(true);
		client.connectToServer();
		Thread t = new Thread(client);
		t.start();
	}

	public void run() {
		System.out.println("I'm Connected!");

		while (true) {

			manager.parseResponse(connection);

		}
	}
}