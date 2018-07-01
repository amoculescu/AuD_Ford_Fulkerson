package lab;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * The class Navigation finds the shortest (and/or) path between points on a map
 * using the Dijkstra algorithm
 */
public class Navigation {
	/**
	 * Return codes: -1 if the source is not on the map -2 if the destination is
	 * not on the map -3 if both source and destination points are not on the
	 * map -4 if no path can be found between source and destination
	 */

	public static final int SOURCE_NOT_FOUND = -1;
	public static final int DESTINATION_NOT_FOUND = -2;
	public static final int SOURCE_DESTINATION_NOT_FOUND = -3;
	public static final int NO_PATH = -4;


	private ArrayList<Node> cities;


	/**
	 * The constructor takes a filename as input, it reads that file and fill
	 * the nodes and edges Lists with corresponding node and edge objects
	 * 
	 * @param nodes
	 *            name of the list containing the nodes
	 */
	public Navigation(ArrayList<Node> nodes) {
		this.cities = nodes;
	}


	/**
	 * This methods finds the shortest route (distance) between points A and B
	 * on the map given in the constructor.
	 * 
	 * If a route is found the return value is an object of type
	 * ArrayList<String>, where every element is a String representing one line
	 * in the map. The output map is identical to the input map, apart from that
	 * all edges on the shortest route are marked "bold". It is also possible to
	 * output a map where all shortest paths starting in A are marked bold.
	 * 
	 * The order of the edges as they appear in the output may differ from the
	 * input.
	 * 
	 * @param A
	 *            Source
	 * @param B
	 *            Destinaton
	 * @return returns a map as described above if A or B is not on the map or
	 *         if there is no path between them the original map is to be
	 *         returned.
	 */
	public Node findPathFlow(String A, String B, ArrayList<Node> nodes){
		this.cities = nodes;
		return findPath(A, B);
	}

	private Node findPath(String A, String B){
		//initialize nodes for searching, find start and end Nodes if existing
		//creating auxillary variables
		boolean foundStart = false;
		boolean foundEnd = false;
		boolean foundPath = false;
		Node start = null;
		Node end = null;
		PriorityQueue<Node> queue = new PriorityQueue<>(1, (Node n1, Node n2) -> n1.getDistanceToStart() > n2.getDistanceToStart() ? -1 : 1);

		//set distance to start of every node to +infinity, and previous in path to null, basically resetting
		//also looking if A and B are included in the map, finally add everything into a priority queue
		for(int i = 0; i < cities.size(); i++){
			Node currentNode = cities.get(i);
			currentNode.setDistanceToStart(Double.NEGATIVE_INFINITY);
			currentNode.setPreviousInPath(null);
			if (currentNode.getName().equals(A)){
				start = currentNode;
				start.setDistanceToStart(0);
				foundStart = true;
			}
			if(cities.get(i).getName().equals(B)){
				end = currentNode;
				foundEnd = true;
			}
			queue.offer(currentNode);
		}
		if (foundStart)
			start.updateNeighbors();
		queue.remove(start);
		//end of initialization

		//start and/or end not found
		if(!foundStart && !foundEnd)
			return new Node("null", SOURCE_DESTINATION_NOT_FOUND);
		if(!foundStart){
			return new Node("null", SOURCE_NOT_FOUND);
		}
		if(!foundEnd){
			return new Node("null", DESTINATION_NOT_FOUND);
		}//actual algorithm
		else{
			Node currentNode;
			updateQueue(start, queue);

			while(queue.size() > 0) {
				//check if there is an edge connecting the next node in the queue to the path, if not remove the item
				if(queue.peek().getPreviousInPath() == null)
					queue.poll();
				else {
					currentNode = queue.poll();
					if (currentNode == end)
						foundPath = true;
					//update distanceToStart of every node connected to ucrrent node and set previousInPath to current Node
					currentNode.updateNeighbors();
					updateQueue(currentNode, queue);
				}
			}
			if(foundPath){
				//return the last node of the path
				return end;
			}else{
				//if no path is found return a new node with the name "null" and NO_PATH as delay
				return new Node("null", NO_PATH);
			}
		}
	}

	public void updateQueue(Node n, PriorityQueue q){
		for (int i = 0; i < n.getEdges().size(); i++){
			if(q.remove(n.getEdge(i).getB()))
				q.add(n.getEdge(i).getB());
		}
	}
}
