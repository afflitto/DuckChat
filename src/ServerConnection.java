import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


import com.duckchat.protocol.Message;

public class ServerConnection {
	
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	
	public ServerConnection(String address, int port) throws UnknownHostException, IOException {
        // Make connection and initialize streams
        socket = new Socket(address, port);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
	}
	
	public void send(Message msg) {
		System.out.println("Sent: " + msg.serialize());
		out.println(msg.serialize());
	}
	
	public void writeRaw(String msg) {
		out.println(msg);
	}
	
	public String readRaw() throws IOException {
		return in.readLine();
	}
	
	//New message incoming?
	public boolean available() throws IOException {
		return in.ready();
	}
	
	public void close() throws IOException {
		socket.close();
	}
	
}