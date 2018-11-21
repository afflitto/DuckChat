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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.duckchat.crypto.DuckyKeyPair;
import com.duckchat.crypto.DuckyPublicKey;
import com.duckchat.crypto.DuckySymmetricKey;
import com.duckchat.protocol.JoinChannelMessage;
import com.duckchat.protocol.NewKeyMessage;
import com.duckchat.protocol.TextMessage;
import com.duckchat.protocol.DebugMessage;


/**
 * A simple Swing-based client for the chat server.
 * It has a main frame window with a text field for entering
 * strings and a text area to see the results of capitalizing
 * them.
 */
public class ChannelClient implements Runnable{

    private ServerConnection connection;
    private ChannelManager manager;

    
    private JFrame frame = new JFrame("Channel Client");
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menu = new JMenu("Admin");
    private JMenuItem keyGenItem = new JMenuItem("Generate new Group Key");
    private JMenuItem closeServerItem = new JMenuItem("Close Server");
    private JTextField dataField = new JTextField(40);
    private JTextArea messageArea = new JTextArea(60, 120);
    


    /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Enter in the
     * listener sends the textfield contents to the server.
     */
    public ChannelClient() {

        // Layout GUI
        messageArea.setEditable(false);
        frame.getContentPane().add(dataField, "South");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        
        menuBar.add(menu);
        menu.add(keyGenItem);
        menu.add(closeServerItem);
        frame.setJMenuBar(menuBar);
        
        manager = new ChannelManager(messageArea);
        
        keyGenItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		DuckySymmetricKey newKey = new DuckySymmetricKey();
				System.out.println("new sym key: " + newKey.encodeKey());
        		connection.send(new NewKeyMessage("chan", newKey, new DuckyPublicKey(manager.getPair().getPublicKey())));
        	}
        });
        closeServerItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		connection.send(new DebugMessage(0,"chan",manager.getSymmetricKey()));
        	}
        });

        // Add Listeners
        dataField.addActionListener(new ActionListener() {
            /**
             * Responds to pressing the enter key in the textfield
             * by sending the contents of the text field to the
             * server and displaying the response from the server
             * in the text area.  If the response is "." we exit
             * the whole application, which closes all sockets,
             * streams and windows.
             */
        	
        	
        	
            public void actionPerformed(ActionEvent e) {
            	String cipherText;
				try {
	            	connection.send(new TextMessage("andrew", "chan", dataField.getText(), manager.getSymmetricKey()));
	                dataField.setText("");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

            }
        });
    }

    /**
     * Implements the connection logic by prompting the end user for
     * the server's IP address, connecting, setting up streams, and
     * consuming the welcome messages from the server.  The Channel
     * protocol says that the server sends three lines of text to the
     * client immediately after establishing a connection.
     */
    public void connectToServer() throws IOException {
    	//InetAddress serverAddress = InetAddress.getLocalHost();
    	//String addr = JOptionPane.showInputDialog(frame, "address", "duckchat", JOptionPane.QUESTION_MESSAGE);
        connection = new ServerConnection("localhost", 2003);
    	
		try {
			manager.setDuckyKeyPair(new DuckyKeyPair(1024));
			manager.setSymmetricKey(new DuckySymmetricKey());
			connection.send(new JoinChannelMessage("andrew"+(int)(Math.random()*10), "chan", manager.getPair().getPublicKey()));
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
		
		while(true) {
			
                    manager.parseResponse(connection);
             

		}
	}
}