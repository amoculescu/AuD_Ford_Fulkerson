package lab;

import java.util.ArrayList;

public class Node {
    private final String name;
    private final double delay;
    private ArrayList<Edge> edges;
    private boolean done;
    private double distanceToStart;
    private Node previousInPath;

    public Node (String name, int delay){
        this.name = name;
        this.delay = delay;
        this.edges = new ArrayList<>();
        this.distanceToStart = Double.POSITIVE_INFINITY;
        this.previousInPath = null;
        this.done = false;
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

    public void addEdge(Edge e){
        this.edges.add(e);
    }

    public double getDistanceToStart(){ return  this.distanceToStart; }

    public void setDistanceToStart(double newDistance){ this.distanceToStart = newDistance; }

    public Node getPreviousInPath(){ return this.previousInPath; }

    public void setPreviousInPath(Node newNode){ this.previousInPath = newNode; }

    public boolean isFinished(){ return this.done; }

    public void finished(){ this.done = true; }


    public void updateNeighbors(String type){
        if(type == "Route, Distance" || type == "Distance"){
            for(int i = 0; i < edges.size(); i++){
                Edge currentEdge = edges.get(i);
                if(this.distanceToStart + currentEdge.getDistance() < currentEdge.getB().distanceToStart) {
                    currentEdge.getB().setDistanceToStart(this.distanceToStart + currentEdge.getDistance());
                    currentEdge.getB().setPreviousInPath(this);
                }
            }
        }
        else if(type == "Time, Distance" || type == "Time"){
            for(int i = 0; i < edges.size(); i++){
                Edge currentEdge = edges.get(i);
                double edgeTime = currentEdge.getDistance() / currentEdge.getMaxSpeed() * 60;
                if(this.distanceToStart + this.delay + edgeTime < currentEdge.getB().distanceToStart) {
                    currentEdge.getB().setDistanceToStart(this.distanceToStart + this.delay + edgeTime);
                    currentEdge.getB().setPreviousInPath(this);
                }
            }
        }
    }

    public boolean isConnectedTo(Node n){
        for (int i = 0; i < edges.size(); i ++){
            if(edges.get(i).getB() == n)
                return true;
        }
        return false;
    }
}
