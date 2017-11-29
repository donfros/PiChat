package PiChat;

/**
 * Class used to create a new instance of a client, allowing them to send and receive
 * messages from the server. It is multi-threaded, allowing for multiple clients to connect
 * to the server at once.
 * 
 * @author Steven D'Onfro
 */
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {
	public static String username = "";
	public static int PORT = 7777;


	private static String host = "127.0.0.1";


	private static Socket cSock = null;
	private static PrintStream ps = null;
	private static DataInputStream dis = null;
	private static BufferedReader iLine = null;
	private static boolean closed = false;

	public static void main(String[] args) {

		// Open socket and I/O streams
		try {
			cSock = new Socket(host, PORT);
			iLine = new BufferedReader(new InputStreamReader(System.in));
			ps = new PrintStream(cSock.getOutputStream());
			dis = new DataInputStream(cSock.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + host);

		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to the host " + host);
		} // end IO Catch

		// Write to the socket
		if (cSock != null && ps != null && dis != null) {
			try {

				// Create a thread to read from the server.
				new Thread(new Client()).start();
				while (!closed) {
					String input = iLine.readLine().trim();
					if (username.equals("")) { // set the user's name
												// client-side so
												// they don't have to see their
												// own
												// messages (fixed in run
												// method)
						username = input;
					} // end if
					ps.println(input);
				} // end while

				// Close socket and I/O streams
				ps.close();
				dis.close();
				cSock.close();
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			} // end catch
		} // end if
	}// end main

	/*
	 * Create a thread to read from the server. (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		// Read from socket
		String responseLine;
		try {
			while ((responseLine = dis.readLine()) != null) {

				// makes sure they don't see their own messages
				if (!responseLine.startsWith("[" + username + "]")) {
					System.out.println(responseLine); // prints what other users
														// are saying
				} // end if
			} // end while
			closed = true;
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		} // end catch
	}// end run

}// end Client
