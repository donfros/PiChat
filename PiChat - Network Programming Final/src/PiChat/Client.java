package PiChat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client implements Runnable {
// fuck this
	public final static int PORT = 7777;
	private static Socket client = null;
	private static BufferedReader is = null;
	private static BufferedReader iLine = null;
	private static DataOutputStream os = null;
	private static boolean closed = false;

	public static void main(String[] args) {

		// Open socket and initialize in/out streams
		try {
			client = new Socket("1.2.3.4", PORT);
			is = new BufferedReader(new InputStreamReader(client.getInputStream()));
			os = new DataOutputStream(client.getOutputStream());
			iLine = new BufferedReader(new InputStreamReader(System.in));

		} catch (IOException e) {
			System.out.println(e);
		}
		// write to the socket
		if (client != null && is != null && os != null) {
			try {
				new Thread(new Client()).start();
				while (!closed) {
					os.writeUTF(iLine.readLine().trim());
				}
				os.close();
				is.close();
				client.close();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}

	/**
	 * Thread reads from server. (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		String response;
		try {
			while ((response = is.readLine()) != null) {
				System.out.println(response);
				if (response.equals("Ending")) { //User types "Ending" to end connection with the server
					break;
				}
				closed = true;
			}
		} catch (IOException e) {
			System.out.println(e);
		}

	}

}
