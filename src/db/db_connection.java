package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.cj.api.jdbc.Statement;
import com.mysql.cj.jdbc.PreparedStatement;

import algorithm.Constant;

public class db_connection {
	// address of link database
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/database_project?useSSL=false";
	private static final String dbName = "root";
	private static final String PWD = "root";
	
	private static int returnNumber=3;
	private static int top_k=Constant.k1;
	
	private static Statement st;
	private static Connection conn ;
	
	
	
	public static Connection getConn() {
		return conn;
	}
	public static void setConn(Connection conn) {
		db_connection.conn = conn;
	}
	public static Statement dbconnect2() throws ClassNotFoundException, SQLException{
		
		System.out.println("connect the database successfully");
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection(URL,dbName,PWD);
		st = (Statement) conn.createStatement();
		setConn(conn);
		return st;
	}
	// the function to create tempTable for boolean dataset
	public static String createNewTable(Statement st, String tableName, ArrayList<String> colName){
        // SQL statement for creating a new table
        String sql = "CREATE TABLE "+ tableName +" ( ";
        sql=sql+" "+"id"+" "+"Integer"+" NOT NULL"+", ";
        for(int i=1;i<colName.size();i++){
        sql=sql+" "+"a"+i+" "+"boolean"+" NULL"+", "; // just for boolean 
        }
        sql=sql+" PRIMARY KEY ( id ))"; 
        
        
        System.out.println(sql);
          //      + "	id integer PRIMARY KEY,\n"
           //     + "	name text NOT NULL,\n"
           //     + "	capacity real\n"
           //   + ");";
        
       return sql;
    }
	
	public static Statement getSt() throws SQLException {
		st = (Statement) getConn().createStatement();
		return st;
	}
	public static void setSt(Statement st) {
		db_connection.st = st;
	}
		// the function to create tempTable for offline boolean dataset
	// k = the value of top-k
		public static String createNewTable(Statement st ,String tableName, String originalTableName, int k){
	        // SQL statement for creating a new table
	        String sql = "CREATE TABLE IF NOT EXISTS"+ tableName +" SELECT * FROM "+ originalTableName + " limit " + k;
	        
	        
	        
	       return sql;
	    }
	// the function to create tempTable for multiple-attribute type dataset
		public static String createNewTable(Statement st, String tableName, ArrayList<String> colName, ArrayList<String> attDomain){
	        // SQL statement for creating a new table
	        String sql = "CREATE TABLE IF NOT EXISTS"+ tableName +" ( ";
	        sql=sql+" "+"id"+" "+"Integer"+" NOT NULL"+", ";
	        for(int i=1;i<colName.size();i++){
	        sql=sql+" "+"a"+i+" "+"boolean"+" NULL"+", "; // just for boolean 
	        }
	        sql=sql+" PRIMARY KEY ( id ))"; 
	        
	        
	        System.out.println(sql);
	          //      + "	id integer PRIMARY KEY,\n"
	           //     + "	name text NOT NULL,\n"
	           //     + "	capacity real\n"
	           //   + ");";
	        
	       return sql;
	    }
		
		// clear everything in this table 
		void destroyTable (String table_name , Statement st) throws SQLException {
			
			String destroySql = "TRUNCATE TABLE " + table_name;
			st.execute(destroySql);
		}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection(URL,dbName,PWD);
		System.out.println("connect the database successfully");
		Statement st = (Statement) conn.createStatement();
		 //get the original table 
		 String query = "SELECT * FROM d LIMIT "+returnNumber;
		 
		 ResultSet rs = st.executeQuery(query);
		 
		 ResultSetMetaData metaData = rs.getMetaData();
	        
	        //get the attribute name 
	        int count = metaData.getColumnCount(); //number of column
	        String columnName[] = new String[count];// name of attribute
            String columnType[] = new String[count];// type of attribute
	        for (int i = 1; i <= count; i++)
	        {
	           columnName[i-1] = metaData.getColumnLabel(i).replace(" ","").replaceAll("-", "");
	           columnType[i-1]= AttributeType.map.get(metaData.getColumnType(i));
	        }
          
	        // create a new table for top-k table,from the new table to calculate the query
	       // String sql=createNewTable(columnType, columnName, rs);
          
