import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.*;
import javax.swing.*;

import com.duckchat.crypto.*;
import com.duckchat.protocol.*;

/**
 * A simple Swing-based client for the chat server.
 * It has a main frame window with a text field for entering
 * strings and a textarea to see the results of capitalizing
 * them.
 */
public class ChannelClient implements Runnable{

    private ServerConnection connection;
    private DuckyKeyPair pair;
    private DuckySymmetricKey symmetricKey;
    
    private JFrame frame = new JFrame("Channel Client");
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menu = new JMenu("Admin");
    private JMenuItem menuItem = new JMenuItem("Menu Admin");
    private JTextField dataField = new JTextField(40);
    private JTextArea messageArea = new JTextArea(8, 60);
    
    private ArrayList<String> messages = new ArrayList<String>();

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
        menu.add(menuItem);
        frame.setJMenuBar(menuBar);
        
        menuItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		DuckySymmetricKey newKey = new DuckySymmetricKey();
				System.out.println("new sym key: " + newKey.encodeKey());
        		connection.send(new NewKeyMessage("chan", newKey, new DuckyPublicKey(pair.getPublicKey())));
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
	            	connection.send(new TextMessage("andrew", "chan", dataField.getText(), symmetricKey));
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
    	String addr = JOptionPane.showInputDialog(frame, "address", "duckchat", JOptionPane.QUESTION_MESSAGE);
    	
        connection = new ServerConnection(addr, 2003);
    	
		try {
			pair = new DuckyKeyPair(1024);
			symmetricKey = new DuckySymmetricKey();
			connection.send(new JoinChannelMessage("andrew", "chan", pair.getPublicKey()));
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
			
			 String response;
             try {
            	 Message m = Message.deserialize(connection.readRaw());
            	 if(m.getType().equals("text")) {
            		 TextMessage tm = (TextMessage) m;
            		 response = symmetricKey.decryptText(tm.getCipherText());
                     System.out.println("msg: " + response);
                     response = "decrypted: " + response;
            	 } else if(m.getType().equals("key")) {
            		 try {
            			NewKeyMessage km = new NewKeyMessage(m.getRawData());
						System.out.println("new sym key: " + km.decryptSymmetricKey(pair));
						response = "new key: " + km.decryptSymmetricKey(pair);
					} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
							| IllegalBlockSizeException | BadPaddingException e) {
						// TODO Auto-generated catch block
						response = "err";
						e.printStackTrace();
					}
            	 } else {
            		 response = m.serialize();
            		 System.out.println("type: " + m.getType());
            	 }
            	
//                 if (response == null || response.equals("")) {
//                       System.exit(0);
//                   }
             } catch (IOException ex) {
                    response = "Error: " + ex;
             }
             
             messages.add(response);
        	 messageArea.append(response+"\n");

		}
	}
}