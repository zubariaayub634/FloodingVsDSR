package preprocessing;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

import components.Node;

public class TopologyGenerator {

	ArrayList<Node> networkNodes;

	TopologyGenerator(int noOfNodes, double nodeMobility, double packetDrop) throws Exception {
		networkNodes = new ArrayList<Node>(noOfNodes);

		// get machine's IP
		InetAddress IPAdress = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());

		ArrayList<Socket> allNodes = new ArrayList<Socket>();

		Integer portNo = 5040;

		for (int i = 0; i < noOfNodes; i++) {
			Socket socket = new Socket();
			while (true) {
				try {
					socket = new Socket(IPAdress, portNo);
					System.out.println();
					break;
				} catch (Exception e) {
					// System.out.print(e);
					System.out.print(".");
					portNo++;
					if (65535 == portNo)
						portNo = 5000;
				}
			}
			allNodes.add(socket);
		}
		
		ForkJoinPool pool = new ForkJoinPool();
		
		for (int i = 0; i < noOfNodes; i++) {
			networkNodes.add(new Node(i, allNodes.get(i), new ArrayList<Socket>(allNodes), nodeMobility, packetDrop));
			networkNodes.get(i).displayNodeDetails();
			pool.invoke(networkNodes.get(i));
		}
	}

	public static void main(String[] args) throws Exception {
		 TopologyGenerator tg = new TopologyGenerator(10, 0.2, 0.2);
		
		//Node fb = new Node(0, new Socket(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()),5040), new ArrayList<Socket>(), 0.20, 0.2);
		
		//ForkJoinPool pool = new ForkJoinPool();
		
		//pool.invoke(fb);
	}
}