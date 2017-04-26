package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.cj.api.jdbc.Statement;
import com.mysql.cj.jdbc.PreparedStatement;

public class syntheticDataset {
	// address of link database
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/database_project?useSSL=false";
	private static final String dbName = "root";
	private static final String PWD = "root";
	
	private static int returnNumber=3;
	private static final int TupleNumber =100;
	private static final int AttributeNumber = 10;
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection(URL,dbName,PWD);
		System.out.println("connect the database successfully");
		Statement st = (Statement) conn.createStatement();
		//create a new dataset 
		String sql=createNewTable();
		st.execute(sql);
		//generate tuple and insert it to table
		
		for (int i=0; i<TupleNumber ;i++){
			st.executeUpdate(insert(i));
		}
		
	  
	}       
	
	   public static String insert(int id){
		   String insertSql= "INSERT INTO database_project.running (";
		   insertSql=insertSql+"id,";
		   for(int i=1;i<AttributeNumber;i++){
			   insertSql=insertSql+"a"+i+",";
		   }
		   insertSql=insertSql+"a10) ";
		   insertSql=insertSql+"VALUES (";
		   insertSql=insertSql+id+",";
		   for(int i=1;i<AttributeNumber;i++){
			   int j=(int)(Math.random()*2);
			   
			   insertSql=insertSql+j+",";
		   }
		   insertSql=insertSql+(int)(Math.random()*2)+")" ;
		   
		   System.out.println(insertSql);
		return insertSql;
	   }
		public static String createNewTable(){
	        // SQL statement for creating a new table
	        String sql = "CREATE TABLE IF NOT EXISTS running ( ";
	        sql=sql+" "+"id"+" "+"Integer"+" NOT NULL"+", ";
	        for(int i=1;i<=AttributeNumber;i++){
	        sql=sql+" "+"a"+i+" "+"boolean"+" NULL"+", ";
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
			syntheticDataset.returnNumber = returnNumber;
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

		public static int getTuplenumber() {
			return TupleNumber;
		}

		public static int getAttributenumber() {
			return AttributeNumber;
		}
		
		
		
	}
