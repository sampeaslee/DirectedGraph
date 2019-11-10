import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

class GraphTest {
    
    Graph graph;
    Set<String> vertices; 
 
       
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
         graph = new Graph();
    }

    @AfterEach
    void tearDown() throws Exception {
        graph = null;
        vertices = null;

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
    /*
     * 
     */
    /**
     * Insert a vertex and check that its in the graph
     */
    @Test
    void test_insert_1_vertex_check() {
        graph.addVertex("1");
        vertices = graph.getAllVertices();
        if (!vertices.contains("1"))
            fail("Insert not working!!");
    }
    /**
     * Insert 50 vertexes and check that they are in graph
     */
    @Test
    void test_insert_50_vertex_check() {
        String str;
        for(int i = 0; i < 50; i++) {
            str = "" + i;
            graph.addVertex(str);
        }
        vertices = graph.getAllVertices();
        for(int j = 0; j < 50; j++) {
            str = "" + j;
            if (!vertices.contains(str))
                fail("Insert not working!!");
        }
    }
    /*
     * Try to insert the same vertex multiple times, should only be added once to 
     * the graph 
     */
    @Test
    void test_insert_same_vertex_twice() {
        graph.addVertex("1");
        graph.addVertex("1");  
        graph.addVertex("1");    
        vertices = graph.getAllVertices();
        if(vertices.size() != 1 ||graph.order() != 1) {
            fail();
        }
    }
    /**
     * Try to insert null vertex, check that it was not inserted; 
     */
    @Test
    void test_insert_null_vertex() {
        graph.addVertex(null);
        vertices = graph.getAllVertices();
        
        if(vertices.contains(null))
            fail();
        if(vertices.size() != 0 ||graph.order() != 0) {
            fail();
        }

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
    
    /**
     * Add an edge between to vertexes that already exist in the graph 
     * Check that the edge exists from vertex1 to vertex2 and 
     * that is does not exits from vertex2 to vertex1 
     */
    @Test 
    void insert_two_vertexs_then_create_edge(){
       graph.addVertex("A");
       graph.addVertex("B");
       graph.addEdge("A", "B");
       List<String> adjacentA = graph.getAdjacentVerticesOf("A");
       List<String> adjacentB = graph.getAdjacentVerticesOf("B");
       if(!adjacentA.contains("B")) { 
           fail();
       }
       if(adjacentB.contains("A")) { 
           fail();
       }
    }
    /**
     * Add an edge between two vertices that do not exist in the graph yet.
     * Check that the edge exists from vertex1 to vertex2 and 
     * that is does not exits from vertex2 to vertex 
     */
    @Test 
    void create_edge_between_to_nonexisted_vertexes() {
        graph.addEdge("A", "B");
        List<String> adjacentA = graph.getAdjacentVerticesOf("A");
        List<String> adjacentB = graph.getAdjacentVerticesOf("B");
        if(!adjacentA.contains("B")) { 
            fail();
        }
        if(adjacentB.contains("A")) { 
            fail();
        }
    }
    /**
     * Try to add edges between null vertexes
     * No edges or vertexes should be added to graph 
     */
    @Test
    void create_edge_between_null_vertexes() {
        graph.addEdge(null, "B");
        graph.addEdge("A", null);
        graph.addEdge(null, null);
        vertices = graph.getAllVertices();
        List<String> adjacentA = graph.getAdjacentVerticesOf("A");
        List<String> adjacentB = graph.getAdjacentVerticesOf("B");
        if(!vertices.isEmpty() | !adjacentA.isEmpty() | !adjacentB.isEmpty()) {
            fail();
        }  
    }
    /**
     * Try adding the same edge twice. The edge should only be added once. 
     */
    @Test
    void test_insert_same_edge_twice() {
        // Add two vertices with one edge
        graph.addEdge("A", "B");
        graph.addEdge("A", "B");
        if (graph.size() != 1) {
            fail();
        }
        if (!graph.getAdjacentVerticesOf("A").contains("B")) {
            fail();
        }

    }
    /**
     * Add 21 vertices and 20 edges
     * Check that they are in graph correctly 
     * Graph will look like:
     * 0 -> 1 
     * 1 -> 2
     * 2 -> 3
     * ...........
     */
    @Test 
    void add_mutiple_edges_check_size(){
        for(int i = 0; i < 20; i++) {
           graph.addEdge("" + i, "" + (i + 1));
        }       
        List<String> adjacent;
        for(int i = 0; i < 20; i++) {
            adjacent = graph.getAdjacentVerticesOf(""+i);
            if(!adjacent.contains("" + (i+1))) {
                fail();
            }
        }
        if(graph.size() != 20 | graph.order() != 21) {
            fail();
        }
        
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
    /**
     * Try and remove null from a graph.
     * Check that nothing changes in the graph
     */
    @Test
    void remove_null() {
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.removeVertex(null);
        List<String> adjacent;
        vertices = graph.getAllVertices();  
        // Check that graph has all added vertexes
        if(!vertices.contains("A")| !vertices.contains("B")|
            !vertices.contains("C")) {
            fail();
        }
        adjacent = graph.getAdjacentVerticesOf("A");
        if (!adjacent.contains("B"))
            fail();
        adjacent = graph.getAdjacentVerticesOf("B");
        if (!adjacent.contains("C"))
            fail();
        
    }
    /**
     * Try and remove a vertex that doesn't exist in the graph from a graph.
     * Check that nothing changes in the graph
     */
    @Test
    void remove_nonexistent_vertex() {
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.removeVertex("D");
        List<String> adjacent;
        vertices = graph.getAllVertices();  
        // Check that graph has all added vertexes
        if(!vertices.contains("A")| !vertices.contains("B")|
            !vertices.contains("C")) {
            fail();
        }
        adjacent = graph.getAdjacentVerticesOf("A");
        if (!adjacent.contains("B"))
            fail();
        adjacent = graph.getAdjacentVerticesOf("B");
        if (!adjacent.contains("C"))
            fail();
        
    }
    /*
     * Add multiple vertexes and edges and delete a vertex
     * Check that graph maintains correct shape after deletion
     * Before delete: (A and B point to C)
     *  A --> B --> D
     *   \ /
     *    C 
     * After Delete:
     *   A      D
     *    \
     *     C
     *  
     * 
     */
    @Test
    void insert_multiple_vertexes_and_edges_remove_vertex() {
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        
        List<String> adjacent;
        vertices = graph.getAllVertices();       
        int numberOfEdges = graph.size();
        int numberOfVertices = graph.order();
        if(numberOfEdges != 4) {
            fail();
        }
        if(numberOfVertices != 4) {
            fail();
        }
        
        // Check that graph has all added vertexes
        if(!vertices.contains("A")| !vertices.contains("B")|
            !vertices.contains("C")|!vertices.contains("D")) {
            fail();
        }
   
        // Check that A's outgoing edges are correct
        adjacent = graph.getAdjacentVerticesOf("A");
        if (!adjacent.contains("B") || !adjacent.contains("C"))
            fail();
        // Check that B's outgoing edges are correct
        adjacent = graph.getAdjacentVerticesOf("B");
        if (!adjacent.contains("C") | !adjacent.contains("D"))
            fail();   
        // C and D have no out going edges 
        adjacent = graph.getAdjacentVerticesOf("C");
        if (!adjacent.isEmpty())
            fail(); 
        adjacent = graph.getAdjacentVerticesOf("D");
        if (!adjacent.isEmpty())
            fail();         
        // Remove B from the graph 
        graph.removeVertex("B");
        // Check that A's outgoing edges are correct
        // An edge to B should no exist any more 
        adjacent = graph.getAdjacentVerticesOf("A");
        if (adjacent.contains("B") || !adjacent.contains("C"))
            fail();
        // There should be no elements in the list of successors
        // of B since its deleted form the graph
        adjacent = graph.getAdjacentVerticesOf("B");
        if (!adjacent.isEmpty())
            fail(); 
        // Check that graph has still has all vertexes not removed
        if(!vertices.contains("A")| !vertices.contains("C")|
            !vertices.contains("D")) {
            fail();
        }
        //Check that vertex B was removed from graph 
        if(vertices.contains("B")) {
            fail();
        }
        numberOfEdges = graph.size();
        numberOfVertices = graph.order();
        if(numberOfEdges != 1) {
            fail();
        }
        if(numberOfVertices != 3) {
            fail();
        }
    }
    
    /*
     * Add multiple vertexes with edges, remove one, them add vertex in again
     */
    @Test
    void add_remove_add() {
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B","D");
        graph.addEdge("C","D");
        graph.addEdge("D", "E");        
        // Check that E is in graph with correct edges
        List<String> adjacent = graph.getAdjacentVerticesOf("D");
        vertices = graph.getAllVertices();   
        if(!vertices.contains("E") | !adjacent.contains("E")) {
            fail();
        }
        //Remove E
        graph.removeVertex("E");
        if(vertices.contains("E") | adjacent.contains("E")) {
            fail();
        }
        
        //Add E back into graph with different edge
        graph.addEdge("B", "E");
        adjacent = graph.getAdjacentVerticesOf("B");
        if(!vertices.contains("E") | !adjacent.contains("E")) {
            fail();
        }
    }


    /*
     * Add 20 vertexes, remove 20 vertexes, add 20_
     */
    @Test
    void add_20_remove_20_add_20() {
        //Add 20
        for(int i = 0; i < 20; i++) {
            graph.addVertex("" +i);
        }
        vertices = graph.getAllVertices();
        //Check all 20 are in graph
        for(int i = 0; i < 20; i++) {
            if(!vertices.contains("" + i)) {
                fail();
            }
        }
        //Remove 20 
        for(int i = 0; i < 20; i++) {
            graph.removeVertex("" +i);
        }
        //Check all 20 are not in graph anymore 
        
        for(int i = 0; i < 20; i++) {
            if(vertices.contains("" + i)) {
                fail();
            }
        }
        //Add 20 again
        for(int i = 0; i < 20; i++) {
            graph.addVertex("" +i);
        }
        //Check all 20 are in graph
        for(int i = 0; i < 20; i++) {
            if(!vertices.contains("" + i)) {
                fail();
            }
        }
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
    
    /*
     * Add two vertices with one edge, remove the edge
     * Before removal: 
     *  A -> B
     *  After removal:
     *  A   B
     */
    @Test
    void test_insert_two_vertice_with_one_edge_remove_edge() {
        // Add two vertices with one edge
        graph.addEdge("A", "B");
        vertices = graph.getAllVertices();
        // B should be an adjacent vertex of A
        if(!graph.getAdjacentVerticesOf("A").contains("B")){
            fail();
        }
        //Check that A and B are in the graph
        if(!vertices.contains("A") |!vertices.contains("B")) {
            fail();
        }
        //There's one edge in the graph
        if(graph.size() != 1) {
            fail();
        }
        //Remove edge from A to B
        graph.removeEdge("A", "B");
        // Check that A and B are still in the graph
        if(!vertices.contains("A") |!vertices.contains("B")) {
            fail();
        }
        // B should not be an adjacent vertex of 
        if(graph.getAdjacentVerticesOf("A").contains("B")){
            fail();
        }
        // There are no more edges in the graph
        if(graph.size() != 0) {
            fail();
        }
    }
    
    /*
     * Add three vertices with multiple edges, 
     * Try to remove an edge that does not exist 
     * Try and remove an edge from a vertex that does not exist 
     * Try and remove a null
     * Before removal: 
     *  A -> B -> C
     *   
     *  After removal:
     *  A -> B -> C
     */
    @Test
    void test_insert_two_vertice_with_one_edge_remove_non_existent_edge() {
        // Add three vertices with two edges
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        vertices = graph.getAllVertices();
        // B should be an adjacent vertex of A
        if(!graph.getAdjacentVerticesOf("A").contains("B")){
            fail();
        }
        // C should  be an adjacent vertex of B
        if(!graph.getAdjacentVerticesOf("B").contains("C")){
            fail();
        }
        //Check that A,B and C are in the graph
        if(!vertices.contains("A") |!vertices.contains("B")
            | !vertices.contains("C")) {
            fail();
        }
        // There are two edges in the graph
        if(graph.size() != 2) {
            fail();
        }
        // Try are remove edge from A to C
        graph.removeEdge("A", "C");
        graph.removeEdge("A", "D");
        graph.removeEdge("E", "F");
        graph.removeEdge(null, "A");
        graph.removeEdge("A", null);
        //Graph should maintain its original shape
        if(!graph.getAdjacentVerticesOf("A").contains("B")){
            fail();
        }
        // C should  be an adjacent vertex of B
        if(!graph.getAdjacentVerticesOf("B").contains("C")){
            fail();
        }
        //Check that A,B and C are in the graph
        if(!vertices.contains("A") |!vertices.contains("B")
            | !vertices.contains("C")) {
            fail();
        }
        // There are two edges in the graph
        if(graph.size() != 2) {
            fail();
        }
    }
    /*
     * Add 4 edges, remove 4 edges, add for edges, check size
     */
    
    @Test
    void test_insert_edges_remove_edges_check_size() {
        // Add two vertices with one edge
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("A", "D");
        graph.addEdge("A", "E");

        // There are no more edges in the graph
        if(graph.size() != 4) {
            fail();
        }
        graph.removeEdge("A", "B");
        graph.removeEdge("A", "C");
        graph.removeEdge("A", "D");
        graph.removeEdge("A", "E");
        if(graph.size() != 0) {
            fail();
        }
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("A", "D");
        graph.addEdge("A", "E");
        if(graph.size() != 4) {
            fail();
        }
    }
    /*
     * Remove the same edge twice and check size
     * The edge should only be removed once
     */
    @Test
    void test_remove_same_edge_twice() {
        // Add two vertices with one edge
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.removeEdge("A", "B");
        graph.removeEdge("A", "B");
        if (graph.size() != 1) {
            fail();
        }
        if (graph.getAdjacentVerticesOf("A").contains("B")) {
            fail();
        }


    }
}
