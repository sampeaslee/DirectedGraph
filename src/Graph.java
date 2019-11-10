import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;



/**
 * Filename:   Graph.java
 * Project:    p4
 * Authors:    
 * 
 * Directed and unweighted graph implementation
 */

public class Graph implements GraphADT{


class GraphNode<String> {
        /**
         * Private inner class to store a vertex of a graph
         * 
         * @author peasl
         *
         * @param <T>
         */
        String data;// Data associated with a GraphNode
        // Successors of GraphNode
        ArrayList<String> succesors;
    

        /**
         * Constructor to create new GraphNode
         * @param data
         */
        GraphNode() {
            succesors = new ArrayList<String>();
 

        }

        /**
         * Constructor to create new GraphNode
         * @param data
         */
        GraphNode(String data) {
            this.data = data;
            succesors = new ArrayList<String>();
          
       
        }

        /**
         * Retrieve data  associated with a GraphNode
         * @return data
         */
        private String getData() {
            return data;
        }
        /**
         * Retrieves the ArrayList of successors for the GraphNode
         * @return neighbors
         */
        private ArrayList<String> getSuccessors() {
            return succesors;
        }

        /**
         * Adds a successor to the GraphNodes neighbors ArrayList
         * @param neighboor
         */
        private void addSuccessor(String neighboor) {
            succesors.add(neighboor);
        }
    }//End of inner GraphNode class
    
    ArrayList<GraphNode<String>> vertices  =
        new ArrayList<GraphNode<String>>(0);
    Set<String> allVertices =  new HashSet<>();
    int numVertices = 0;
    int numEdges = 0;

    /*
     * Default no-argument constructor
     */ 
    public Graph() {

    }
    
    /**
     * Add new vertex to the graph.
     *
     * If vertex is null or already exists,
     * method ends without adding a vertex or 
     * throwing an exception.
     * 
     * Valid argument conditions:
     * 1. vertex is non-null
     * 2. vertex is not already in the graph 
     */
    public void addVertex(String vertex) {
        //Vertex is null do not add
        if(vertex == null) {
            return;}
        //Vertex is already in the graph do no add
        if(search(vertex) != null) return;
        //Vertex is not in graph so add it
        GraphNode<String> v = new GraphNode<>(vertex);
        vertices.add(v);
        allVertices.add(vertex);
        numVertices++;
        
    }

    /**
     * Remove a vertex and all associated 
     * edges from the graph.
     * 
     * If vertex is null or does not exist,
     * method ends without removing a vertex, edges, 
     * or throwing an exception.
     * 
     * Valid argument conditions:
     * 1. vertex is non-null
     * 2. vertex is not already in the graph 
     */
    public void removeVertex(String vertex) {
        //Vertex is null do not add
        if(vertex == null) return;
        GraphNode<String> vRemoved = search(vertex);
        //Vertex is not in the graph do not remove anything
        if(vRemoved == null) return;
        //Go through list of edges for each vertex in graph
        //If vertex has a edge with vRemoved, removed the edge from 
        //the graph
        List<String> n;
        for(GraphNode<String> v: vertices) {
            n = v.getSuccessors();
            if(n.contains(vertex)) {
                n.remove(vertex);
                numEdges--;
            }
        }
       // Need to decrease the number of edges by one for each
       // outgoing edge of B
       for(String adj: vRemoved.getSuccessors()){
           numEdges--;
           
       }
        //Remove vertex from graph 
        vertices.remove(vRemoved);
        allVertices.remove(vertex);
        numVertices--;
  
    }

