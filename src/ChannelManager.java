
import com.duckchat.crypto.DuckyKeyPair;
import com.duckchat.crypto.DuckySymmetricKey;
import com.duckchat.protocol.Message;
import com.duckchat.protocol.NewKeyMessage;
import com.duckchat.protocol.TextMessage;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JTextArea;

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
            Message m = Message.deserialize(connection.readRaw());
            if (m.getType().equals("text")) {
                TextMessage tm = new TextMessage(m.getRawData());
                response = symmetricKey.decryptText(tm.getCipherText());
                System.out.println("msg: " + response);
                response = "decrypted: " + response;
            } else if (m.getType().equals("key")) {
                try {
                    NewKeyMessage km = new NewKeyMessage(m.getRawData());
                    symmetricKey = km.decryptSymmetricKey(pair);
                    System.out.println("new sym key: " + symmetricKey.encodeKey());
                    response = "new sym key: " + symmetricKey.encodeKey();
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