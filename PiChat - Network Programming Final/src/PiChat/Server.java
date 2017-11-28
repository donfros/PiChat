package PiChat;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server class creates and hosts the PiChat server
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
	public static int userCount = 0;

	/**
	 * Establishes the port (7777), and opens sockets for users, and allows the
	 * users to join until the capacity is reached. If the server is full, it
	 * displays a message informing the user that the server is full, and to try
	 * joining again later.
	 * 
	 * @param args
	 *            -ignored-
	 */
	public static void main(String[] args) {
		// establishes port 7777
		try {

			sSock = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println(e);
		}

		/*
		 * opens socket connection and allows users to join until the server is
		 * full
		 */
		while (true) {
			try {
				cSock = sSock.accept();
				int i = 0;
				for (i = 0; i < MAX_USERS; i++) {
					if (clients[i] == null) {
						(clients[i] = new clientThread(cSock, clients)).start();
						userCount++;
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
	 * Allows for multi-threading for multiple users to join a server and talk to eachother in realtime
	 * @author Evan Goyuk
	 * 
	 *
	 */
	public static class clientThread extends Thread {
		private String[] commands = { "/list", "/exit", "/help" };
		private String clientUsername = null;
		private DataInputStream in = null;
		private PrintStream out = null;
		private int MAX_USERS;
		private final clientThread[] threads;

		/**
		 * default constructor
		 * 
		 * @param client 
		 * 		socket of client currently at
		 * @param threads
		 *      array of spots for clients to fill
		 */

		public clientThread(Socket client, clientThread[] threads) {
			this.threads = threads;
			MAX_USERS = threads.length;
		}

		/**
		 * sets up the server for each client and allows users to talk to
		 * eachother (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */

		public void run() {

			int MAX_USERS = this.MAX_USERS;
			clientThread[] threads = this.threads;

			try {

				// Makes input/output streams for each client

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
							clientUsername = username;
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
					} else if (line.startsWith("/list")) {
						out.print("------------------------------------------------\n");
						out.print("There are currently " + userCount + " users in the PiChat Server\n");
						for (int i = 0; i < MAX_USERS; i++) {
							if (threads[i] != null) {
								out.print(threads[i].getUsername() + "\n");
							}
						}
						out.print("------------------------------------------------\n");
					} else if (line.startsWith("/help")) {
						out.print("HELP\n");
					}
					// Displays message to all clients
					synchronized (this) {
						for (int i = 0; i < MAX_USERS; i++) {

							if (threads[i] != null && threads[i].clientUsername != null) {
								threads[i].out.println("[" + username + "] " + line);

							}
						}
					}
				}
				synchronized (this) {
					for (int i = 0; i < MAX_USERS; i++) {
						if (threads[i] != null && threads[i] != this && threads[i].clientUsername != null) {
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

		private String getUsername() {
			return this.clientUsername;
		}

	}
}
