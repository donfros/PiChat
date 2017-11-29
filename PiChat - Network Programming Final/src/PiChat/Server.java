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
	public static final int MAX_USERS = 10;
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
	 * Allows for multi-threading for multiple users to join a server and talk
	 * to eachother in realtime
	 * 
	 * @author Evan Goyuk
	 * @author Sam Partlow
	 * 
	 *
	 */
	public static class clientThread extends Thread {
		// private String[] commands = { "/list", "/exit", "/help" };
		private String clientUsername = null;
		private DataInputStream in = null;
		private PrintStream out = null;
		private int MAX_USERS;
		private final clientThread[] threads;
		private Socket cTSock = null;

		/**
		 * default constructor
		 * 
		 * @param client
		 *            socket of client currently at
		 * @param threads
		 *            array of spots for clients to fill
		 */

		public clientThread(Socket client, clientThread[] threads) {
			this.cTSock = client;
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

				in = new DataInputStream(cTSock.getInputStream());
				out = new PrintStream(cTSock.getOutputStream());
				String username;
				while (true) {
					out.println("Please enter your username: ");
					username = in.readLine().trim();
					break;
				}

				// displays welcome message to client once they have joined the
				// server
				out.println(
						"----------------------------------------------------------------------------------------------------------------------------\n");
				out.println(
						"\tPPPPPPPPPPPPPPPPP     iiii         CCCCCCCCCCCCChhhhhhh                                       tttt\n"
								+ "\tP::::::::::::::::P   i::::i     CCC::::::::::::Ch:::::h                                    ttt:::t\n"
								+ "\tP::::::PPPPPP:::::P   iiii    CC:::::::::::::::Ch:::::h                                    t:::::t\n"
								+ "\tPP:::::P     P:::::P         C:::::CCCCCCCC::::Ch:::::h                                    t:::::t\n"
								+ "          P::::P     P:::::Piiiiiii C:::::C       CCCCCC h::::h hhhhh         aaaaaaaaaaaaa  ttttttt:::::ttttttt\n"
								+ "          P::::P     P:::::Pi:::::iC:::::C               h::::hh:::::hhh      a::::::::::::a t:::::::::::::::::t\n"
								+ "	  P::::PPPPPP:::::P  i::::iC:::::C               h::::::::::::::hh    aaaaaaaaa:::::at:::::::::::::::::t\n"
								+ "	  P:::::::::::::PP   i::::iC:::::C               h:::::::hhh::::::h            a::::atttttt:::::::tttttt\n"
								+ "	  P::::PPPPPPPPP     i::::iC:::::C               h::::::h   h::::::h    aaaaaaa:::::a      t:::::t\n"
								+ "	  P::::P             i::::iC:::::C               h:::::h     h:::::h  aa::::::::::::a      t:::::t\n"
								+ "	  P::::P             i::::iC:::::C               h:::::h     h:::::h a::::aaaa::::::a      t:::::t\n"
								+ "	  P::::P             i::::i C:::::C       CCCCCC h:::::h     h:::::ha::::a    a:::::a      t:::::t    tttttt\n"
								+ "	PP::::::PP          i::::::i C:::::CCCCCCCC::::C h:::::h     h:::::ha::::a    a:::::a      t::::::tttt:::::t\n"
								+ "	P::::::::P          i::::::i  CC:::::::::::::::C h:::::h     h:::::ha:::::aaaa::::::a      tt::::::::::::::t\n"
								+ "	P::::::::P          i::::::i    CCC::::::::::::C h:::::h     h:::::h a::::::::::aa:::a       tt:::::::::::tt\n"
								+ "	PPPPPPPPPP          iiiiiiii       CCCCCCCCCCCCC hhhhhhh     hhhhhhh  aaaaaaaaaa  aaaa         ttttttttttt\n");
				out.println(
						"----------------------------------------------------------------------------------------------------------------------------\n");
				out.println("\t\t\t\t\tWelcome to the PiChat server, " + username
						+ "!\n\t\t\t\tType '/exit' to exit, and '/help' for a list of commands");
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

					// commands
					if (line.equals("/exit")) {
						break;
					} else if (line.equals("/list") && userCount == 1) {
						out.print("------------------------------------------------\n");
						out.print("There is currently " + userCount + " user in the PiChat Server\n");
						for (int i = 0; i < MAX_USERS; i++) {
							if (threads[i] != null) {
								out.print(threads[i].getUsername() + "\n");
							}
						}
						out.print("------------------------------------------------\n");
					} else if (line.equals("/list") && userCount > 1) {
						out.print("------------------------------------------------\n");
						out.print("There are currently " + userCount + " users in the PiChat Server\n");
						for (int i = 0; i < MAX_USERS; i++) {
							if (threads[i] != null) {
								out.print(threads[i].getUsername() + "\n");
							}
						}
						out.print("------------------------------------------------\n");
					}
					if (line.equals("/help")) {
						out.print(
								"/list\nDisplays the number of users in the chatroom and their names."
								+ "\n/exit\nCommand to leave server.\n");
					}
					if (line.equals("/kick")) {
						String userToKick;
						out.print("Who would you like to kick?");
						userToKick = in.readLine();
						for (int i = 0; i < MAX_USERS; i++) {
							if (threads[i] != null && threads[i].getUsername().equals(userToKick)) {
								threads[i] = null;
								out.println(userToKick + " has been removed from the server");
							}
						}
					}

					// Displays message to all clients
					synchronized (this) {
						for (int i = 0; i < MAX_USERS; i++) {
							if (!line.startsWith("/")) {
								if (threads[i] != null && threads[i].clientUsername != null) {
									threads[i].out.println("[" + username + "] " + line);

								}
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
							userCount--;
						}
					}
				}

				// closes input,output, and socket
				in.close();
				out.close();
				cTSock.close();
			} catch (IOException e) {
			}

		}

		private String getUsername() {
			return this.clientUsername;
		}

	}
}
