package PiChat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

			try

			{

				in = new DataInputStream(clientSocket.getInputStream());
				out = new PrintStream(clientSocket.getOutputStream());
				String username;

				while (true) {
					out.println("Enter your username: ");
					username = in.readLine().trim();
					if (username.indexOf('@') == -1) { /// Don't get point
														/// of this do we
														/// need it??
						break;
					}
					
				}

				out.println(username + " has entered the piChat to leave enter ");

				synchronized (this) {
					int i = 0;
					while (i < MAX_USERS) {

						if (threads[i] != null) {
							if (threads[i] == this) {
								clientUserName = "@" + username;
								break;

							}

						}

						i++;

					}

					for (int i1 = 0; i1 < MAX_USERS; i1++) {
						if (threads[i1] != null && threads[i1] != this) {
							threads[i1].out.println("New Person in chat: " + username);
						}	

					}
				}
				  while (true) {
				        String line = in.readLine();
				        if (line.startsWith("/quit")) {
				          break;
				        }
				
				
				  

				synchronized (this) {   ///Public method 
					int i = 0;
					while (i < MAX_USERS) {

						if (threads[i] != null) {
							if (threads[i].clientUserName != null) {
								 threads[i].out.println("<" + username + "> " + line);
							}
						}

						i++;
					}

				}

				synchronized (this) {
					int i = 0;
					while (i < MAX_USERS) {

						if (threads[i] != null) {
							if (threads[i] != this) {
								if (threads[i].clientUserName != null) {

									threads[i].out.println(username + " has left the PiChat");
								}

							}
						}

						i++;
					}

				}

				synchronized (this) {
					int i = 0;
					while (i < MAX_USERS) {

						if (threads[i] == this) {

							threads[i] = null;
						}

						i++;
					}

				}

				in.close();
				out.close();
				clientSocket.close();

			} 
				  }catch (IOException e) {

			}

		}

	}
	}

