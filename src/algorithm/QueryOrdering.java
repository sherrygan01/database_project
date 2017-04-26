package algorithm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.mysql.cj.api.jdbc.Statement;

import db.AttributeSelection;

// a heuristic collect all bhq for all candidates and order them based on likelihood to reject 
// given m queries, reorder the execution 
public class QueryOrdering {
    QueryOrdering(ArrayList<String> query, ArrayList<String> CandidateSet) throws ClassNotFoundException, SQLException{
    	
    	ArrayList<String> ordered = reorder(query, CandidateSet);
    }
    
    static void expectedNumber (int Att, int chooseAtt){
    	double possibility =1 / Math.pow(2,chooseAtt);
    	System.out.println(possibility);
    }
    
    // reorder query based on their number of matched tuple
    // return the ordered query set
    static ArrayList<String> reorder(ArrayList<String> query, ArrayList<String> candidateSet) throws ClassNotFoundException, SQLException{
    	Statement st = AttributeSelection.dbconnect2();
 		 ArrayList<String> ordered =new ArrayList<String>();
 		 HashMap <String, Integer> re = new HashMap<String , Integer>();
         for(String i: query){
        	// check how many candidate tuple match this query
        	ResultSet rs = st.executeQuery(i);
        	rs.next();
        	int num = rs.getInt(1);
        	re.put(i, num);
        }
        
	//	ct.findMinimal(candidateTuple, pre, colName); 
		 
		for(String i1 :candidateSet){
			if(ordered.isEmpty()){
				System.out.println("empty");
				ordered.add(i1);
				
			}else if(re.get(i1)<re.get(ordered.get(0))){
				ordered.add(0,i1);
			}else if(re.get(i1)>= re.get(ordered.get(ordered.size()-1))){
				ordered.add(i1);
			}else{
				for(String j : ordered){
					if(re.get(i1)>re.get(j)||re.get(i1)<re.get(ordered.indexOf(j)+1)){
						ordered.add(ordered.indexOf(j)+1,i1);
						break;
					}
				}
			}
		}
		
		System.out.println(ordered);
		return ordered;
    }
}
