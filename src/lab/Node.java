package lab;

import java.util.ArrayList;

public class Node {
    private final String name;
    private final double delay;
    private ArrayList<Edge> edges;
    private Edge edgeToPrevious;
    private double distanceToStart;
    private Node previousInPath;

    public Node (String name, int delay){
        this.name = name;
        this.delay = delay;
        this.edges = new ArrayList<>();
        this.distanceToStart = Double.POSITIVE_INFINITY;
        this.previousInPath = null;
    }

    public String getName(){
        return this.name;
    }

    public double getDelay(){
        return this.delay;
    }

    public ArrayList<Edge> getEdges(){
        return this.edges;
    }

    public Edge getEdge(int index){
        return this.edges.get(index);
    }

    public Edge getEdgeToPrevious() { return edgeToPrevious; }

    public void addEdge(Edge e){
        this.edges.add(e);
    }

    public double getDistanceToStart(){ return  this.distanceToStart; }

    public void setDistanceToStart(double newDistance){ this.distanceToStart = newDistance; }

    public Node getPreviousInPath(){ return this.previousInPath; }

    public void setPreviousInPath(Node newNode){ this.previousInPath = newNode; }


    //add up all the outgoing flows
    public int getOutgoingFlow(){
        int flow = 0;
        for (int i = 0; i < edges.size(); i++) {
            flow += edges.get(i).getFlow();
        }
        return flow;
    }

    /**set distance to start of this nodes neighbors(nods connected with edges from this node) to the edges residual flow;
     * set previous in path of the neighbors to this node and
     * set the neighbors edgeToPrevious to the edge leading from this node to the neighbor
     */
    public void updateNeighbors(){
        for(int i = 0; i < edges.size(); i++){
            Edge currentEdge = edges.get(i);
            if(currentEdge.getResidualFlow() > 0) {
                currentEdge.getB().setDistanceToStart(currentEdge.getResidualFlow());
                currentEdge.getB().setPreviousInPath(this);
                currentEdge.getB().edgeToPrevious = currentEdge;
            }
        }
    }
}
