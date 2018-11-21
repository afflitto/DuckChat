import java.security.PublicKey;
import java.util.Base64;

public class Protocol {
	public static String joinChannel(String name, PublicKey pubKey, String channel) {
		String encodedPubKey = Base64.getEncoder().encodeToString(pubKey.getEncoded());
		return "[name:" + name + " && channel:" + channel + " && pubkey:" + encodedPubKey + "]";
	}
	
	public static String message(String name, String cipherText) {
		return "[name:" + name + " && timestamp:" + "0" + " && cipherText:" + cipherText + "]";
	}
	
	public static String adminSetKey(String key) {
		return "[key:" + key + "]";
	}
        
        public static String adminDebug(int messageFlag) {
		return "[debug:" + messageFlag + "]";
	}
}