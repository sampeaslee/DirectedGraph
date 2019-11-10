import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.Queue;

/**
 * Filename:   PackageManager.java
 * Project:    p4
 * Authors:    
 * 
 * PackageManager is used to process json package dependency files
 * and provide function that make that information available to other users.
 * 
 * Each package that depends upon other packages has its own
 * entry in the json file.  
 * 
 * Package dependencies are important when building software, 
 * as you must install packages in an order such that each package 
 * is installed after all of the packages that it depends on 
 * have been installed.
 * 
 * For example: package A depends upon package B,
 * then package B must be installed before package A.
 * 
 * This program will read package information and 
 * provide information about the packages that must be 
 * installed before any given package can be installed.
 * all of the packages in
 * 
 * You may add a main method, but we will test all methods with
 * our own Test classes.
 */

public class PackageManager2{

    private Graph graph;
    private ArrayList<Integer> numPred;
    private ArrayList<String> allPackages;
    /*
     * Package Manager default no-argument constructor.
     */
    public PackageManager2() {
        graph = new Graph();
        numPred = new ArrayList<>();
        allPackages = new ArrayList<>();
    }

    /**
     * Takes in a file path for a json file and builds the
     * package dependency graph from it. 
     * 
     * @param jsonFilepath the name of json data file with package dependency
     * information
     * @throws FileNotFoundException if file path is incorrect
     * @throws IOException if the give file cannot be read
     * @throws ParseException if the given json cannot be parsed 
     */
    @SuppressWarnings("rawtypes")
    public void constructGraph(String jsonFilepath)
        throws FileNotFoundException, IOException, ParseException {
        // Read file and parse JSON
        Object obj = new JSONParser().parse(new FileReader(jsonFilepath));
        // Cast to JSON Object
        JSONObject jo = (JSONObject) obj;
        // Get contents of packages array
        JSONArray ja = (JSONArray) jo.get("packages");
        // Create iterator to loop through packages array
        Iterator itr = ja.iterator();
        int i = 0;
        while (itr.hasNext()) {
         
            numPred.add(i, 0);
            // Get next object in packages array
            JSONObject t = (JSONObject) itr.next();
            // Add the value associated with name to the graph
            graph.addVertex((String) t.get("name")); 
            allPackages.add((String) t.get("name"));
            System.out.println((String) t.get("name"));
            // Get the depencencies array
            JSONArray depend = (JSONArray) t.get("dependencies");
            numPred.set(i, depend.size());
            
            // Create another iterator to loop through dependencies array
            Iterator itr2 = depend.iterator();
            while (itr2.hasNext()) {
                String currV = (String) itr2.next();
                // Create edges that go from the vertexes in the 
                //dependency array
                // to the vertix just added
                graph.addEdge(currV, (String) t.get("name"));   
           
            }
            i++;
        }
        for(String str: graph.getAllVertices()) {
            if(allPackages.contains(str)) {   
                continue;
            }else {
                allPackages.add(str);
                numPred.add(0);
            }
        }
        
        graph.printGraph();

    }
    
    /**
     * Helper method to get all packages in the graph.
     * 
     * @return Set<String> of all the packages
     */
    public Set<String> getAllPackages() {
        return graph.getAllVertices();
    }



    /**
     * Given a package name, returns a list of packages in a
     * valid installation order.  
     * 
     * Valid installation order means that each package is listed 
     * before any packages that depend upon that package.
     * 
     * @return List<String>, order in which the packages have to be installed
     *  
     * @throws CycleException if you encounter a cycle in the graph while finding
     * the installation order for a particular package. Tip: Cycles in some other
     * part of the graph that do not affect the installation order for the 
     * specified package, should not throw this exception.
     * 
     * @throws PackageNotFoundException if the package passed does not exist in the 
     * dependency graph.
     */
    public List<String> getInstallationOrder(String pkg)
        throws CycleException, PackageNotFoundException {
        // Create a set containing all vertices in graph
        Set<String> allVertices = graph.getAllVertices();
        // If pkg is not in the graph throw exception
        if (!allVertices.contains(pkg))
            throw new PackageNotFoundException();
        // If there is a cycle at pkg throw exception
        if (hasCycleAt(pkg))
            throw new CycleException();
        // Create list to hold insallation order for the package
        ArrayList<String> installOrder = new ArrayList<String>();
        // Transpose the graph, makes it easier to traverse the graph
        // from a vertex and find all its dependencies
        Graph graphT = transpose();
        // Gets the installation order for a particular graph
        //installOrder = getInstallationOrder(pkg, installOrder, graphT);
        installOrder = getInstallationOrdertAt(pkg, installOrder);
        // Add the package as the last item in its installation order
        installOrder.add(pkg);
        return installOrder;
    }

    
    
