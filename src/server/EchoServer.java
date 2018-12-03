package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class EchoServer extends Thread {

	protected static boolean serverContinue = true;
	protected Socket clientSocket;
	static int port = 2003;
	static ServerSocket serverSocket = null;
	static ClientHandler h;

	public static void main(String[] args) throws IOException {

		h = new ClientHandler();

		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Connection Socket Created");
			try {
				while (serverContinue) {
					System.out.println("Waiting for Connection");
					new EchoServer(serverSocket.accept());
				}
			} catch (IOException e) {
				if (serverContinue) {
					System.err.println("Accept failed.");
					System.exit(1);
				}

			}
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port + ".");
			System.exit(1);
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.err.println("Could not close port: " + port + ".");
				System.exit(1);
			}
		}
	}

	private EchoServer(Socket clientSoc) {
		clientSocket = clientSoc;
		start();
	}

	public ArrayList<String> newMessageQueue = new ArrayList<String>();
	PrintWriter out;
	BufferedReader in;

	public void run() {

		System.out.println("New Communication Thread Started");
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null && serverContinue) {

				if (inputLine.contains("debug")) {
					if (inputLine.contains("flag:0")) {
						serverContinue = false;
						System.out.println("Closing");

						new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									serverSocket.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}).start();
					}
				}

				System.out.println("Server: " + inputLine);
				h.parseMessage(inputLine, this);
				// This should trigger newMessageQueue being populated by inputLine, as well as
				// other messages sent by other client stubs, so when new messages come in, IE
				// parseMesssage is called, we should wait for this call to finish, then flush
				// newMessageQueue , this is done by the clienthandler
				// Nice!

			}

			out.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			if (serverContinue) {
				System.err.println("Problem with Communication Server");
				System.exit(1);

			}
		}

	}

	public void flushMessageQueue() {
		for (String s : newMessageQueue) {
			out.println(s);
		}
		newMessageQueue.clear();
	}
}
