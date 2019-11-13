import static org.junit.jupiter.api.Assertions.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

/**
 * Filename:   PackageManagerTest.java
 * Project:    p4
 * Authors:    Sam Peaslee
 *
 * Description: JUnit test that tests the functionality of the PackageManager
 * class 
 * 
 */
class PackageManagerTest {

    PackageManager pm;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {}

    @BeforeEach
    void setUp() throws Exception {
        pm = new PackageManager();
    }

    @AfterEach
    void tearDown() throws Exception {
        pm = null;
      }  
////////////////////////////////////////////////////////////////////////////   
    /*
     * Check that getAllPackages returns a Set containing all
     * packages in the graph 
     */
    @Test 
    void test_consructGraph_makes_graph_correctly()
    throws FileNotFoundException, IOException, ParseException {
        pm.constructGraph("Cycles.json");      
        Set<String> allPackages = pm.getAllPackages(); 
        String[] expected = {"A","B","C","D","F"};
        int i = 0;
        for(String pkg : allPackages) {
            if(!pkg.equals(expected[i])) {
                fail();
            }
            i++;
        }
        if(allPackages.size() != expected.length) {
            fail();
        }
    }
    /*
     * Check that FileNotFoundException is thrown by constructGraph
     */
    @Test
    void test_constructGraph_filenotfound() throws Exception {
        try {
        pm.constructGraph("invalidFile.json");
        fail();
        } catch(FileNotFoundException e) {
            
        }
    }
    /*
     * Check that ParseError is thrown by constructGraph
     */
    @Test
    void test_constructGraph_parseerror() throws Exception {
        try {
            pm.constructGraph("badJSON.json");
            fail();
            } catch(ParseException e) {
               
            }
    }
       
    
///////////////////////////////////////////////////////////////////////////////    
   /** 
    * Get installation order for packages when there are no cycles in the graph 
    * 
    * @throws Exception
    */
   @Test
   void test_get_instalationOrder_no_cycles() throws Exception{
       pm.constructGraph("noCycles.json"); 
       List<String> installC = pm.getInstallationOrder("C");
       ArrayList<String> expectedC = new ArrayList<>() ; 
       expectedC.add("F");
       expectedC.add("D");
       expectedC.add("E");
       expectedC.add("C");   
       for(int i = 0; i < installC.size(); i++) {
          if(!installC.get(i).equals(expectedC.get(i))) {
              fail();
          }
        }
       List<String> installF = pm.getInstallationOrder("F");
       
       for(int i = 0; i < installF.size(); i++) {
           if(!installF.get(i).equals("F")) {
               fail();
           }
         }
    }
   
   /** 
    * Get installation order at "A" there is a cycle
    * At "D" there is no cycle in its installation order  
    * At "F" there is a no cycle but there is at its dependency A
    * @throws Exception
    */
   @Test
   void test_get_instalationOrder_cycles()
       throws FileNotFoundException, IOException, ParseException {
       pm.constructGraph("Cycles.json"); 
       try {
           pm.getInstallationOrder("A");
           fail();  
       }catch(CycleException e) {
           
       }catch(PackageNotFoundException e) {
           fail();
       }
       try {
           pm.getInstallationOrder("D");
      }catch(CycleException e) {
           fail();
       }catch(PackageNotFoundException e) {
           fail();
       }
       try {
           pm.getInstallationOrder("F");
           fail();
      }catch(CycleException e) {
   
       }catch(PackageNotFoundException e) {
           fail();
       }
    
       
   }
   
   /** 
    * Package not in graph error should be thrown  
    * 
    * @throws Exception
    */
   @Test
   void test_get_instalationOrder_package_not_in_graph()
       throws FileNotFoundException, IOException, ParseException{
       pm.constructGraph("Cycles.json"); 
       try {
           pm.getInstallationOrder("Invalid");
           fail();  
       } catch(CycleException e) {
           fail();
       }catch(PackageNotFoundException e) {
       
       }

   }
 ////////////////////////////////////////////////////////////////////////////

   /*
    * Get the packages still need to be installed
    * No cycles in the graph
    */
   @Test
   void test_toInstall_no_cycles() throws Exception{
       pm.constructGraph("noCycles.json"); 
       List<String> install = pm.toInstall("A","C");
       ArrayList<String> expected = new ArrayList<>() ; 
       expected.add("B");
       expected.add("A");  
       for(int i = 0; i < install.size(); i++) {
          if(!install.get(i).equals(expected.get(i))) {
              fail();
          }
        }
   
    }

