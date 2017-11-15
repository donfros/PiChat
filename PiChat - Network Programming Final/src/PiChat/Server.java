package PiChat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

	private static Socket clientSocket = null;
	public static final int PORT = 7777;
	private static ServerSocket serverSocket = null;
	private static final int MAX_USERS = 4;
	private static final clientThread[] threads = new clientThread[MAX_USERS];

	public static void main(String[] args) throws IOException {
		String clientSentence;
		String serverSentence;
		// Register service on port 12345
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println(e);
		}
		while (true) {
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		// Get a communication stream associated with the socket
		OutputStream s1out = clientSocket.getOutputStream();
		InputStream s1In = clientSocket.getInputStream();
		DataOutputStream dos = new DataOutputStream(s1out);
		DataInputStream dis = new DataInputStream(s1In);

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		// exchange messages until the client ends the connection
		do {
			serverSentence = inFromUser.readLine();
			dos.writeUTF(serverSentence);
			if (serverSentence.equals("end"))
				break;

			clientSentence = dis.readUTF();
			System.out.println("Client: " + clientSentence);
			if (clientSentence.equals("/quit")) {
				break;
			}

		} while (true);
		System.out.println("This session has ended.");
		// Close the connection, but not the server socket
		dis.close();
		dos.close();
		s1out.close();
		clientSocket.close();

	}

	public class clientThread {

	}

	@Override
	public void run() {

	}

}
