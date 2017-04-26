package algorithm;

import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.cj.api.jdbc.Statement;

import db.AttributeSelection;
import db.db_connection;

public class CandidateGeneration {
	//the query to access the current table, to get the h tuple;
	// the value of k in top-k interface
	// the current number of tuple we already get, which means we already have h tuples
	static String tempo_table;
	int k;
	int h;
	static int level = -1000;
	static String candidate_table = Constant.CANDIDATE_TABLE ;
	
	//Given h tuple, get the next ranked tuple
    CandidateGeneration(String tempo_table, int k, int h) throws ClassNotFoundException, SQLException{
    	this.tempo_table = tempo_table;
    	this.k=k;
    	this.h=h;
    	
    	//first, connect the current table to get the current tuples
    	// start from level 1: choose 1 attribute from all attribute
    	if(level<0) level = 1;
    	AttributeSelection as=new AttributeSelection(tempo_table, level);
    	//use the as to select the attribute, generate query
    	
    	// query = as ()
    	//then use the query to generate the DAG graph
    	
    	
    }
    
    public static void createCandidateSetTable() throws ClassNotFoundException, SQLException{
    	Statement st = AttributeSelection.dbconnect2();
    	String createSql = "CREATE TABLE IF NOT EXISTS " + Constant.CANDIDATE_TABLE + " LIKE " + Constant.DB_NAME +"." + tempo_table ;
    	st.execute(createSql);
    	st.close();
    	
    }
 
 
 // insert a tuple based on its tuple id
 	public static void insertCandidateTuple(String tupleId) throws ClassNotFoundException, SQLException{
	 
 	 Statement st = db_connection.getSt();
 	 
	 String insertSql = "INSERT INTO "+ Constant.DB_NAME+ "." + candidate_table + " SELECT * FROM " + Constant.ORI_TABLE + " where id = " +tupleId ;
	 System.out.println(insertSql);
 	 st.execute(insertSql);
 	 
 }
 	
 	public static void insertNextRank(String tupleId) throws ClassNotFoundException, SQLException{
 		 
 	 	 Statement st = db_connection.getSt();
 		 String insertSql = "INSERT INTO "+ Constant.DB_NAME+ "." + Constant.TEMP_TABLE + " SELECT * FROM " + Constant.ORI_TABLE + " where id = " +tupleId ;
 		 System.out.println(insertSql);
 	 	 st.execute(insertSql);
 	 	
 	 }
 
 	public static String getTempo_table() {
		return tempo_table;
	}

	public static void setTempo_table(String tempo_table) {
		CandidateGeneration.tempo_table = tempo_table;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public static int getLevel() {
		return level;
	}

	public static void setLevel(int level) {
		CandidateGeneration.level = level;
	}

	public static String getCandidate_table() {
		return candidate_table;
	}

	public static void setCandidate_table(String candidate_table) {
		CandidateGeneration.candidate_table = candidate_table;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		
		
	}
}