    /**
     * Given two packages - one to be installed and the other installed, 
     * return a List of the packages that need to be newly installed. 
     * 
     * For example, refer to shared_dependecies.json - toInstall("A","B") 
     * If package A needs to be installed and packageB is already installed, 
     * return the list ["A", "C"] since D will have been installed when 
     * B was previously installed.
     * 
     * @return List<String>, packages that need to be newly installed.
     * 
     * @throws CycleException if you encounter a cycle in the graph while finding
     * the dependencies of the given packages. If there is a cycle in some other
     * part of the graph that doesn't affect the parsing of these dependencies, 
     * cycle exception should not be thrown.
     * 
     * @throws PackageNotFoundException if any of the packages passed 
     * do not exist in the dependency graph.
     */
    public List<String> toInstall(String newPkg, String installedPkg)
        throws CycleException, PackageNotFoundException {
        // Set of all vertices in the graph
        Set<String> allVertices = graph.getAllVertices();
        // Check that both packages are in the graph
        if (!allVertices.contains(newPkg) | !allVertices.contains(installedPkg))
            throw new PackageNotFoundException();
        // Check to see if a cycle is present at either vertex
        if (hasCycleAt(newPkg) | hasCycleAt(installedPkg))
            throw new CycleException();

        // Find all packages that installedPkg depends on, those packages
        // are already installed
        List<String> installedAlready = getInstallationOrder(installedPkg);
        // Find all packages the newPkg depends on
        List<String> newPkgsDepends = getInstallationOrder(newPkg);
        // Need to find intersection of both dependencies list
        List<String> intersection = new ArrayList<String>();
        for (int i = 0; i < newPkgsDepends.size(); i++) {
            for (int j = 0; j < installedAlready.size(); j++) {
                if (newPkgsDepends.get(i).equals(installedAlready.get(j))) {
                    intersection.add(newPkgsDepends.get(i));
                    continue;
                }
            }
        }
        // Make new list thats a copy of newPkg's dependencies
        List<String> installNeeded = new ArrayList<String>(newPkgsDepends);
        // Remove the intersection of both packages dependencies, which is 
        // all packages that are already installed
        installNeeded.removeAll(intersection);
        return installNeeded;//List of packages that still need to be installed
    }
    
