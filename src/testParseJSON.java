
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class testParseJSON {

    public static void main(String[] args)  throws Exception {
        Object obj = new JSONParser().parse(new FileReader("valid.json"));
        ArrayList<String> al =  new ArrayList<String>();
        JSONObject jo = (JSONObject) obj;
        JSONArray ja = (JSONArray) jo.get("packages");
        Iterator itr = ja.iterator();
        while(itr.hasNext()) {
            JSONObject t = (JSONObject) itr.next();
            JSONArray depend  = (JSONArray) t.get("dependencies");
            System.out.println("\n" + "Name:" + t.get("name"));
            Iterator itr2 = depend.iterator();
            System.out.println("Dependencies: " + t.get("dependencies"));
            while(itr2.hasNext()) {
                System.out.print(itr2.next());
    
            }
       
        }
        
    
    }
}
