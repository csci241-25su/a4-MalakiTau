///Name: Malaki-Jacob Taub
///Date: 7/30/2025
///Description: Contains the main function, along with its helpers to determine the shortest path from one node to another in a graph.
package graph;

import heap.Heap;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;

/** Provides an implementation of Dijkstra's single-source shortest paths
 * algorithm.
 * Sample usage:
 *   Graph g = // create your graph
 *   ShortestPaths sp = new ShortestPaths();
 *   Node a = g.getNode("A");
 *   sp.compute(a);
 *   Node b = g.getNode("B");
 *   LinkedList<Node> abPath = sp.getShortestPath(b);
 *   double abPathLength = sp.getShortestPathLength(b);
 *   */
public class ShortestPaths {
    // stores auxiliary data associated with each node for the shortest
    // paths computation:
    private HashMap<Node,PathData> paths;
    private Heap<Node,Double> frontier;
    private HashMap<Node, Double> frontierh;
    private Heap<Node, Double> settled;
    private HashMap<Node, Double> settledh;

    /** Compute the shortest path to all nodes from origin using Dijkstra's
     * algorithm. Fill in the paths field, which associates each Node with its
     * PathData record, storing total distance from the source, and the
     * backpointer to the previous node on the shortest path.
     * Precondition: origin is a node in the Graph.*/
    public void compute(Node origin) {
        paths = new HashMap<Node, PathData>();
        // Creating Frontier and Settled
        frontier = new Heap<Node, Double>();
        frontierh = new HashMap<Node, Double>();
        settled = new Heap<Node, Double>();
        settledh = new HashMap<Node, Double>();
        // Adding Origin to Frontier
        frontier.add(origin, 0.0);
        frontierh.put(origin, 0.0);
        paths.put(origin, new PathData(0, null));
        while(frontier.size()>0){
            // Removing frontier's first value and putting it in settled
            Node f = frontier.peek();
            frontier.poll();
            frontierh.remove(f);
            settled.add(f, paths.get(f).distance);
            settledh.put(f, paths.get(f).distance);
            // Scanning current node's neighbors
            for(Node w : f.getNeighbors().keySet()){
                if(!frontierh.containsKey(w) && !settledh.containsKey(w)){
                    paths.put(w, new PathData(paths.get(f).distance+f.getNeighbors().get(w), f));
                    frontier.add(w, paths.get(w).distance);
                }
                else if(paths.get(w).distance + f.getNeighbors().get(w) < paths.get(w).distance){
                    paths.put(w, new PathData(paths.get(f).distance+f.getNeighbors().get(w), f));
                }
            }
        }
    }

    /** Returns the length of the shortest path from the origin to destination.
     * If no path exists, return Double.POSITIVE_INFINITY.
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called. */
    public double shortestPathLength(Node destination) {
        // Getting the length of the shortest path is easy, as all d values are already stored in the hashMap made in compute().
        if(paths.containsKey(destination)){
            return paths.get(destination).distance;
        }
        else{
            return Double.POSITIVE_INFINITY;
        }
    }

    /** Returns a LinkedList of the nodes along the shortest path from origin
     * to destination. This path includes the origin and destination. If origin
     * and destination are the same node, it is included only once.
     * If no path to it exists, return null.
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called. */
    private LinkedList<Node> answer;
    public LinkedList<Node> shortestPath(Node destination) {
        if(!paths.containsKey(destination)){
            return null;
        }
        else{
            answer = new LinkedList<Node>();
            return traversal(destination);
        }
    }
    /*Recursively creates a Linked List of Nodes from each backpointer visited.*/
    public LinkedList<Node> traversal(Node travel){
        answer.addFirst(travel);
        if(paths.get(travel).distance==0){
            return answer;
        }
        else{
            return traversal(paths.get(travel).previous);
        }
    }

    /** Inner class representing data used by Dijkstra's algorithm in the
     * process of computing shortest paths from a given source node. */
    class PathData {
        double distance; // distance of the shortest path from source
        Node previous; // previous node in the path from the source

        /** constructor: initialize distance and previous node */
        public PathData(double dist, Node prev) {
            distance = dist;
            previous = prev;
        }
    }


    /** Static helper method to open and parse a file containing graph
     * information. Can parse either a basic file or a DB1B CSV file with
     * flight data. See GraphParser, BasicParser, and DB1BParser for more.*/
    protected static Graph parseGraph(String fileType, String fileName) throws
        FileNotFoundException {
        // create an appropriate parser for the given file type
        GraphParser parser;
        if (fileType.equals("basic")) {
            parser = new BasicParser();
        } else if (fileType.equals("db1b")) {
            parser = new DB1BParser();
        } else {
            throw new IllegalArgumentException(
                    "Unsupported file type: " + fileType);
        }

        // open the given file
        parser.open(new File(fileName));

        // parse the file and return the graph
        return parser.parse();
    }

    public static void main(String[] args) {
      // read command line args
      String fileType = args[0];
      String fileName = args[1];
      String origCode = args[2];

      String destCode = null;
      if (args.length == 4) {
          destCode = args[3];
      }

      // parse a graph with the given type and filename
      Graph graph;
      try {
          graph = parseGraph(fileType, fileName);
      } catch (FileNotFoundException e) {
          System.out.println("Could not open file " + fileName);
          return;
      }
      graph.report();


      // TODO 4: create a ShortestPaths object, use it to compute shortest
      // paths data from the origin node given by origCode.
      ShortestPaths sp = new ShortestPaths();
      Node origin = graph.getNode(origCode);
      sp.compute(origin);
      // TODO 5:
      // If destCode was not given, print each reachable node followed by the
      // length of the shortest path to it from the origin.
      if(destCode==null){
        
        for(String nodeString : graph.getNodes().keySet()){
            System.out.println("Node: " + nodeString);
            System.out.println("Shortest Path Length: " + sp.shortestPathLength(graph.getNode(nodeString)));
        }
      }
      // TODO 6:
      // If destCode was given, print the nodes in the path from
      // origCode to destCode, followed by the total path length
      // If no path exists, print a message saying so.
      else{
        LinkedList<Node> Path = sp.shortestPath(graph.getNode(destCode));
        if(Path==null){
            System.out.println("No path exists");
        }
        else{
            for(int i=0;i<Path.size();i++){
                System.out.println(Path.get(i));
            }
            System.out.println("The length of the path from destCode to origin is "+sp.shortestPathLength(graph.getNode(destCode)));
        }
      }
    }
}
