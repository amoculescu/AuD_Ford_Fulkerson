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
	 *            name of the file containing the input map
	 */
	public Navigation(ArrayList<Node> nodes) {
		this.cities = nodes;
	}

	public ArrayList<Node> getCities(){ return this.cities; }

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
		return findShortestRoute(A, B);
	}

	public Node findShortestRoute(String A, String B) {
		if (A != B) {
			Node result = findPath(A, B, "Route, Distance");
			if (result.getName() != "ToD")
				return result;
		}
		return null;
	}

	/**
	 * This methods finds the fastest route (in time) between points A and B on
	 * the map given in the constructor.
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
	public ArrayList<String> findFastestRoute(String A, String B) {
		if(A != B) {
			Node result = findPath(A, B, "Time, Distance");
			if (result.getName() != "null" && result.getName() != "ToD")
				return makeMap(result);
		}
		return makeMap(null);
	}

	/**
	 * Finds the shortest distance in kilometers between A and B using the
	 * Dijkstra algorithm.
	 *
	 * @param A
	 *            the start point A
	 * @param B
	 *            the destination point B
	 * @return the shortest distance in kilometers rounded upwards.
	 *         SOURCE_NOT_FOUND if point A is not on the map
	 *         DESTINATION_NOT_FOUND if point B is not on the map
	 *         SOURCE_DESTINATION_NOT_FOUND if point A and point B are not on
	 *         the map NO_PATH if no path can be found between point A and point
	 *         B
	 */
	public int findShortestDistance(String A, String B) {
		if(A != B) {
			Node result = findPath(A, B, "Distance");
			return evaluateTimeAndDistance(result, B);
		}
		return 0;
	}

	/**
	 * Find the fastest route between A and B using the dijkstra algorithm.
	 *
	 * @param pointA
	 *            Source
	 * @param pointB
	 *            Destination
	 * @return the fastest time in minutes rounded upwards. SOURCE_NOT_FOUND if
	 *         point A is not on the map DESTINATION_NOT_FOUND if point B is not
	 *         on the map SOURCE_DESTINATION_NOT_FOUND if point A and point B
	 *         are not on the map NO_PATH if no path can be found between point
	 *         A and point B
	 */
	public int findFastestTime(String pointA, String pointB) {
		if(pointA != pointB) {
			Node result = findPath(pointA, pointB, "Time");
			return evaluateTimeAndDistance(result, pointB);
		}
		return 0;
	}

	public int evaluateTimeAndDistance(Node result, String B){
		if(result.getName() == "null")
			return (int)Math.ceil(result.getDelay());
		else if (result.getName() == B)
			return 0;
		else
			return (int)Math.ceil(result.getDelay());
	}

	public Node findPath(String A, String B, String bla){
		//initialize nodes for searching, find start and end Nodes if existing
		boolean foundStart = false;
		boolean foundEnd = false;
		boolean foundPath = false;
		Node start = null;
		Node end = null;
		PriorityQueue<Node> queue = new PriorityQueue<Node>(1, (Node n1, Node n2) -> n1.getDistanceToStart() > n2.getDistanceToStart() ? -1 : 1);

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
		    start.updateNeighbors(bla);
		queue.remove(start);

		//end of initialization

		//start and/or end not found
		if(!foundStart && !foundEnd)
			return new Node("null", -3);
		if(!foundStart){
			return new Node("null", -1);
		}
		if(!foundEnd){
			return new Node("null", -2);
		}//actual algorithm
		else{
			Node currentNode;
			updateQueue(start, queue);

			while(queue.size() > 0) {
				if(queue.peek().getPreviousInPath() == null)
					queue.poll();
				else {
					currentNode = queue.poll();
					if (currentNode == end)
						foundPath = true;
					currentNode.updateNeighbors(bla);
					updateQueue(currentNode, queue);
				}
			}
			if(foundPath){
				if(bla == "Distance")
					return new Node("ToD", (int)Math.ceil(end.getDistanceToStart()));
				else if(bla == "Time")
					return new Node("ToD", (int)Math.ceil(end.getDistanceToStart() - start.getDelay()));
				return end;

			}else{
				return new Node("null", -4);
			}
		}
	}

	public void updateQueue(Node n, PriorityQueue q){
		for (int i = 0; i < n.getEdges().size(); i++){
			if(q.remove(n.getEdge(i).getB()))
				q.add(n.getEdge(i).getB());
		}
	}

	/**
	 * creates a String map of an Arraylist. If endpoint is specified/ not null it will create a map with the
	 * shortest path to the end point
	 * @param endpoint
	 * 				if algorithm found a shortest path this is the end point of that path else null
	 * @return
	 * 		Arraylist of strings of the map
	 */
	public ArrayList<String> makeMap(Node endpoint){
		//no endpoint found
		if(endpoint == null){
			ArrayList<String> map = new ArrayList<>();
			String currentLine = "Digraph {";
			Node currentNode;
			Edge currentEdge;
			map.add(currentLine);
			for(int i = 0; i < cities.size(); i ++){
				currentNode = cities.get(i);
				for(int j = 0; j < currentNode.getEdges().size(); j++) {
					currentEdge = currentNode.getEdge(j);
					map.add(map.size() - i, currentNode.getName() + " -> " + currentEdge.getB().getName() + " [label=\" " + (int)currentEdge.getFlow() + "," + (int)currentEdge.getResidualFlow() + "\"];");
				}
				map.add(currentNode.getName() + " [label=\"" + currentNode.getName() + "\"];");
			}
			map.add("}");
			return map;
		}else {
			Node currentNode = endpoint;
			ArrayList<Node> path = new ArrayList<>();
			while(currentNode != null){
				path.add(currentNode);
				currentNode = currentNode.getPreviousInPath();
			}

			ArrayList<String> map = new ArrayList<>();
			Edge currentEdge;
			map.add("Digraph {");
			boolean partOfPath = false;
			for(int i = 0; i < cities.size(); i ++) {
				currentNode = cities.get(i);
				if (path.contains(currentNode))
					partOfPath = true;
				else
					partOfPath = false;
				for (int j = 0; j < currentNode.getEdges().size(); j++) {
					currentEdge = currentNode.getEdge(j);
					if (partOfPath && path.contains(currentEdge.getB()) && currentEdge.getB().getPreviousInPath() == currentNode)
						map.add(map.size() - i, currentNode.getName() + " -> " + currentEdge.getB().getName() + " [label=\"" + (int)currentEdge.getFlow() + "," + (int)currentEdge.getResidualFlow() + "\"][style=bold];");
					else
						map.add(map.size() - i, currentNode.getName() + " -> " + currentEdge.getB().getName() + " [label=\"" + (int)currentEdge.getFlow() + "," + (int)currentEdge.getResidualFlow() + "\"];");
				}
				if (partOfPath)
					map.add(currentNode.getName() + " [label=\"" + currentNode.getName()+ "\"][style=bold];");
				else
					map.add(currentNode.getName() + " [label=\"" + currentNode.getName()+ "\"];");
			}
			map.add("}");
			return map;
		}
	}
}
