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

public class Server {

	public static void main(String[] args) throws IOException {
		String clientSentence;
		String serverSentence;

		// Register service on port 12345
		ServerSocket s = new ServerSocket(45555);
		Socket s1 = s.accept(); // Wait and accept a connection
		// Get a communication stream associated with the socket
		OutputStream s1out = s1.getOutputStream();
		InputStream s1In = s1.getInputStream();
		DataOutputStream dos = new DataOutputStream(s1out);
		DataInputStream dis = new DataInputStream(s1In);

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		// exchange messages until the client ends the connection
		do {
			serverSentence = inFromUser.readLine();
			dos.writeUTF(serverSentence);
			if (serverSentence.equals("end")) 
				break;
			

			clientSentence = dis.readUTF();
			System.out.println("Client: " + clientSentence);
			if (clientSentence.equals("end")) {
				break;
			}

		} while (true);
		System.out.println("This session has ended.");
		// Close the connection, but not the server socket
		dis.close();
		dos.close();
		s1out.close();
		s1.close();

	}

}
