package com.Player.chatMultiThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Player {

	static Set<Player> playerHandlers = Collections.synchronizedSet(new HashSet<Player>());

	public Player() {
		playerHandlers.add(this);
	}

	public static void main(String[] args) {
		try {

			// BufferedReader keyRead = new BufferedReader(new
			// InputStreamReader(System.in));

			// getting localhost ip
			InetAddress ip = InetAddress.getByName("localhost");

			// establish the connection with server port 5056
			Socket s = new Socket(ip, 5056);

			// obtaining input and out streams
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());

			int sentMessageCount = 0;
			int receivedMessageCount = 0;
			String messageToSent = null;
			String receivedMessage = null;

			if (args.length > 0) {
				messageToSent = args[0];
			}
			// the following loop performs the exchange of
			// information between player and player handler
			while (sentMessageCount < 11 && receivedMessageCount < 11) {
				synchronized (s) {

					if (messageToSent == null) {
						try {
							receivedMessage = dis.readUTF();
						} catch (EOFException e) {
							s.close();
							break;
						}
						receivedMessageCount++;
						System.out.println("player: " + s.getLocalPort() + ",received: " + receivedMessage);
						messageToSent = receivedMessage + sentMessageCount;
					}
					if (sentMessageCount == 10 && receivedMessageCount == 10) {
						s.close();
						break;
					}
					s.wait(1000);
					System.out.println("player: " + s.getLocalPort() + ",sent: " + messageToSent);
					dos.writeUTF(messageToSent);
					sentMessageCount++;
					if (sentMessageCount == 10 && receivedMessageCount == 10) {
						s.close();
						break;
					}
					s.wait(1000);

					messageToSent = null;

				}

			}
			dis.close();
			dos.close();

		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

}