   /*
    * Test that toInstall throws CycleExceptions correctly 
    */
   @Test
   void test_toInstall_Cycles()       
   throws FileNotFoundException, IOException, ParseException{
       pm.constructGraph("Cycles.json"); 
       try {
       pm.toInstall("A","D");
       fail();
       }catch(CycleException e) {
   
       }catch(PackageNotFoundException e) {
           fail();
       }
       try {
       pm.toInstall("D","A");
       fail();
       }catch(CycleException e) {
           
       }catch(PackageNotFoundException e) {
           fail();
       }
       try {
       pm.toInstall("F","D");
       fail();
       }catch(CycleException e) {
           
       }catch(PackageNotFoundException e) {
           fail();
       }
      
    }
   /*
    * Get the packages still need to be installed on another graph
    * Cycle in graph but does not affect all packages 
    */
   @Test
   void test_toInstall_again()   
   throws FileNotFoundException, IOException, ParseException,
   PackageNotFoundException{
       try {
       pm.constructGraph("graph1.json"); 
       List<String> install = pm.toInstall("G","A");
       if(install.size() != 1 | !install.get(0).equals("G")) {
           fail();
       }
       }catch(CycleException e) {
           fail();
       }
       try {
           pm.toInstall("F", "A");
           fail();           
       }catch(CycleException e){
           
       }
   
    }
   /*
    * Test that to install throws PackageNotFound Exceptions correctly 
    * 
    */
   @Test
   void test_toInstall_package_not_in_graph()
       throws FileNotFoundException, IOException, ParseException{
       pm.constructGraph("Cycles.json"); 
       try {
           pm.toInstall("A", "invalid");
           fail(); 
       } catch(CycleException e) {
           fail();
       }catch(PackageNotFoundException e) {
       
       }
       try {
           pm.toInstall("invalid", "A");
           fail(); 
       } catch(CycleException e) {
           fail();
       }catch(PackageNotFoundException e) {
       
       }
   }
   
   
///////////////////////////////////////////////////////////////////////////////
   /*
    * Check that getInstallationOrderForAllPackages throws a cycle,
    * when a cycle is present in the graph 
    */
   @Test 
   void test_getInstallationOrderforAllPackages_throws_cycle() 
   throws FileNotFoundException, IOException, ParseException{
       pm.constructGraph("Cycles.json");
       try{
          pm.getInstallationOrderForAllPackages();
          fail();
       }catch(CycleException e) {
        
       }
   }
   
   /*
    * Check that getInstallationOrderForAllPackages returns valid topological o
    * order, no cycle present in graph
    */
   @Test 
   void test_getInstallationOrderforAllPackages_return_vaild_topo_order() 
   throws FileNotFoundException, IOException, ParseException, CycleException{
       
       pm.constructGraph("noCycles.json");
       List<String> topo =pm.getInstallationOrderForAllPackages();
       String[] expected = {"F", "D", "B", "E", "C", "A"};
       for(int i = 0; i < topo.size(); i ++) {
           if(!topo.get(i).equals(expected[i])) {
               fail();
           }
       }
       
 //////////////////////////////////////////////////////////////////////////////      
   }
   /*
    * Check that getPackageWithMaxDependencies throws a cycle,
    * when a cycle is present in the graph 
    */
   @Test 
   void test_getPackageWithMaxDependenciess_throws_cycle() 
   throws FileNotFoundException, IOException, ParseException{
       pm.constructGraph("Cycles.json");
       try{
           pm.getPackageWithMaxDependencies();
           fail();
       }catch(CycleException e) {
        
       } try{
           pm.constructGraph("graph1.json");
           pm.getPackageWithMaxDependencies();
           fail();
       }catch(CycleException e) {

       }
   }
   
   /*
    * Check that getPackageWithMaxDependencies returns package with max 
    *  dependencies, no cycle present in graph
    */
   @Test 
   void test_getPackageWithMaxDependencies_return_vaild_topo_order() 
   throws FileNotFoundException, IOException, ParseException, CycleException{
       
       pm.constructGraph("noCycles.json");
       String maxDepend = pm.getPackageWithMaxDependencies();
       if(!maxDepend.equals("A")) {
           fail();
       }   
   }
}
