package lab;

public class Edge {
    private final Node b;
    private final Node a;
    private double flow;
    private double residualFlow;

    public Edge(Node a, Node b, int flow, int residualFlow){
        this.a = a;
        this.b = b;
        this.flow = flow;
        this.residualFlow = residualFlow;
    }

    public Node getA(){
        return a;
    }

    public Node getB(){
        return b;
    }

    public double getFlow(){
        return this.flow;
    }

    public double getResidualFlow(){
        return this.residualFlow;
    }

    public void setFlow(double flow){
        this.flow = flow;
    }

    public void setResidualFlow(double resFlow){
        this.residualFlow = resFlow;
    }
}
