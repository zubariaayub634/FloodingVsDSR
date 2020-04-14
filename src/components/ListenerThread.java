package components;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/*
 * maqsad of ListenerThread: receive input from a socket and put it in a queue, the owner examines the queue when they free
 *
 * */
public class ListenerThread extends Thread {
	private DataInputStream in;
	private Queue<String> out;
	byte[] msg;

	Node bossNode;
	Socket inSocket;

	public Queue<String> getOut() {
		return out;
	}

	public ListenerThread(Node bossNode, Socket inputSocket) throws IOException {
		this.bossNode = bossNode;
		this.in = new DataInputStream(inputSocket.getInputStream());
		this.out = new LinkedList<String>();
		this.msg = new byte[1000000];
		this.inSocket = inputSocket;

		System.out.print("I am listening for Node with ID: ");
		System.out.print(bossNode.getId());
		System.out.print(" at port ");
		System.out.println(inSocket.getLocalPort());
	}

	public boolean execution() {
		try {
			String array=(new DataInputStream(in)).readUTF(); // read from stream
			if (Math.random() > bossNode.packetDrop)
				out.add(array); // throw it in the queue
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void run() {
		while (execution())
			;
	}
}
