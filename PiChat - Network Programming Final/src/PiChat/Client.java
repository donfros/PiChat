package PiChat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client implements Runnable {

	public final static int PORT = 7777;
	private static Socket client = null;
	private static DataInputStream is = null;
	private static BufferedReader iLine = null;
	private static BufferedReader inStream = null;
	private static DataOutputStream os = null;
	private static boolean closed = false;

	public static void main(String[] args) {

		try {
			client = new Socket("1.2.3.4", PORT);
			is = new DataInputStream(client.getInputStream());
			inStream = new BufferedReader(new InputStreamReader(System.in));
			os = new DataOutputStream(client.getOutputStream());
			iLine = new BufferedReader(new InputStreamReader(System.in));

		} catch (IOException e) {
			System.out.println(e);
		}

		try {
			os.close();
			is.close();
			client.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * Thread reads from sever. (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		String response;
		try {
			while ((response = inStream.readLine()) != null) {
				System.out.println(response);
				if (response.equals("Ending")) {
					break;
				}
				closed = true;
			}
		} catch (IOException e) {
			System.out.println(e);
		}

	}

}
