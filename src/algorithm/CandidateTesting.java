package algorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.mysql.cj.api.jdbc.Statement;

import ca.pfv.spmf.algorithms.frequentpatterns.apriori_rare.AlgoAprioriRare;
import db.AttributeSelection;
import db.AttributeType;
import db.db_connection;

// in this process, given a set of candidate tuples, use finding infrequent itemset to generate beyond-h 
// minimal query
public class CandidateTesting {
	
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/database_project?useSSL=false";
	private static final String dbName = "root";
	private static final String PWD = "root";
	static ArrayList<String> bhq = new ArrayList<String>();
	public ArrayList<HashMap<String, String>> minimalItemSet = new ArrayList<HashMap<String, String>>();
	// for each candidate tuple, given top-h tuple, generate their beyond-h minimal queries 
	// candidateTuple: the id of this tuple 
	// String pre, the query that return given h tuple, get access to the temporary database 
	ArrayList<String> CandidateSet;
	public CandidateTesting(ArrayList<String> CandidateSet, ArrayList<String> colName) throws SQLException, ClassNotFoundException, IOException{
		this.CandidateSet = CandidateSet;
		
		for(String i:CandidateSet){
			
			ArrayList<String> dataset = new ArrayList<String>();
			Statement st= db_connection.getSt();
			Statement st2 = db_connection.getSt();
			ResultSet rs = st.executeQuery("SELECT * FROM running where id =" + i);
			rs.next();
			
			for(String j:colName){
				
				String temp = j+"=" + rs.getString(j);
				System.out.println("this predicare:" + temp);
				dataset.add(temp);
			}
			
			
			System.out.println(dataset);
			HashMap<String, Integer> temp = new HashMap<String, Integer>();
			
			for(int k=1;k<=dataset.size();k++){
				temp.put(colName.get(k-1), k);
			}
			
			
			System.out.println(temp);
			Writer writer = new FileWriter("C:\\Users\\sherr\\workspaceDatabase\\Database_project\\src\\algorithm\\contextZart.txt");
		
			ResultSet rs2 = st.executeQuery("SELECT * FROM run_tempo");
			ResultSet rs3 = st2.executeQuery("SELECT * FROM running WHERE id = " + i);
			rs3.next();
			while(rs2.next()){
				for(String i1: colName){
					if(rs2.getString(i1).equals(rs3.getString(i1))){
			    	        writer.write(temp.get(i1)+" ");

					}
					
				}
				
				writer.write("\n");
				
			}
			
			writer.close();
	        System.out.println("stop here");
	        String inputFilePath = fileToPath("contextZart.txt");
			String outputFilePath = "C:\\Users\\sherr\\workspaceDatabase\\Database_project\\src\\algorithm\\output.txt"; 
			
			ResultSet rs_count = st.executeQuery("SELECT count(*) FROM run_tempo");
			rs_count.next();
			int h = rs_count.getInt(1);
			int k= Constant.k1;
			// the threshold that we will use:
			double minsup = k/h;
			
			// Applying the APRIORI-Inverse algorithm to find sporadic itemsets
			AlgoAprioriRare apriori2 = new AlgoAprioriRare();
			// apply the algorithm
			apriori2.runAlgorithm(minsup, inputFilePath, outputFilePath);
			apriori2.printStats();
			
			File file = new File("C:\\Users\\sherr\\workspaceDatabase\\Database_project\\src\\algorithm\\output.txt");
			if(file.exists() && file.length()== 0) {
				System.out.println("empty");
				
			}
			
		}	
	        System.out.println("finish" );
}
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestAprioriRare_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
		
	
	
    public void findMinimal(String candidateTuple, String pre, ArrayList<String> colName) throws ClassNotFoundException, SQLException{
    	String sql = pre ;
    	Statement st = AttributeSelection.dbconnect2();
    	Statement st2 = AttributeSelection.dbconnect2();
    	
    	ResultSet rs = st.executeQuery(pre + " WHERE id = " +candidateTuple);
    	HashMap<String, String> tempCan = new HashMap<String, String>();
    	System.out.println(pre + " WHERE id = " +candidateTuple);
  //  	ResultSet rs2 = st2.executeQuery(pre);
    	
    	int level = 1;
    	rs.next();
    	for(String i:colName){
    		tempCan.put(i, rs.getString(i));
    	}
    	System.out.println(tempCan);
    	ArrayList<String> bhq = selectquery(st, level, tempCan);
    	System.out.println(bhq);
    	for (int i = 0; i < bhq.size(); i++) {
    		String j = bhq.get(i).replace("run_tempo","running");
    	    bhq.set(i, j);
    	}
    	
    	System.out.println("repeat" + bhq);
    	
    }
    
    
   
