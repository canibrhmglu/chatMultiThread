package com.Player.chatMultiThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Server {

	static Set<Socket> sockets = Collections.synchronizedSet(new HashSet<Socket>());

	public static void main(String[] args) throws IOException {
		// server is listening on port 5056
		@SuppressWarnings("resource")
		ServerSocket ss = new ServerSocket(5056);

		// running infinite loop for getting
		// client request
		while (true) {
			Socket s = null;

			try {
				// socket object to receive incoming client requests
				s = ss.accept();

				System.out.println("A new player is connected : " + s);

				// obtaining input and out streams
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());

				System.out.println("Assigning new thread for this player");

				// create a new thread object
				PlayerHandler t = new PlayerHandler(s, dis, dos);

				// Invoking the start() method
				t.start();

			} catch (Exception e) {
				sockets.forEach(socket -> {
					try {
						socket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
				e.printStackTrace();
			}
		}
	}

}