    /**
     * Add the edge from vertex1 to vertex2
     * to this graph.  (edge is directed and unweighted)
     * If either vertex does not exist,
     * add vertex, and add edge, no exception is thrown.
     * If the edge exists in the graph,
     * no edge is added and no exception is thrown.
     * 
     * Valid argument conditions:
     * 1. neither vertex is null
     * 2. both vertices are in the graph 
     * 3. the edge is not in the graph
     */
    public void addEdge(String vertex1, String vertex2) {
        //Do not add edge if either vertex is null
        if(vertex1 == null | vertex2 == null) return;
        GraphNode<String> v1 = search(vertex1);
        GraphNode<String> v2 = search(vertex2);
        if(v1 == null) {           
            addVertex(vertex1);
            v1 = search(vertex1);
        }
        if(v2 == null) {
            addVertex(vertex2);
            v2 = search(vertex2);
        }
        //Iterate through adjacent nodes for v1
        //If v2 is already an adjacent node the do not add new edge
       for(String n: v1.getSuccessors()){ 
           if(n.equals(vertex2)) {
               return;
           }
       }
       //No edge exist between v1 and v2 so new edge can be created
       v1.addSuccessor(vertex2);
       numEdges++;
       
    }
    /**
     * Remove the edge from vertex1 to vertex2
     * from this graph.  (edge is directed and unweighted)
     * If either vertex does not exist,
     * or if an edge from vertex1 to vertex2 does not exist,
     * no edge is removed and no exception is thrown.
     * 
     * Valid argument conditions:
     * 1. neither vertex is null
     * 2. both vertices are in the graph 
     * 3. the edge from vertex1 to vertex2 is in the graph
     */
    public void removeEdge(String vertex1, String vertex2) {
        GraphNode<String> v1 = search(vertex1);
        GraphNode<String> v2 = search(vertex2);
        //IF either vertix is not in Graph do not remove anything 
        if(v1 == null | v2 == null) return;
       
        List<String> neighbors = v1.getSuccessors();
        if(neighbors.contains(vertex2)) {
            neighbors.remove(vertex2);
            numEdges--;
        }
        
    }   

    /**
     * Returns a Set that contains all the vertices
     * 
     */
    public Set<String> getAllVertices() {
        return allVertices;
    }

    /**
     * Get all the neighbor (adjacent) vertices of a vertex
     *
     */
    public List<String> getAdjacentVerticesOf(String vertex) {
        
        if(search(vertex) != null) {
            //Vertex is in the graph, return the list of its adjacent vertexes
            return search(vertex).getSuccessors();
        }else {
            //Vertex is not in the graph, return an empty list
            return new ArrayList<String>();
        }
            
    
    }
    
    /**
     * Returns the number of edges in this graph.
     */
    public int size() {
        return numEdges;
    }

    /**
     * Returns the number of vertices in this graph.
     */
    public int order() {
        return numVertices;
    }
    
////////////////////////////////////////////////////////////////////////////////
       
    /**
     * Searches for a specified vertex in the graph and returns the 
     * GraphNode if the vertex is in the graph, otherwise return null
     * @param data
     * @return GraphNode or null
     */
    private GraphNode<String> search(String data) {
        for(int i = 0; i < vertices.size(); i++) {
            if(vertices.get(i).getData().equals(data)) {
                return vertices.get(i);
            }
        }
        return null;
    }
    /**
     * Prints Graph
     */
    public void printGraph() {
        System.out.println("Printing Graph......");
        System.out.println("List of All Vertexes: ");
        for (int i = 0; i < numVertices; i++) {
            System.out.print(vertices.get(i).getData() + ", ");
        }
        System.out.println();
        for (int i = 0; i < numVertices; i++) {
            GraphNode<String> currV = vertices.get(i);
            ArrayList<String> neighbors = currV.getSuccessors();
            System.out.print(
                "Vertex: " + currV.getData()+ 
                " Successors: ");
            if (neighbors.size() == 0) {
                System.out.println("None");
            }
            for (int j = 0; j < neighbors.size(); j++) {
                if (j != neighbors.size() - 1) {
                    System.out.print("" + neighbors.get(j) + ", ");
                } else {
                    System.out.println(neighbors.get(j));
                }
            }
        }
    }
    
       public static void main(String[] args) {
           Graph graph = new Graph();
           graph.addVertex("1");
           graph.addVertex("2");
           graph.addVertex("3");
           graph.addVertex("4");
           graph.addEdge("1", "4");
           graph.addEdge("1", "3");
           graph.addEdge("2","1");

           graph.printGraph();

           
      
       }
    
}