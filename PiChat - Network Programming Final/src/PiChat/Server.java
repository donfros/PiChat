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
	}

	public class clientThread {

	}

	@Override
	public void run() {

	}
}