package lab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MaxFlow.java
 */


public class MaxFlow {

	public ArrayList<Node> nodes;
	public Navigation pathfinder;
	/**
	 * Return codes:
	 * 		-1 no source on the map
	 * 		-2 no destination on the map
	 * 		-3 if both source and destination points are not on the map
	 * 		-4 if no path can be found between source and destination
	 * 	MAXINT if sources identical to destinations
	 */
	public static final int NO_SOURCE_FOUND = -1;
	public static final int NO_DESTINATION_FOUND = -2;
	public static final int NO_SOURCE_DESTINATION_FOUND = -3;
	public static final int NO_PATH = -4;
	public static final int SOURCES_SAME_AS_DESTINATIONS = Integer.MAX_VALUE;
	
	/**
	 * The constructor, setting the name of the file to parse.
	 * 
	 * @param filename the absolute or relative path and filename of the file
	 */
	public MaxFlow(final String filename) {
		this.nodes = new ArrayList<>();
		ArrayList<String> nodeNames = new ArrayList<>();
        Pattern pattern = Pattern.compile("[^->\"\\[\\s]+");
        Matcher matcher;

		try{
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			String currentLine = br.readLine();
			boolean finished = false;
			//create nodes and edges

			while(!finished) {
                if(currentLine.matches(".* -> .*")) {
                    matcher = pattern.matcher(currentLine);
                    matcher.find();
                    String start = matcher.group();
                    matcher.find();
                    String end = matcher.group();
                    int maxFlow = Integer.parseInt(currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.indexOf("]") - 1));

                    if (!nodeNames.contains(start) && !nodeNames.contains(end)) {
                        Node startNode = new Node(start, 0);
                        Node endNode = new Node(end, 0);
                        Edge connection = new Edge(startNode, endNode, maxFlow, 0);

                        startNode.addEdge(connection);
                        nodeNames.add(start);
                        nodes.add(startNode);
                        nodeNames.add(end);
                        nodes.add(endNode);
                    } else if (!nodeNames.contains(start) && nodeNames.contains(end)) {
                        Node startNode = new Node(start, 0);
                        Node endNode = findNode(end);
                        Edge connection = new Edge(startNode, endNode, maxFlow, 0);

                        startNode.addEdge(connection);
                        nodeNames.add(start);
                        nodes.add(startNode);
                    } else if (nodeNames.contains(start) && !nodeNames.contains(end)) {
                        Node startNode = findNode(start);
                        Node endNode = new Node(end, 0);
                        Edge connection = new Edge(startNode, endNode, maxFlow, 0);

                        startNode.addEdge(connection);
                        nodeNames.add(end);
                        nodes.add(endNode);
                    } else {
                        Node startNode = findNode(start);
                        Node endNode = findNode(end);
                        Edge connection = new Edge(startNode, endNode, maxFlow, 0);
                        startNode.addEdge(connection);
                    }
                }
                currentLine = br.readLine();
                if (currentLine == null)
                    finished = true;

            }
            this.pathfinder = new Navigation(nodes);

		} catch (IOException e){e.printStackTrace();}
	}

	private Node findNode(String nodeName){
	    for (int i = 0; i < nodes.size(); i++){
	        if(nodes.get(i).getName().equals(nodeName))
	            return nodes.get(i);
        }
        return null;
    }
	
	/**
	 * Calculates the maximum number of cars able to travel the graph specified
	 * in filename.
	 *
	 * @param sources a list of all source nodes
	 * @param destinations a list of all destination nodes
	 * @return 	the maximum number of cars able to travel the graph,
	 * 			NO_SOURCE_FOUND if no source is on the map
	 * 			NO_DESTINATION_FOUND if no destination is on the map
	 * 			NO_SOURCE_DESTINATION_FOUND if both - no source and no destination - are not on the map
	 * 			NO_PATH if no path can be found
	 * 			SOURCES_SAME_AS_DESTINATIONS if sources == destinations 
	 */
	public final int findMaxFlow(final String[] sources, final String[] destinations) {
		//TODO Add you code here
        if(Arrays.equals(sources, destinations)){
            return SOURCES_SAME_AS_DESTINATIONS;
        } else{
            Node startNode;
            String sourceName = sources[0];
            String sinkName = destinations[0];

            //check if sources/sinks exists and if multiple sources/sinks, create "superSource/-Sink"
            boolean sourceNotFound = false;
            if (sources.length > 1){
                sourceNotFound = addSuperNode("superSource", sources);
                sourceName = "superSource";
            }else
                sourceNotFound = findNode(sourceName) == null;
            boolean destinationNotFound = false;
            if (destinations.length > 1){
                destinationNotFound = addSuperNode("superSink", destinations);
                sinkName = "superSink";
            }else
                destinationNotFound = findNode(sinkName) == null;

            if(sourceNotFound && destinationNotFound)
                return NO_SOURCE_DESTINATION_FOUND;
            else if (sourceNotFound)
                return NO_SOURCE_FOUND;
            else if (destinationNotFound)
                return NO_DESTINATION_FOUND;

            //set flow to 0
            setZero();
            ArrayList<Node> path = new ArrayList<>();
            startNode = findNode(sourceName);
            //find first path, if no path found return NO_PATH
            Node pathNode = pathfinder.findPathFlow(sourceName, sinkName, nodes);
            if (pathNode.getName().equals("null") && pathNode.getDelay() == -4)
                return NO_PATH;
            //main loop;
            while (pathNode.getName() != "null") {
                double flow = Double.POSITIVE_INFINITY;
                path.clear();
                //find minimum residual capacity in path as new flow
                while (pathNode != null) {
                    path.add(pathNode);
                    if (pathNode != startNode && pathNode.getEdgeToPrevious().getResidualFlow() < flow)
                        flow = pathNode.getEdgeToPrevious().getResidualFlow();
                    pathNode = pathNode.getPreviousInPath();
                }
                //add min(new flow. residual capacity) to old flow, and subtract same from residual capacity
                for (int i = 0; i < path.size(); i++) {
                    if (path.get(i) != startNode) {
                        path.get(i).getEdgeToPrevious().setFlow(path.get(i).getEdgeToPrevious().getFlow() + Math.min(flow, path.get(i).getEdgeToPrevious().getResidualFlow()));
                        path.get(i).getEdgeToPrevious().setResidualFlow(path.get(i).getEdgeToPrevious().getResidualFlow() - Math.min(flow, path.get(i).getEdgeToPrevious().getResidualFlow()));
                    }
                }
                //find next path
                pathNode = pathfinder.findPathFlow(sourceName, sinkName, nodes);
            }
            //after algorithm is done maximum flow is the summation of all the outgoing flows of all the sources/destinations
            return startNode.getOutgoingFlow();
        }
	}

    /**
     * if multiple sources or destinations, insert a single source or destination as source/destination
     * @param sourceOrDestination
     *      string defining whether source or destination is inserted
     * @param sourceOrDestinationArray
     *      array containing the sources or destinations
     * @return
     *      boolean if all the sources or destinations were found
     */
    private boolean addSuperNode(String sourceOrDestination, String[] sourceOrDestinationArray){
        boolean dsNotFound = false;
        Node superNode = new Node(sourceOrDestination, 0);
        for(int i = 0; i < sourceOrDestinationArray.length; i ++){
            //ds = find the node with the source/destination name
            Node ds = findNode(sourceOrDestinationArray[i]);
            //if destination and exists make edge from destination to "superSink"
            if(ds != null && sourceOrDestination.equals("superSink")) {
                Edge newEdge = new Edge(ds, superNode, Integer.MAX_VALUE, 0);
                ds.addEdge(newEdge);
            }
            //if source and exists make edge from "superSource" to source
            else if(ds != null && sourceOrDestination.equals("superSource")){
                Edge newEdge = new Edge(superNode, ds, Integer.MAX_VALUE, 0);
                superNode.addEdge(newEdge);
            }
            // either source or destination not found
            else {
                dsNotFound = true;
            }
        }
        //add new nodes at index 0;
        nodes.add(0, superNode);
        return dsNotFound;
    }

    /**
     * initialization of the edges, set residiual flow to maximum capacity, and set flow to 0
     */
	private void setZero(){
	    for(int i = 0; i < nodes.size(); i++){
	        Node currentNode = nodes.get(i);
	        for (int j = 0; j < currentNode.getEdges().size(); j++){
	            Edge currentEdge = currentNode.getEdge(j);
	            currentEdge.setResidualFlow(currentEdge.getFlow());
	            currentEdge.setFlow(0);
            }
        }
    }

	/**
	 * Calculates the graph showing the maxFlow.
	 *
	 * @param sources a list of all source nodes
	 * @param destinations a list of all destination nodes
	 * @return a ArrayList of Strings as specified in the task in dot code
	 */
	public final ArrayList<String> findResidualNetwork(final String[] sources,	final String[] destinations) {
		findMaxFlow(sources, destinations);
        if (destinations.length > 1)
            nodes.remove(0);
        if(sources.length > 1)
            nodes.remove(0);
        ArrayList<String> map = new ArrayList<>();
        String currentLine = "Digraph {";
        Node currentNode;
        Edge currentEdge;
        map.add(currentLine);
        for (int i = 0; i < nodes.size(); i++) {
            currentNode = nodes.get(i);
            for (int j = 0; j < currentNode.getEdges().size(); j++) {
                currentEdge = currentNode.getEdge(j);
                currentLine = currentNode.getName() + " -> " + currentEdge.getB().getName() + "[label=\"" + (int) (currentEdge.getFlow() + currentEdge.getResidualFlow()) + "-" + (int) currentEdge.getFlow() + "\"]";
                if (currentEdge.getResidualFlow() > 0)
                    currentLine += "[style=bold];";
                else
                    currentLine += ";";
                map.add(1, currentLine);
            }
        }
        for(int i = 0; i < sources.length; i ++){
            map.add(sources[i] + "[shape=doublecircle][style=bold];");
        }
        for(int i = 0; i < destinations.length; i ++){
            map.add(destinations[i] + "[shape=circle][style=bold];");
        }
        map.add("}");
        return map;
    }
}