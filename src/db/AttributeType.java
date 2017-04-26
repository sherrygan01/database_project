package db;

import java.util.HashMap;
import java.util.Map;

public class AttributeType {
	public static HashMap<Integer, String>  map = new HashMap< Integer, String>();
	
	static {
        map.put(-7, "BOOLEAN");
        map.put(-6, "BOOLEAN");
        map.put(4, "INT");
        map.put(6, "float");
        map.put(12, "VARCHAR");
        
    }
	
	public static HashMap<String, Integer>  algomap = new HashMap<String, Integer>();
	static{
		algomap.put("a1=0", 1);
		algomap.put("a1=1", 2);
		algomap.put("a2=0", 3);
		algomap.put("a2=1", 4);
		algomap.put("a3=0", 5);
		algomap.put("a3=1", 6);
		algomap.put("a4=0", 7);
		algomap.put("a4=1", 8);
		algomap.put("a5=0", 9);
		algomap.put("a5=1", 10);
		
	}
	
}
