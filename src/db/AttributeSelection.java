package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mysql.cj.api.jdbc.Statement;

import algorithm.Constant;
import algorithm.DAG;

public class AttributeSelection {
//in this algorithm, given h tuples, m categorical attributes (just suppose their domain is 0 and 1 first)
//calculate their occurrence 	
//create a new table to store these data
	
	
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/database_project?useSSL=false";
	private static final String dbName = "root";
	private static final String PWD = "root";
	public static ArrayList<ArrayList<String>> GroupQuery= new ArrayList<ArrayList<String>>();
	// attDomian used to record the domain of categorical attribute
	public HashMap<Integer, ArrayList<String>> attDomain=new HashMap<Integer, ArrayList<String>>();

	int current_level=1;
	static int count=0;
	public static ArrayList<String[]> predicate=new ArrayList<String[]>();
	static ArrayList<String>   predicateStr= new ArrayList<String>();
	static ArrayList<String>   executeQuery= new ArrayList<String>();
	public String db_name = "database_project";
	public String ori_table ;
	public static String tempo_table ;
	public String occur_table = "occurrence";
 	int level;
	boolean flag;
	String currentTuples;
	HashMap<String, String[]> attributeDom=new HashMap<String, String[]>();
	static int index=0 ;
	static ArrayList <String> indexS =new ArrayList<String>();
	static ArrayList<String> colName = new ArrayList<String>();
	static Statement st ;
	
	public static Statement getSt() {
		
		return st;
	}

	public static void setSt(Statement st) {
		AttributeSelection.st = st;
	}

	public AttributeSelection(String tempo_table,int level) throws ClassNotFoundException, SQLException{
		this.tempo_table=tempo_table;
		this.level=level;
		
		dbconnect(tempo_table, level);
	}
	
