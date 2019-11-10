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

public class PackageManager{
    
    // Directed Graph to store packages 
    private Graph graph;
    // Stores number of predecessors for each package
    private ArrayList<Integer> numPred;
    // All packages in the graph, indexes match with indexes in numPred
    private ArrayList<String> allPackages;
    /*
     * Package Manager default no-argument constructor.
     */
    public PackageManager() {
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
            // Add the package to the graph
            graph.addVertex((String) t.get("name")); 
            // Add the package to the list containing all packages
            allPackages.add((String) t.get("name"));
            // Get the dependencies array
            JSONArray depend = (JSONArray) t.get("dependencies");
            // Add the number of dependencies of the package to numPred
            numPred.set(i, depend.size());    
            // Create another iterator to loop through dependencies array
            Iterator itr2 = depend.iterator();
            while (itr2.hasNext()) {                
                // Create edges that go from the packages in the 
                // dependency array to the package just added
                graph.addEdge((String) itr2.next(), (String) t.get("name"));     
            }
            i++;
        }
        // If a package was only present in dependency array(s) of other
        // packages add it to allPackages, and numPred
        // The package has zero dependencies 
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
     * @throws CycleException if you encounter a cycle in the graph 
     * while finding the installation order for a particular package.
     * Tip: Cycles in some other
     * part of the graph that do not affect the installation order for the 
     * specified package, should not throw this exception.
     * 
     * @throws PackageNotFoundException if the package passed
     * does not exist in the dependency graph.
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
        // Create list to hold installation order for the package
        ArrayList<String> installOrder = new ArrayList<String>();
        // Get the list of packages that need to be installed before pkg
        installOrder = getInstallationOrderAt(pkg, installOrder);
        // Add the pkg as the last item in its installation order
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
     * @throws CycleException if you encounter a cycle in the graph 
     * while finding the dependencies of the given packages.
     * If there is a cycle in some other part of the graph that doesn't 
     * affect the parsing of these dependencies, 
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
     * @return List<String>, order in which all the packages have
     * to be installed
     * @throws CycleException if you encounter a cycle in the graph
     */
     public List<String> getInstallationOrderForAllPackages()        
       throws CycleException {
       // List that will store valid global installation order
       List<String> installOrder = 
           new ArrayList<String>(graph.getAllVertices().size());
       // Queue for traversing graph 
       // Will store packages with no dependencies (predecessors)
       Queue<String> queue = new LinkedList<>();
       // Holds number of dependencies for each vertex
       int[] numDepends = new int[graph.getAllVertices().size()];
       for(int j= 0; j < numPred.size(); j++) {
           numDepends[j] = numPred.get(j);
       }

       int visitedVertexes = 0;// Keeps track of visited nodes in graph
       for(String vertex: allPackages) { 
           if (hasCycleAt(vertex)) {
               throw new CycleException();
           }   
           // Add packages with no dependencies to queue
           if(numPred.get(visitedVertexes) == 0){
               queue.add(vertex);
           }
           visitedVertexes++;
       }
     
       List<String> adjacent; //Successors of a package
       while(!queue.isEmpty()) {
           // Get package with no dependencies
           String currVertex = queue.poll();
           // Add to installation order
           installOrder.add(currVertex);
           // Get all its adjacent vertexes
           adjacent =  graph.getAdjacentVerticesOf(currVertex);
           for(String adj: adjacent) {
               visitedVertexes = 0; // Reset visited packages to 0
               // Get the index of the package's successor(s) that corresponds
               // to the index in numInEdges
               for(String v: allPackages) {
                  if(v.equals(adj)) {
                      break;
                  }else {
                  visitedVertexes++;
                  }
               }  
              // Decrement the number of dependencies for the package,
              // since one of its dependencies has already been added to
              // the installation order
              numDepends[visitedVertexes]--;   
               //If the vertex has no more dependencies add to queue
               if( numDepends[visitedVertexes] == 0) {;
                   queue.add(adj);
               }
           }
       }
       return  installOrder;
   }
    /**
     * Find and return the name of the package with 
     * the maximum number of dependencies.
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
        // Holds total number of dependencies for each package
        int[] numOfDepends = new int[allPackages.size()];
        int i = 0;
        // Get the number of dependencies for each vertex
        for (String vertex : allPackages) {
            // Check for cycle in the graph 
            if (hasCycleAt(vertex))
                throw new CycleException();
            ArrayList<String> installOrder = new ArrayList<>();
            installOrder = getInstallationOrderAt(vertex, installOrder);
            // The size of the list of a packages's installation order
            // is the total number of dependencies needed to install 
            numOfDepends[i] = installOrder.size();
            i++;

        }
        // Find the package with the most dependencies
        int max = 0;
        for (int j = 0; j < numOfDepends.length; j++) {
            if (numOfDepends[j] > numOfDepends[max]) {
                max = j;
            }
        }
        // return package with most dependencies 
        return allPackages.get(max);
    }

    /* Private methods I added to implementation */
    
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
   private ArrayList<String> getInstallationOrderAt(String pkg,
       ArrayList<String> installOrder) throws CycleException{
       // Loop through all packages in the graph
       for(String currPackage: graph.getAllVertices()) {
           //Get successors of each package
           List<String> adjacent = graph.getAdjacentVerticesOf(currPackage);
           if(adjacent.contains(pkg)) {
               // If the current package is a predecessor of pkg,
               // check that there's no  cycle 
               if (hasCycleAt(currPackage)) {
                   throw new CycleException();
               }
               // Recursively get the predecessors of the current package 
               installOrder = 
                   getInstallationOrderAt(currPackage, installOrder);
               // After recursion is done going through the graph add packages
               // to the installation order 
               // Packages can share dependencies
               // So if the dependencies was already added
               // to the installation order it does not need to be added again
               if(!installOrder.contains(currPackage)) {
               installOrder.add(currPackage);
               }
           }
       }      
       return  installOrder;
   }
   /**
    * Method uses a queue to traverse a graph and detect a cycle if present at
    * a specified vertex. If you can get back to the vertex when traversing
    * the graph there is a cycle 
    * @param vertex- starting 
    * @return true if a cycle is present, false if not
    */
   private boolean hasCycleAt(String vertex){        
       // Queue for traversing graph 
       // Will store packages with no dependencies (predecessors)
       // Holds number of dependencies for each vertex
       boolean[] visited = new boolean[allPackages.size()];
       for(int i = 0; i < allPackages.size(); i++) {
           if(allPackages.get(i).equals(vertex)) {
               visited[i] = true;
               continue;
           }
       }
       Queue<String> q = new LinkedList<>();
       // Add the starting vertex to the queue     
       q.add(vertex);
       while(!q.isEmpty()) {
           // Get and remove item from head of queue
           String current = q.poll();
           // Successors of the current vertex 
           List<String> curAdj = graph.getAdjacentVerticesOf(current);
           // Loop through the successors 
           int j;
           for(int i = 0; i < curAdj.size(); i++) {
               j = 0;
               for(int k = 0; k < allPackages.size(); k++) {
                   if(allPackages.get(k).equals(curAdj.get(i))) {
                       break;       
                   }
                   j++;
               }      
               // If the starting vertex in the list of successors 
               // then there is a cycle present 
               if(curAdj.get(i).equals(vertex)) {
                   return true;
               }else {
                   // Add the successor to the queue 
                   if(!visited[j]) {
                       visited[j]= true;
                       q.add(curAdj.get(i));     
                   }
               }
           }
       }    
       return false;
   }
   public static void main(String[] args) throws Exception  {
       PackageManager pm = new PackageManager();
       pm.constructGraph("graphCycle.json");
       System.out.println(pm.hasCycleAt("A"));
   }
}