    /**
     * Return a valid global installation order of all the packages in the 
     * dependency graph.
     * 
     * assumes: no package has been installed and you are required to install 
     * all the packages
     * 
     * returns a valid installation order that will not violate any dependencies
     * 
     * @return List<String>, order in which all the packages have to be installed
     * @throws CycleException if you encounter a cycle in the graph
     */
  private List<String> getInstallationOrderForAllPackagesTWO()
        throws CycleException {
        // List that will store valid global installation order
        List<String> installOrder = new ArrayList<String>();
        // Queue for traversing graph 
        // Will store vertexes with no incoming edges
        Queue<String> queue = new LinkedList<>();
        // All vertices present in the graph
        Set<String> vertices = graph.getAllVertices();
        // Holds number of incoming edges for each vertex
        int[] numInEdges = new int[vertices.size()];    
        int visitedVertexes = 0;// To keep track of visited node in graph
        List<String> adjacent;
        // Transpose the graph the out going edges of this graph correspond 
        // to the incoming edges of the original graph
        Graph graphT = transpose();
        //Get number of incoming edges for each vertex
        for(String vertex: vertices) {   
            if (hasCycleAt(vertex)) {
                throw new CycleException();
            }
            adjacent = graphT.getAdjacentVerticesOf(vertex);
            numInEdges[visitedVertexes] = adjacent.size();
            visitedVertexes++;
        }
   
        
        visitedVertexes = 0;
        //Add vertexes with zero incoming edges to the Queue
        for(String vertex: vertices) {   
            if(numInEdges[visitedVertexes] == 0){
                queue.add(vertex);
            }
            visitedVertexes++;
        }

        
        
        while(!queue.isEmpty()) {
            // Get vertex with no edges
            String currVertex = queue.poll();
            // Add to installation order
            installOrder.add(currVertex);
            // Get all its adjacent vertexes
            // in original graph
            adjacent =  graph.getAdjacentVerticesOf(currVertex);
            
            for(String adj: adjacent) {
                // Remove the "incoming edge" from the transposed graph
                graphT.removeEdge(adj, currVertex);
                //If the vertex has no incoming edges add to queue
                if(graphT.getAdjacentVerticesOf(adj).size() == 0) {
                    queue.add(adj);
                }               
            }
        }

        
        return  installOrder;
    }
   /**
    * Without having to transpose graph
    * @return
    * @throws CycleException
    */
   public List<String> getInstallationOrderForAllPackages()        
       throws CycleException {
       // List that will store valid global installation order
       List<String> installOrder = new ArrayList<String>(graph.getAllVertices().size());
       // Queue for traversing graph 
       // Will store vertexes with no incoming edges
       Queue<String> queue = new LinkedList<>();
       // All vertices present in the graph
       //Set<String> vertices = graph.getAllVertices();
       // Holds number of incoming edges for each vertex
       int[] numInEdges = new int[graph.getAllVertices().size()];
       for(int j= 0; j < numPred.size(); j++) {
           numInEdges[j] = numPred.get(j);
       }

       List<String> adjacent;
       int visitedVertexes = 0;// To keep track of visited node in graph
       for(String vertex: allPackages) { 
           if (hasCycleAt(vertex)) {
               throw new CycleException();
           }   
           if(numPred.get(visitedVertexes) == 0){
               queue.add(vertex);
           }
           visitedVertexes++;
       }
       visitedVertexes = 0;

       while(!queue.isEmpty()) {
           // Get vertex with no edges
           String currVertex = queue.poll();
           // Add to installation order

           installOrder.add(currVertex);
           // Get all its adjacent vertexes
           // in original graph
           adjacent =  graph.getAdjacentVerticesOf(currVertex);

           for(String adj: adjacent) {
               visitedVertexes = 0;
               for(String v: allPackages) {
                  if(v.equals(adj)) {
                      break;
                  }else {
                  visitedVertexes++;
                  }
               }        
              numInEdges[visitedVertexes]--;   
               //If the vertex has no incoming edges add to queue
               if( numInEdges[visitedVertexes] == 0) {
                   System.out.println("SAM" + adj);
                   queue.add(adj);
               }
           }
       }
       return  installOrder;
   }
    /**
     * Find and return the name of the package with the maximum number of dependencies.
     * 
     * Tip: it's not just the number of dependencies given in the json file.  
     * The number of dependencies includes the dependencies of its dependencies.  
     * But, if a package is listed in multiple places, it is only counted once.
     * 
     * Example: if A depends on B and C, and B depends on C, and C depends on D.  
     * Then,  A has 3 dependencies - B,C and D.
     * 
     * @return String, name of the package with most dependencies.
     * @throws CycleException if you encounter a cycle in the graph
     */
    public String getPackageWithMaxDependencies() throws CycleException {
        // Set of all vertices in graph
        Set<String> allVertices = graph.getAllVertices();
        // Iteraror to loop through set
        Iterator<String> itr = allVertices.iterator();
        // Int array to hold number of dependencies for each vertex
        int[] numOfDepends = new int[allVertices.size()];
        // Make an array holding all vertexes in same order
        // of allVertices set
        String[] set = new String[allVertices.size()];
        for(int j = 0; j  < allVertices.size(); j++) {      
            set[j] = itr.next();
        }
        int i = 0;
        // Get the number of dependencies for each vertex
        for (String vertex : allVertices) {
            // check for cycle in the graph 
            if (hasCycleAt(vertex))
                throw new CycleException();
            ArrayList<String> installOrder = new ArrayList<>();
            installOrder = getInstallationOrder(vertex, installOrder, transpose());
            // The size of the list of a vertexe's installation order
            // is the number of dependencies needed to install 
            numOfDepends[i] = installOrder.size();
            i++;

        }
        // Find the vertex with the most dependencies
        int max = 0;
        for (int j = 0; j < numOfDepends.length; j++) {
            if (numOfDepends[j] > numOfDepends[max]) {
                max = j;
            }
        }
        // return package with most dependencies 
        return set[max];
    }


///////////////////////////////////////////////////////////////////////////////
    /*
     * My Methods
     */
    /**
     * Method that starts at a vertex uses recursion to traverse the 
     * graph and add the dependencies of the vertex in the correct installation
     * order.
     * Adds dependencies after recursion hits the base case, which is a vertex 
     * with no adjacent vertexes. 
     * @param vertex- starting vertex
     * @param installOrder - list contain installation order
     * @param graphT - transposed graph of package manager graph
     * @return
     */
   private ArrayList<String> getInstallationOrder(String vertex, 
       ArrayList<String> installOrder,Graph graphT)
       throws CycleException{
       // Get successors of the vertex
       List<String> adjacent = graphT.getAdjacentVerticesOf(vertex);
    
       // Loop through the successors 
       for(int i = 0; i < adjacent.size(); i++) {
           // Recursively go through the adjacent vertexes of each 
           // successor 
           if (hasCycleAt(adjacent.get(i)))
               throw new CycleException();
           installOrder = 
               getInstallationOrder(adjacent.get(i), installOrder, graphT);  
           // Vertexes can share dependencies
           // So if the dependencies was already added to the installation order
           // it does not need to be added again
           if(!installOrder.contains(adjacent.get(i))) {
               installOrder.add(adjacent.get(i));
           }
       }
       return installOrder;
   }
   