	public static Statement dbconnect2() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection(URL,dbName,PWD);
		System.out.println("connect the database successfully");
		Statement st = (Statement) conn.createStatement();
		return st;
	}
	
	
	 void dbconnect(String tempo_table, int level) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection(URL,dbName,PWD);
		System.out.println("connect the database successfully");
		Statement st = (Statement) conn.createStatement();
		this.st = st;
		
	        /* SQL statement for creating a new table
	        String sql = "CREATE TABLE occurrence ( ";
	        sql=sql+" "+"predicate "+"VARCHAR(250) " +"NOT NULL"+", ";
	       
	        sql=sql+" "+"level "+" "+"INT "+" NULL"+", ";
	        sql=sql+" "+"times "+" "+"INT "+" NULL"+", ";
	        sql=sql+" PRIMARY KEY ( predicate ))"; 
	        
	        System.out.println(sql);
	        st.execute(sql); */
		//get the original table 
		// String query = "SELECT * FROM run_tempo" ;
		 
		 String query=Constant.PRESQL + Constant.DB_NAME + "." + tempo_table +" ;";
		 System.out.println(query);
		 ResultSet rs = st.executeQuery(query);
		 ResultSetMetaData metaData = rs.getMetaData();
	        
	        // get the attribute name 
		    // get name of attribute
		    // get type of attribute 
		    // get the categorical attribute
	        int count = metaData.getColumnCount(); 
	    
	        String columnName[] = new String[count];
            String columnType[] = new String[count];
            ArrayList<String> colName=new ArrayList<String>(); 
            
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
	        
	        
	        this.colName = colName ;
	        
		int[] list=new int[colName.size()];
		for(int i=0;i<list.length;i++){
		 	list[i]=i;
		}
		
		String reallist[]=(String[])colName.toArray(new String[colName.size()]);
		predicate.clear();
		count=0;
		
		//combination : choose n attribute from m attribute 
		//if level = 1, choose 1 attribute from m attribute
		//reallist: the list of all categorical attributes
		//list 
		//0:low
		combination(reallist,list,level,0,reallist.length);
		//generate the query base on the chose attributes and category; then insert the query result to the table
		counter(colName, colName.size(),level,st,predicate);
		
	
		
		
	    }
    
	
	
	public ArrayList<String> getColName() {
		return colName;
	}

	// generate queryGroup
	static void counter(ArrayList<String> colName,int attributeNum, int level, Statement st, ArrayList<String[]> predicate) throws SQLException{
		System.out.println("into the counter method");
		System.out.println("predicate : "+predicate.size());
	    String cate[]=Constant.BOOL_CATE;
		String presql="SELECT * from "+Constant.DB_NAME+ "." + tempo_table+" WHERE ";

		for(int m=0;m<predicate.size();m++){
			
		List<String[]> list=new ArrayList<String[]>();	
		String test1[]=predicate.get(m);
		String test2[]=cate;
		for(String i:test1){
		    String temp[]=new String[test2.length];
		    String sqltemp=i;
			for(int j=0;j<test2.length;j++){
				sqltemp=sqltemp+ "="+ test2[j];
				temp[j]=sqltemp;
				sqltemp=i;
			}
		   list.add(temp);
		}
	
		for(String []k:list){
			for(int i=0;i<k.length;i++){
				System.out.print("all " +  k[i]+" ");
			}
			System.out.println();
		}
		
		if(!indexS.isEmpty()){
			System.out.println("indexS " + indexS);
			String []temp = indexS.toArray(new String[indexS.size()]);
			ArrayList<String> temoAlist= new ArrayList<String>(Arrays.asList(temp));
			GroupQuery.add(temoAlist);
			System.out.println("a gq: " + GroupQuery);
			
		}
		indexS.clear();
		System.out.println("clear a gq: " + GroupQuery);
		// processing querygroup
		// generate query that need to execute
		st(presql, new int[0],list); 
		
		}
		
		String []temp2 = indexS.toArray(new String[indexS.size()]);
		ArrayList<String> temoAlist= new ArrayList<String>(Arrays.asList(temp2));
		
		GroupQuery.add(temoAlist);
		System.out.println("a gq: " + GroupQuery);
		
		// execute the query 
		for(int n=0;n<executeQuery.size();n++){
			
//			System.out.println("candidate query " + executeQuery.get(n));
			ResultSet rs=st.executeQuery(executeQuery.get(n));
			rs.last();
			int temp=rs.getRow();
			
			String sql_count="INSERT INTO database_project.occurrence (";
			sql_count=sql_count+"predicate"+","+"level,"+" "+"times) ";
			String pre=predicateStr.get(n);
			sql_count=sql_count+"VALUES("+'"'+pre+'"'+",";
			sql_count=sql_count+level+","+temp+")";
			sql_count=sql_count+ " ON DUPLICATE KEY UPDATE "+ "level="+ level + ", times="+temp;
			
	//		System.out.println("sql_count "+ sql_count);
		    st.execute(sql_count);
		}
	/*	for(int n=0;n<predicate.get(k).length;n++){
			for(int m=0;m<cate.length;m++){
				//
				sql= sql + predicate.get(k)[n]+ "=" +cate[m]+" ";
				
			/*	ResultSet rs=st.executeQuery(sql);
				rs.last();
				System.out.println(rs.getRow());
				int temp=rs.getRow();//count the number of this predicate and insert it into table
				String sql_count="INSERT INTO database_project.occurrence (";
				sql_count=sql_count+"predicate"+","+"level,"+" "+"times) ";
				String pre=predicate.get(k)[n]+ "=" +cate[m];
				sql_count=sql_count+"VALUES("+'"'+pre+'"'+",";
				sql_count=sql_count+level+","+temp+")";
				System.out.println(sql_count);
			    st.execute(sql_count);
			
				
			}
				
				System.out.println(sql);
				
		}	    */
		
	}		

	
	public static void st(String presql, int[] indexs,List<String[]> list){
        
			int size=list.size();
			if(indexs.length==0){
				indexs=new int[size];

				for(int i=0;i<size;i++){
					indexs[i]=0;
				}
			}	
			
			for(int i=0;i<size;i++){	
				if(indexs[i]>list.get(i).length-1){
					if(i!=size-1){
						indexs[i]=0;
						indexs[i+1]=indexs[i+1]+1;
					}else{
					return;
				}
			}	
		}
			
		String temp=presql;
		for(int i=0;i<size-1;i++){
			temp=temp+list.get(i)[indexs[i]].toString()+" AND ";
	}
		temp=temp+list.get(size-1)[indexs[size-1]].toString()+" ";
        System.out.println("temp " + temp );
       
        
        String tempPredicateStr=temp.replace(presql, "").replaceAll(" AND ",",");
        indexS.add(tempPredicateStr);
        predicateStr.add(tempPredicateStr);
		executeQuery.add(temp);
        
		indexs[0]+=1;
		st(presql, indexs, list);
	}
	
	 public static void combination(String reallist[],int[] list,int r,int low,int n){
		
		if(low<r){
			for(int j=low;j<n;j++){
				if((low>0&&list[j]<list[low-1])||low==0){
					String temp=reallist[low];
					reallist[low]=reallist[j];
					reallist[j]=temp;
					  int temp2=list[low];
					  list[low]=list[j];
					  list[j]=temp2;
					combination(reallist,list,r,low+1,n);
					temp=reallist[low];
					reallist[low]=reallist[j];
					reallist[j]=temp;
					 temp2=list[low];
					  list[low]=list[j];
					  list[j]=temp2;
		  }
	}
 }
	  if(low==r){
		  String temp[]=new String[r];	  
		  for(int i=0;i<r;i++){
	
		  temp[i]=reallist[i];
		  }
		  predicate.add(temp);
		  count++;
		  }	
	}
	 
	 public static String insert(int id){
		   String insertSql= "INSERT INTO database_project.new_bool (";
		   insertSql=insertSql+"id,";
		   for(int i=1;i<12;i++){
			   insertSql=insertSql+"a"+i+",";
		   }
		   insertSql=insertSql+"a12) ";
		   insertSql=insertSql+"VALUES (";
		   insertSql=insertSql+id+",";
		   for(int i=1;i<12;i++){
			   int j=(int)(Math.random()*2);
			   
			   insertSql=insertSql+j+",";
		   }
		   insertSql=insertSql+(int)(Math.random()*2)+")" ;
		   
		   System.out.println(insertSql);
		return insertSql;
	   }
	
	
	
	
	
	// the function that check this group of query works or not 
	 // if this level works return true, else return false
    static boolean checkQueryGroup(Statement st) throws SQLException, ClassNotFoundException, IOException{
    	boolean realFlag = false ;
    	System.out.println("check the work query");
    	
    	
    	Iterator<ArrayList<String>> iter = GroupQuery.iterator();  
    	System.out.println("grpipquery" + GroupQuery);
    	while(iter.hasNext()){  
    		
    		boolean flag=true;
    	    ArrayList<String> s = iter.next();  
    	    for(String j:s){
				String sql2 = "SELECT * from database_project.occurrence WHERE predicate = " +'"'+ j +'"';
				System.out.println(sql2);
				
				
				ResultSet rs=st.executeQuery(sql2);
				rs.next();
				String times = rs.getString("times");
				System.out.println(times);
				int b=Integer.parseInt(times);
				if(b>=Constant.k1){
					flag = false ;
					System.out.println("this group not work " + j);
					iter.remove();
					break;
				}
			
			}
    	    
    	    if(flag){
				realFlag = true; 
				System.out.println("here true " + iter);
				String pre = "SELECT * from database_project.running WHERE" ;
				// i stands for the query group that works 
				generateDAG(pre , st , s);
				break;
			}
    	   }  
    	  
    	
    	
	/*	for(ArrayList<String> i:GroupQuery){
			System.out.println("check this group" + i);
			boolean flag=true;
			
			for(String j:i){
				
				String sql2 = "SELECT * from database_project.occurrence WHERE predicate = " +'"'+ j +'"';
				System.out.println(sql2);
				
				
				ResultSet rs=st.executeQuery(sql2);
				rs.next();
				String times = rs.getString("times");
				System.out.println(times);
				int b=Integer.parseInt(times);
				if(b>=10){
					flag = false ;
					System.out.println("this group not work " + j);
					break;
				}
			
			}
			
			// if works, generate DAG through these queries 
			if(flag){
				realFlag = true; 
				System.out.println("here true " + i);
				String pre = "SELECT * from database_project.running WHERE" ;
				// i stands for the query group that works 
				generateDAG(pre , st , i);
				break;
			}
		}    */
		
		if (!realFlag){
			System.out.println("this level is not work");
			GroupQuery.clear();
			return false;
		}else return true;
    }
    
	private static void generateDAG(String presql , Statement st, ArrayList<String> i) throws SQLException, ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		DAG dag = new DAG(presql, st, i ,colName);
	/*	String pre = "SELECT * FROM database_project.new_bool ";
		String tuple1 = "1";
		String tuple2 = "0";
		Statement st2 = dbconnect2();
		ResultSet rs1= st.executeQuery(pre+"WHERE id =" +tuple1);
		ResultSet rs2= st2.executeQuery(pre+ "WHERE id ="+ tuple2);
		ArrayList<String> temp = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();  */
 		
	}           
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
	{
	
	/*	String[] test={"a","b","c","d","e"};
	//	int []newlist=new int[test.length];
	//	for(int i=0;i<test.length;i++){
	//		newlist[i]=i;
		}
		
		
	   combination(test, newlist, 2 , 0, test.length);
		// combination2(test, newlist, 1, 0 , 5);
	   for(String[] i: predicate){
		   for(String j: i){
			   System.out.print(j+" ");
		   }
		   System.out.println();
	   }
		System.out.println(predicate.size());        */
		String table_name = "run_tempo";
		int level = 1;
		int h = Constant.h1;
		int num = 0;
		
		String sql = "TRUNCATE database_project.occurrence ;"  ;
	//	st.execute(sql);
		
		
        db_connection.dbconnect2();
        Constant.total = 0;
        while (num<h){
		AttributeSelection as = new AttributeSelection(table_name, level);
		Statement st = db_connection.getSt();
        ResultSet rs = st.executeQuery("SELECT count(*) FROM " + table_name) ;
        rs.next();
        num = rs.getInt(1);
        System.out.println("tuple number "+ num);
		System.out.println("predicateStr" + predicateStr);
		System.out.println(executeQuery);
		boolean flag = checkQueryGroup(st);
		
		if(!flag){
		System.out.println(flag + " need next level");	
		level++;
		}
		
        }
        System.out.println("total query " + Constant.total);
/*    List<String[]> list=new ArrayList<String[]>();
		String test1[]={"a","b","c"};
		String test2[]={"0","1","2"};
		for(String i:test1){
		    String temp[]=new String[test2.length];
		    String sqltemp=i;
			for(int j=0;j<test2.length;j++){
				sqltemp=sqltemp+ "="+ test2[j];
				temp[j]=sqltemp;
				sqltemp=i;
			}
		   list.add(temp);
		}
		
		for(String []k:list){
			for(int i=0;i<k.length;i++){
				System.out.print(k[i]+" ");
			}
			System.out.println();
		}
		st(new int[0],list);  */
		
	//	System.out.println("GroupQuery " + GroupQuery);
	//	Statement st=dbconnect2();
        
	//	
        
	}

	public static ArrayList<ArrayList<String>> getGroupQuery() {
		return GroupQuery;
	}

	public static void setGroupQuery(ArrayList<ArrayList<String>> groupQuery) {
		GroupQuery = groupQuery;
	}

	public HashMap<Integer, ArrayList<String>> getAttDomain() {
		return attDomain;
	}

	public void setAttDomain(HashMap<Integer, ArrayList<String>> attDomain) {
		this.attDomain = attDomain;
	}

	public int getCurrent_level() {
		return current_level;
	}

	public void setCurrent_level(int current_level) {
		this.current_level = current_level;
	}

	public static ArrayList<String[]> getPredicate() {
		return predicate;
	}

	public static void setPredicate(ArrayList<String[]> predicate) {
		AttributeSelection.predicate = predicate;
	}

	public static ArrayList<String> getPredicateStr() {
		return predicateStr;
	}

	public static void setPredicateStr(ArrayList<String> predicateStr) {
		AttributeSelection.predicateStr = predicateStr;
	}

	public static ArrayList<String> getExecuteQuery() {
		return executeQuery;
	}

	public static void setExecuteQuery(ArrayList<String> executeQuery) {
		AttributeSelection.executeQuery = executeQuery;
	}

	public String getDb_name() {
		return db_name;
	}

	public void setDb_name(String db_name) {
		this.db_name = db_name;
	}

	public String getOri_table() {
		return ori_table;
	}

	public void setOri_table(String ori_table) {
		this.ori_table = ori_table;
	}

	public static String getTempo_table() {
		return tempo_table;
	}

	public static void setTempo_table(String tempo_table) {
		AttributeSelection.tempo_table = tempo_table;
	}

	public String getOccur_table() {
		return occur_table;
	}

	public void setOccur_table(String occur_table) {
		this.occur_table = occur_table;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getCurrentTuples() {
		return currentTuples;
	}

	public void setCurrentTuples(String currentTuples) {
		this.currentTuples = currentTuples;
	}

	public HashMap<String, String[]> getAttributeDom() {
		return attributeDom;
	}

	public void setAttributeDom(HashMap<String, String[]> attributeDom) {
		this.attributeDom = attributeDom;
	}

	public static int getIndex() {
		return index;
	}

	public static void setIndex(int index) {
		AttributeSelection.index = index;
	}

	public static ArrayList<String> getIndexS() {
		return indexS;
	}

	public static void setIndexS(ArrayList<String> indexS) {
		AttributeSelection.indexS = indexS;
	}

	public static String getPwd() {
		return PWD;
	}
	

}
