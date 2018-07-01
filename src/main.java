import lab.MaxFlow;
import lab.Node;

import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        MaxFlow flow = new MaxFlow("Iksburg5");
        ArrayList<String> path;
        String[] sources = {"A"};
        String[] destinations = {"E"};
        //System.out.println(flow.findMaxFlow(sources, destinations));

        path = flow.findResidualNetwork(sources, destinations);
        for(int i = 0; i < path.size(); i++) {
            System.out.println(path.get(i));
        }

    }
}
