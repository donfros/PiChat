package PiChat;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author Sam Partlow
 *
 */
public class Server {

	private static Socket cSock = null;
	public static final int PORT = 7777;
	private static ServerSocket sSock = null;
	public static final int MAX_USERS = 4;
	public static final clientThread[] clients = new clientThread[MAX_USERS];

	public static void main(String[] args) {
		try {

			sSock = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println(e);
		}
		while (true) {
			try {
				cSock = sSock.accept();
				int i = 0;
				for (i = 0; i < MAX_USERS; i++) {
					if (clients[i] == null) {
						(clients[i] = new clientThread(cSock, clients)).start();
						break;
					}
				}
				if (i == MAX_USERS) {
					PrintStream os = new PrintStream(cSock.getOutputStream());
					os.println("Server is full. Please try again later.");
					os.close();
					cSock.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}

	}

	/**
	 * 
	 * @author Evan Goyuk
	 *
	 */
	public static class clientThread extends Thread {

		private String clientUserName = null;
		private DataInputStream in = null;
		private PrintStream out = null;
		private Socket client = null;
		private int MAX_USERS;
		private final clientThread[] threads;

		public clientThread(Socket client, clientThread[] threads) {
			this.client = client;
			this.threads = threads;
			MAX_USERS = threads.length;
		}

		public void run() {

			int MAX_USERS = this.MAX_USERS;
			clientThread[] threads = this.threads;

			try {

				// Makees input/output streams for each client

				in = new DataInputStream(cSock.getInputStream());
				out = new PrintStream(cSock.getOutputStream());
				String username;
				while (true) {
					out.println("Please enter your username: ");
					username = in.readLine().trim();
					break;
				}

				// displays welcome message to client once they have joined the
				// server
				out.println("Welcome to the PiChat Server " + username + "!\nType '/exit' to exit!");
				synchronized (this) {
					for (int i = 0; i < MAX_USERS; i++) {
						if (threads[i] != null && threads[i] == this) {
							clientUserName =  username;
							break;
						}
					}
					for (int i = 0; i < MAX_USERS; i++) {
						if (threads[i] != null && threads[i] != this) {
							threads[i].out.println(username + " has entered the PiChat Server!");
						}
					}
				}
				// allows users to talk back and forth 
				while (true) {
					String line = in.readLine();
					if (line.startsWith("/exit")) {
						break;
					}
					// Displays message to all clients
					synchronized (this) {
						for (int i = 0; i < MAX_USERS; i++) {
							if (threads[i] != null && threads[i].clientUserName != null) {
								threads[i].out.println("[" + username + "] " + line);
							}
						}
					}
				}
				synchronized (this) {
					for (int i = 0; i < MAX_USERS; i++) {
						if (threads[i] != null && threads[i] != this && threads[i].clientUserName != null) {
							threads[i].out.println(username + " has left the chatroom.");
						}
					}
				}
				out.println("Goodbye " + username);

				
				
				// Deletes client once they exit the server
				synchronized (this) {
					for (int i = 0; i < MAX_USERS; i++) {
						if (threads[i] == this) {
							threads[i] = null;
						}
					}
				}
				
				
				// closes input,output, and socket
				in.close();
				out.close();
				cSock.close();
			} catch (IOException e) {
			}

		}

	}
}
