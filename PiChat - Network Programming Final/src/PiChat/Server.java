package PiChat;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author partlows this is aids
 *
 */
public class Server {

	/// test
	private static Socket clientSocket = null;
	public static final int PORT = 7777;
	private static ServerSocket serverSocket = null;
	public static final int MAX_USERS = 4;
	public static final clientThread[] threads = new clientThread[MAX_USERS];

	public static void main(String[] args) {
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
				int i = 0;
				for (i = 0; i < MAX_USERS; i++) {
					if (threads[i] == null) {
						(threads[i] = new clientThread(clientSocket, threads)).start();
						break;
					}
				}
				if (i == MAX_USERS) {
					PrintStream os = new PrintStream(clientSocket.getOutputStream());
					os.println("Server is full. Please try again later.");
					os.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}

	}

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
			      /*
			       * Create input and output streams for this client.
			       */
			      in = new DataInputStream(clientSocket.getInputStream());
			      out = new PrintStream(clientSocket.getOutputStream());
			      String username;
			      while (true) {
			        out.println("Enter your username.");
			        username = in.readLine().trim();
			        break;
			      }

			      
			      out.println("Welcome " + username + " to our chat room.\nTo leave enter /quit in a new line.");
			
			      synchronized (this) {
			        for (int i = 0; i < MAX_USERS; i++) {
			          if (threads[i] != null && threads[i] == this) {
			            clientUserName = "@" + username;
			            break;
			          }
			        }
			        for (int i = 0; i < MAX_USERS; i++) {
			          if (threads[i] != null && threads[i] != this) {
			            threads[i].out.println("*** A new user " + username
			                + " entered the chat room !!! ***");
			          }
			        }
			      }
			      /* Start the conversation. */
			      while (true) {
			        String line = in.readLine();
			        if (line.startsWith("/quit")) {
			          break;
			        }
			          /* The message is public, broadcast it to all other clients. */
			          synchronized (this) {
			            for (int i = 0; i < MAX_USERS; i++) {
			              if (threads[i] != null && threads[i].clientUserName != null) {
			                threads[i].out.println("<" + username + "> " + line);
			              }
			            }
			          }
			        }
			      synchronized (this) {
			        for (int i = 0; i < MAX_USERS; i++) {
			          if (threads[i] != null && threads[i] != this
			              && threads[i].clientUserName != null) {
			            threads[i].out.println("*** The user " + username
			                + " is leaving the chat room !!! ***");
			          }
			        }
			      }
			      out.println("*** Bye " + username + " ***");

			      /*
			       * Clean up. Set the current thread variable to null so that a new client
			       * could be accepted by the server.
			       */
			      synchronized (this) {
			        for (int i = 0; i < MAX_USERS; i++) {
			          if (threads[i] == this) {
			            threads[i] = null;
			          }
			        }
			      }
			      /*
			       * Close the output stream, close the input stream, close the socket.
			       */
			      in.close();
			      out.close();
			      clientSocket.close();
			    } catch (IOException e) {
			    }

		}

	}
}
