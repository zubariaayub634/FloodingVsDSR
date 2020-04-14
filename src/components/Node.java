package components;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.RecursiveAction;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Node extends RecursiveAction {
	Integer id;

	Socket socket;

	ListenerThread assistant;
	NonblockingBufferedReader systemInput;

	ArrayList<Socket> allAddresses;

	HashSet<Integer> neighbors;
	double nodeMobility; // % chance that when receiving a packet, the node will change its connections
	double packetDrop; // % chance that when receiving a packet, the node will drop the packet

	public Node(Integer id, Socket socket, ArrayList<Socket> allAddresses, double nodeMobility, double packetDrop)
			throws Exception {

		// initialize node attributes
		this.allAddresses = new ArrayList<Socket>(allAddresses);
		this.socket = socket;
		neighbors = new HashSet<Integer>();
		connectToNeighbors();
		this.nodeMobility = nodeMobility;
		this.packetDrop = packetDrop;
		this.id = id;

		assistant = new ListenerThread(this, socket);
		systemInput = new NonblockingBufferedReader(new BufferedReader(new InputStreamReader(System.in)));

		displayNodeDetails();
	}

	public Integer getId() {
		return id;
	}

	void connectToNeighbors() {
		Integer sqrt = (int) Math.sqrt(allAddresses.size());
		Integer neighborCount = (int) Math.random() * sqrt;
		for (int i = 0; i < neighborCount; i++) {
			Integer randNo = (int) (Math.random() * allAddresses.size());
			neighbors.add(randNo);
		}
	}

	public void displayNodeDetails() {
		System.out.print("Id: ");
		System.out.print(id);
		System.out.print(", IP: ");
		System.out.print(socket.getInetAddress().toString());
		System.out.print(", port: ");
		System.out.print(socket.getLocalPort());
		System.out.print(", Neighbors: {");
		Iterator<Integer> iter = neighbors.iterator();
		while (iter.hasNext()) {
			System.out.print(Integer.toString((int) iter.next()) + ", ");
		}
		System.out.print("}, Node Mobility: ");
		System.out.print(nodeMobility);
		System.out.print(", Packet Drop Rate: ");
		System.out.println(packetDrop);
	}

	public void floodMessage(String message) {
		Iterator<Integer> iter = neighbors.iterator();
		while (iter.hasNext()) {
			try {
				(new DataOutputStream(allAddresses.get(iter.next()).getOutputStream())).writeUTF(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void execution() throws IOException {
		System.out.print("Enter a message you want to send: ");
		while (true) {
			// check if any input from console
			String a = systemInput.readLine();
			if (a != null)
				System.out.println(a);
			// check if any msg in queue
			try {
				String msg = assistant.getOut().remove();
				System.out.println("-------msg received-------\n"+msg+"--------------------------\n");
			} catch (NoSuchElementException e) {
				continue;
			}
		}
	}

/*	public static void main(String[] args) throws IOException {
		System.out.println("I am a Node");
	}
*/

	@Override
	protected void compute() {
		// TODO Auto-generated method stub
		System.out.println("I am a Node");
	}
}