    private ArrayList<String> selectquery(Statement st, int level, HashMap<String, String> tempCan) throws SQLException {
		// TODO Auto-generated method stub
    	ArrayList<String[]> predicate=new ArrayList<String[]>();
    	ArrayList<String> reallist=new ArrayList<String>();
    	for (String key : tempCan.keySet()) {
    		reallist.add(key);
    	}
    	
    	String[] Stringlist = reallist.toArray(new String[reallist.size()]);
    	int []list =new int[Stringlist.length];
    	for(int i=0;i<list.length;i++){
    		list[i]=i;
    	}
    	
    	AttributeSelection.combination(Stringlist,list, level, 0, list.length);
    	predicate = AttributeSelection.predicate;
    	System.out.println("size : "+ AttributeSelection.predicate.size());
    	for(int i=0;i<AttributeSelection.predicate.size();i++){
    		String sql = "select count(*) from database_project.run_tempo where ";
    		for (int j=0 ;j<AttributeSelection.predicate.get(i).length-1;j++ ){
    			sql = sql + predicate.get(i)[j] + "=" + tempCan.get(predicate.get(i)[j])+" AND ";
    			
    			
    		}
    		sql = sql + predicate.get(i)[AttributeSelection.predicate.get(i).length-1] + "=" + tempCan.get(predicate.get(i)[AttributeSelection.predicate.get(i).length-1]);
    		System.out.println(sql);
    		ResultSet rs = st.executeQuery(sql);
    		rs.next();
    		int num = rs.getInt(1);
    		
    		System.out.println("match number :"+ num);
    		int thros = 2;
    		if(num < thros ){
    			bhq.add(sql);
    		}
    	}
    	
    	if (bhq.isEmpty()&&level<5){
    		System.out.println("into next level");
    		AttributeSelection.predicate.clear();
    		bhq.clear();
    		selectquery(st, level+1, tempCan);
    		
    	}
    	System.out.println("here"+ bhq);
		return bhq;
    }
     
	public CandidateTesting(ResultSet rs, String candidateTuples[], String sql){
    	 
     }
     
     
    
     public static ArrayList<String> getBhq() {
		return bhq;
	}
	public static void setBhq(ArrayList<String> bhq) {
		CandidateTesting.bhq = bhq;
	}
	public ArrayList<HashMap<String, String>> getMinimalItemSet() {
		return minimalItemSet;
	}
	public void setMinimalItemSet(ArrayList<HashMap<String, String>> minimalItemSet) {
		this.minimalItemSet = minimalItemSet;
	}
	public ArrayList<String> getCandidateSet() {
		return CandidateSet;
	}
	public void setCandidateSet(ArrayList<String> candidateSet) {
		CandidateSet = candidateSet;
	}
	public static String getUrl() {
		return URL;
	}
	public static String getDbname() {
		return dbName;
	}
	public static String getPwd() {
		return PWD;
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
 	{
 	/*    boolean t1[]={false,false,false,false,true};
 		boolean t2[]={false,false,false,true,true};
 		boolean t3[]={false,false,true,false,true};
 		boolean tester[]={false,true,true,true,true};
 		int threshod=1;
 		int numberSame[]=new int[tester.length];
 		ArrayList<boolean[]> top_h=new ArrayList<boolean []>();
 		top_h.add(t1);
 		top_h.add(t2);
 		top_h.add(t3);
 		String r[]=new String[top_h.size()];
 		int AttNumber=5;
 		for(int j=0;j<top_h.size();j++){
 			boolean t[]=top_h.get(j);
 			for(int i=0;i<t.length;i++){
 				System.out.println(i+" "+t[i]+tester[i]);
 				if(t[i]==tester[i]){
 					numberSame[i]++;
 				    r[j]=r[j]+"t"+i+"="+t[i]+" ";
 				}
 				
 			}
 		}
 		for(int i=0;i<numberSame.length;i++){
 		
 			if(numberSame[i]<3&&numberSame[i]!=0)
 		            System.out.print("tester"+i+"="+tester[i]+" ");
 		}
 		for(String r1:r){
 			System.out.println(r1);
 		}  */
    	 
    	   double minsup = 10/30;
			
			// Applying the APRIORI-Inverse algorithm to find sporadic itemsets
			AlgoAprioriRare apriori2 = new AlgoAprioriRare();
			String inputFilePath = fileToPath("contextZart.txt");
				String outputFilePath = "C:\\Users\\sherr\\workspaceDatabase\\Database_project\\src\\algorithm\\output.txt"; 
			// apply the algorithm
			apriori2.runAlgorithm(minsup, inputFilePath, outputFilePath);
			apriori2.printStats();
 	}       
     
}
