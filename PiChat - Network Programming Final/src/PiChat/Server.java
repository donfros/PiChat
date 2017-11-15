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

	public class clientThread {   /// will need edits eclipse is not showing errors for me 

		private String clientUserName = null;
		private DataInputStream in = null;
		private printStream out = null;
		private Socket client = null;

		public clientThread(socket client, clientThread[] threads) {
			this.client = client;
			this.threads = threads;
			MAX_USERS = threads.length;
		}

		@Override
		public void run() {
			
		 int MAX_USERS = this.MAX_USERS
				clientThread[] threads = this.threads;

		}

		try

		{
			
			in = new DataInputStream(clientSocket.getInputStream());
			out = new PrintStream(clientSocket.getOutputStream());
			String username;
			
			while(true){
				out.println("Enter your username: ")
				if (username.indexOf('@') == -1){ ///Don't get point of this do we need it??
					break;
				}
			}
			
			out.println(username + " has entered the piChat to leave enter ")
			
			synchronized(this){
				int i=0;
				while (i < MAX_USERS){
					
					if (threads[i] != null ){
						if (threads[i] == this){
							
							clientUserName = usename;
							break;
							
						}
						
						
						
						
					}
						
					
					i++;
					
					
				}
		
				 for (int i = 0; i < MAX_USERS; i++) {
			          if (threads[i] != null && threads[i] != this) {
			            threads[i].os.println("New Person in chat: " + name);
			          }
				
				}
			}
				
				
			synchronized(this){
				int i=0;
				while (i < MAX_USERS){
					
					if (threads[i] != null ){
						if (threads[i].clienUserName != null){
							
							threads[i].out.println(userName)
							
						}
					}
						
					i++;
				}
		
				}
			
			
			
			
			synchronized(this){
				int i=0;
				while (i < MAX_USERS){
					
					if (threads[i] != null ){
						if (threads[i] != this){
							if (threads[i].clienUserName != null){
								
							
							
							threads[i].out.println(UserName + " has left the PiChat");
							}
							
						}
					}
						
					i++;
				}
		
				}
			
			
			
			
			
			synchronized(this){
				int i=0;
				while (i < MAX_USERS){
					
					if (threads[i] == this ){
						
						threads[i] = null;
					}
						
					i++;
				}
		
				}
			
			
			
			in.close();
			out.close;
			clientSockect.close();
			
		
			
			
				
				
			
			
			
			
			
			
			
			
			
		} catch(IOException e){
			
		}

	}

}