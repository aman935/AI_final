/*
 * Genetic.java
 * Create a tour and evolve a solution
 */

package alviz.algorithm;

import alviz.base.algorithm.Algorithm;
import alviz.base.graph.BaseGraph.Node;
import alviz.base.graph.BaseGraph;
import alviz.graph.Graph;
import alviz.util.Pair;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Genetic extends Algorithm {
    private BaseGraph graph;
    private LinkedList<Pair<BaseGraph.Node,BaseGraph.Node>> optimal;
    private LinkedList<Pair<BaseGraph.Node,BaseGraph.Node>> previous;

    public Genetic(BaseGraph graph) {
        super();
        this.graph = graph;
        /*this.open = null;
          this.closed = null;*/
    }
    private void openNode(BaseGraph.Node n, BaseGraph.Node p) {
        if (n != null) {
            // graph.openNode(n, p);
            optimal.offer(new Pair(n, p));
        }
    }
    private void closeNode(Pair<BaseGraph.Node,BaseGraph.Node> pn) {
        BaseGraph.Node n = pn.fst;
        if (n != null) {
            graph.closeNode(n);
            previous.add(pn);
        }
    }

    public void execute() throws Exception {
        optimal = new LinkedList<Pair<BaseGraph.Node,BaseGraph.Node>>();
        previous = new LinkedList<Pair<BaseGraph.Node,BaseGraph.Node>>();
        //openNode(graph.getStartNode(), null);
        tsp_algo();
        // generatePath();
        setStateEnded();
        show();
    }

    public void tsp_algo() {
        // Adding all the cities
        List<BaseGraph.Node> allNodes = graph.getNodes();
        for(BaseGraph.Node a : allNodes)
            {
                City city = new City(a.x, a.y);
                // graph.openNode(a, f);
                TourManager.addCity(city);
                System.out.println(a.x +" " + a.y);
            }

        // Initialize population
        Population pop = new Population(50, true);
        System.out.println("Initial distance: " + pop.getFittest().getDistance());

        // Evolve population for 100 generations
        pop = GA.evolvePopulation(pop);
        for (int i = 0; i < 100; i++) {
            // List<BaseGraph.Node> tourNodes = pop.getFittest();
            // for(BaseGraph.Node a: tourNodes){
            //     if(a != NULL){
            //         openNode(a, );
            //     }
            // }
            
            pop = GA.evolvePopulation(pop);
        }

        // Print final results
        System.out.println("Finished");
        System.out.println("Final distance: " + pop.getFittest().getDistance());
        System.out.println("Solution:");
        // System.out.println(pop.getFittest());
        Tour optimal = pop.getFittest();
        int n = TourManager.numberOfCities();
        for (int i = 0;i < n - 1;i++){
            // System.out.println(optimal.getCity(i));
            BaseGraph.Node k = city2Node(optimal.getCity(i + 1), allNodes);
            BaseGraph.Node p = city2Node(optimal.getCity(i), allNodes);
            graph.openNode(p, k);
            System.out.println(k.x + " " + k.y);
            // for (BaseGraph.Node a:allNodes){
            //     if (k.x == a.x)
            //         graph.openNode(k, a);
            // }
        }
        BaseGraph.Node k = city2Node(optimal.getCity(0), allNodes);
        BaseGraph.Node p = city2Node(optimal.getCity(n - 1), allNodes);
        graph.openNode(p, k);

        // System.out.println(allNodes);
        // graph.openNode(optimal.getCity(0), optimal.getcity(n - 1));
    }
    public BaseGraph.Node city2Node(City city, List<BaseGraph.Node> all){
        for (BaseGraph.Node c:all){
            if (c.x == city.getX())
                return c;
        }
        return all.get(0);
    }
    // public static void main(String[] args) {

    //     // Create and add our cities
    //     City city = new City(60, 200);
    //     TourManager.addCity(city);


    //     // Initialize population
    //     Population pop = new Population(50, true);
    //     System.out.println("Initial distance: " + pop.getFittest().getDistance());

    //     // Evolve population for 100 generations
    //     pop = GA.evolvePopulation(pop);
    //     for (int i = 0; i < 100; i++) {
    //         pop = GA.evolvePopulation(pop);
    //     }

    //     // Print final results
    //     System.out.println("Finished");
    //     System.out.println("Final distance: " + pop.getFittest().getDistance());
    //     System.out.println("Solution:");
    //     System.out.println(pop.getFittest());
    // }
}
