package lab;

import java.util.ArrayList;

public class Edge {
    private final Node b;
    private final Node a;
    private final double distance;
    private final double maxSpeed;

    public Edge(Node a, Node b, int distance, int maxSpeed){
        this.a = a;
        this.b = b;
        this.distance = distance;
        this.maxSpeed = maxSpeed;
    }

    public Node getA(){
        return a;
    }

    public Node getB(){
        return b;
    }

    public double getDistance(){
        return this.distance;
    }

    public double getMaxSpeed(){
        return this.maxSpeed;
    }
}
