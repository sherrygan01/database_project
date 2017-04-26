package algorithm;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import db.AttributeType;
import db.db_connection;

public class BEYOND_h_GETNEXT {
	//the original user-specified query
	private String originalsql;
	// attDomian used to record the domain of categorical attribute
	// the key stands for the attribute name; the ArrayList stands for the attribute possible value
	// "a1"=["false","true"] stands for attribute a1 has two possible value
    public HashMap<String, ArrayList<String>> attDomain=new HashMap<String, ArrayList<String>>();
    public String ori_table = "ruuning" ;
    public String tempo_table = "run_temp" ;
    // the value of k; in synthetic dataset, k is set different value;
    public int k = 8 ;
    
	BEYOND_h_GETNEXT(String originalsql,String ori_table, String tempo_table) throws ClassNotFoundException, SQLException{
		
		this.originalsql=originalsql;
		this.ori_table = ori_table;
		this.tempo_table = tempo_table;
		
		// use this sql to get the top h tuple from the database 
		// create the temporary table to store the top-h tuples
		db_connect(originalsql);
	}
	
	void db_connect(String sql) throws ClassNotFoundException, SQLException{
		//connect the database
		Statement st = db_connection.dbconnect2(); 
		// get the non-numeric attribute name from original table
		ResultSet rs = st.executeQuery(sql);
		ResultSetMetaData metaData = rs.getMetaData();
		int count = metaData.getColumnCount(); //number of column
		// colName :store the categorical attribute 
		ArrayList<String> colName = new ArrayList<String>() ;
		
		String columnName[] = new String[count];// name of attribute
        String columnType[] = new String[count];// type of attribute
        
        // get the categorical attribute 
		for (int i = 1; i <=count; i++)
        {  
           columnName[i-1] = metaData.getColumnLabel(i);
           System.out.println("columnName " + columnName[i-1]);
           columnType[i-1]=AttributeType.map.get(metaData.getColumnType(i));
           System.out.println("meta " + metaData.getColumnType(i));
           System.out.println("type " + columnType[i-1]);
           // just work for boolean dataset
           if(columnType[i-1].equals("BOOLEAN")){
        	   colName.add(columnName[i-1]);
           }
        }
		
		ArrayList <String> newColumnName = new ArrayList<String>(Arrays.asList(columnName));
		// create tempo table based on colName and k tuples into tempo table 
		db_connection.createNewTable((com.mysql.cj.api.jdbc.Statement) st, ori_table, tempo_table, k);
	}
	
	//get candidates using candidateGeneration
	void getCandidate(int k,String tempo_table) throws ClassNotFoundException, SQLException{
		//tempo_table : the name of table used to store tuples retrieve
		//h: the number of tuple we already have
		//k: value of top-k
		int h = getNumberH(tempo_table);
		CandidateGeneration cg = new CandidateGeneration(tempo_table, h, k);
		
	}
	
	// the function that get the h 
	int getNumberH(String table_name) throws SQLException, ClassNotFoundException{
		Statement st = db_connection.dbconnect2();
		String sqlcount = "select count(*) from " + Constant.DB_NAME+ "." + table_name;
		ResultSet rs = st.executeQuery(sqlcount);
		rs.next();
		int num = rs.getInt(1);
		
		return num;
	}
	
	void testCandidate(){
		
	}
	
	
	// test whether this algorithm works
	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		
		
	}
}
