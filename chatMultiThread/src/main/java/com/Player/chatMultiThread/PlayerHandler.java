package com.Player.chatMultiThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class PlayerHandler extends Thread {

	static Set<PlayerHandler> playerHandlers = Collections.synchronizedSet(new HashSet<PlayerHandler>());

	private Socket s;
	private DataInputStream dis;
	private DataOutputStream dos;

	private static synchronized Set<PlayerHandler> getPlayerHandlers() {
		return playerHandlers;
	}

	// Constructor
	public PlayerHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
		this.s = s;
		this.dis = dis;
		this.dos = dos;
		playerHandlers.add(this);
	}

	@Override
	public void run() {

		while (getPlayerHandlers().size() == 2) {

			Iterator<PlayerHandler> playerHandler = getPlayerHandlers().iterator();
			while(playerHandler.hasNext()) {
				PlayerHandler pHandler = playerHandler.next();
				synchronized (this) {
					if (this.s.getPort() > pHandler.s.getPort()) {
						try {
							String readMessage;
							if ((readMessage = this.dis.readUTF()) != null)
								pHandler.dos.writeUTF(readMessage);
							this.wait(1000);
							this.dos.writeUTF(pHandler.dis.readUTF());
							this.wait(1000);
						} catch (IOException | InterruptedException | ConcurrentModificationException e) {
							try {
								this.s.close();
								playerHandlers.clear();
								this.dis.close();
								this.dos.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			}
		}

	}

}