            // Step 2.B: Creating JDBC PreparedStatement 
           // preparedStatement = (PreparedStatement) conn.prepareStatement(sql);
            // Step 2.C: Executing SQL & retrieve data into ResultSet
          //  int sqlQueryResult = preparedStatement.executeUpdate();
            
           
     /* the way to insert tuple to the new table      
		 while (rs.next())
	      { 
			 
			String insertSql="INSERT INTO database_project.newd (";
			for(int i=0;i<count;i++)	{
				insertSql=insertSql+" "+columnName[i]+",";
			}
			insertSql=insertSql.substring(0, insertSql.length()-1);
			insertSql=insertSql+") VALUES (";
			
			for(int i=0;i<count;i++)	{
				if(columnType[i].equals("INT")){
				insertSql=insertSql+" "+rs.getInt(columnName[i])+",";
				}else if(columnType[i].contains("VARCHAR")){
					insertSql=insertSql+" "+"'"+rs.getString(columnName[i])+"'"+",";
				}else if (columnType[i].equals("BOOLEAN")){
					insertSql=insertSql+" "+rs.getBoolean(columnName[i])+",";
				}
			}
			insertSql=insertSql.substring(0, insertSql.length()-1);
			insertSql=insertSql+");";
			Statement st1 = (Statement) conn.createStatement();	
			System.out.println(insertSql);
			st1.executeUpdate(insertSql);

			    
	    /*        switch(columnNameTemp){
	            	case columnName[0]:
	            	int id = rs.getInt("id");
	            	break;
	            	case "rank":
	            	String rank = rs.getString("rank");
	            	break;
	            	case "Free_luggage":
	            	boolean Free_luggage= rs.getBoolean("Free luggage");
	            	break;
	            	case "Luggage record":
	            	boolean Luggage_record= rs.getBoolean("Luggage record");
	            	break;
	            	case "Legroom":
	            	boolean Legroom= rs.getBoolean("Legroom");
	            	case "Wifi":
	            	boolean Wifi=rs.getBoolean("Wifi");
	            	boolean On_time_record=rs.getBoolean("On-time Record");
	            	break;  */
			
			//System.out.println(insertSql);
			// PreparedStatement preparedStatement = null;
			// preparedStatement = (PreparedStatement) conn.prepareStatement(insertSql);
	            // Step 2.C: Executing SQL & retrieve data into ResultSet
	        //  int sqlQueryResult = preparedStatement.executeUpdate();
	        
	        
	/*		} 
		 conn.close();
		 st.close();
			}     */
	        operationNewTable(conn,columnName,columnType);
	        
	}       
	
	static void insertTuple(){
		
	}
	
	static void operationNewTable(Connection conn,String columnName[], String columnType[]) throws SQLException{
		ArrayList<String> query=new  ArrayList<String>() ;
		
		Statement st = (Statement) conn.createStatement();
		
		
		for(int i=0;i<columnType.length;i++){
			String sql="select * from newd where";
			String sql2="select * from newd where";
			if(columnType[i].equals("BOOLEAN")){
				sql=sql+" "+columnName[i]+"= 0 ;" ;
				sql2=sql2+" "+columnName[i]+"= 1 ;" ;
				System.out.println(sql);
				System.out.println(sql2);
				
				ResultSet rs = st.executeQuery(sql);
				rs.last();
				int total=rs.getRow();
				if(total<3&&total>0){
					query.add(sql);
				}
				
				ResultSet rs2 = st.executeQuery(sql2);
				rs2.last();
				int total2=rs2.getRow();
				if(total2<3&&total2>0){
					query.add(sql2);   
				}
				
			}
		
		    
		}
		
		System.out.println(query);
		}
		
	
	  

		String queryGeneration (String query){
			String newQuery = null;
			
			return newQuery;
		}
		
		String insertQuery(String[] column){
			String insertQuery = "INSERT INTO newd (";
			return insertQuery;
		}
		//This method use to create a new table
		public static String createNewTable(String []columnType, String []columnName, ResultSet rs){
	        // SQL statement for creating a new table
	        String sql = "CREATE TABLE newd ( ";
	        sql=sql+" "+columnName[0]+" "+columnType[0]+" NOT NULL"+", ";
	        for(int i=1;i<columnName.length;i++){
	        sql=sql+" "+columnName[i]+" "+columnType[i]+" NULL"+", ";
	        }
	        sql=sql+" PRIMARY KEY ( id ))"; 
	        
	        
	        System.out.println(sql);
	          //      + "	id integer PRIMARY KEY,\n"
	           //     + "	name text NOT NULL,\n"
	           //     + "	capacity real\n"
	           //   + ");";
	        
	       return sql;
	    }
		public static int getReturnNumber() {
			return returnNumber;
		}
		public static void setReturnNumber(int returnNumber) {
			db_connection.returnNumber = returnNumber;
		}
		public static int getTop_k() {
			return top_k;
		}
		public static void setTop_k(int top_k) {
			db_connection.top_k = top_k;
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
	}