   private ArrayList<String> getInstallationOrdertAt(String pkg, ArrayList<String> installOrder)
       throws CycleException{
       //ArrayList<String> installOrder = new ArrayList<String>(graph.getAllVertices().size());
       // Queue for traversing graph 
       // Will store vertexes with no incoming edges
       for(String vertex: graph.getAllVertices()) {
           List<String> adjacent = graph.getAdjacentVerticesOf(vertex);
           if(adjacent.contains(pkg)) {
               installOrder = getInstallationOrdertAt(vertex, installOrder);
               if(!installOrder.contains(vertex)) {
               installOrder.add(vertex);
               }
           }
       }
       
       return  installOrder;
   }
   /**
    * Transposes the graph created by Construct graph
    * @return - graphT
    */
   private Graph transpose() {
       Graph graphT = new Graph();
       Set<String> allVertices = graph.getAllVertices();
       //Iterator<String> itr = allVertices.iterator();
       List<String> adjacent;
       //String v;
       
       for(  String v: allVertices) {
           adjacent = graph.getAdjacentVerticesOf(v);
           for(int j = 0; j < adjacent.size(); j++) {
               graphT.addEdge(adjacent.get(j), v);
           }
       }
       return graphT;
   }
   
   private boolean hasCycleAt(String vertex){        
       Queue<String> q = new LinkedList<>();
       q.add(vertex);
       while(!q.isEmpty()) {
           String current = q.poll();
           List<String> curAdj = graph.getAdjacentVerticesOf(current);
           for(int i = 0; i < curAdj.size(); i++) {
               if(curAdj.get(i).equals(vertex)) {
                   return true;
               }else {
                   q.add(curAdj.get(i));
               }
           }
       }    
       return false;
   }

    public static void main (String [] args) throws Exception {
        System.out.println("PackageManager.main()");
        PackageManager2 pm = new PackageManager2();
        
        pm.constructGraph("GS03.json");
   
        List<String> test = pm.getInstallationOrderForAllPackages();
        for(String t: test) {
            System.out.print(t + " ");
        }
        System.out.println();
        System.out.println();
        List<String> test2 = pm.getInstallationOrderForAllPackagesTWO();
        for(String t: test2) {
            System.out.print(t + " ");
        }
        System.out.println();
      System.out.println("Max D's: " + pm.getPackageWithMaxDependencies());
      System.out.println();
     Iterator it = pm.graph.getAllVertices().iterator();
      pm.graph.getAllVertices().iterator().next();
      for(int i = 0; i < pm.numPred.size();i++) {
          
          System.out.println(pm.numPred.get(i) + " allPAckages: " +  pm.allPackages.get(i));
      }
      List<String> ttt = pm.getInstallationOrder("java_package");
      for(String str: ttt) {
          System.out.print(str + " ");
      }
      System.out.println();
     ArrayList<String> sam =  new ArrayList<String>();
      List<String> tttt = pm.getInstallationOrdertAt("java_package",sam);
      for(String str: tttt) {
          System.out.print(str + " ");
      }
      }  
}
