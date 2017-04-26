package algorithm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.AttributeSelection;

public class syntheticTest {
   
	public static String ori_table = "ruuning" ;
    public static String tempo_table = "run_temp" ;
    public static int h = Constant.h1; 
    public int k = Constant.h2 ;
    public static int temp_h = 0;
    
    public void setH(int h) {
		this.h = h;
	}

	public void setK(int k) {
		this.k = k;
	}

	public int getH() {
		return h;
	}

	public int getK() {
		return k;
	}
	
	

	public String getOri_table() {
		return ori_table;
	}

	public void setOri_table(String ori_table) {
		this.ori_table = ori_table;
	}

	public String getTempo_table() {
		return tempo_table;
	}

	public void setTempo_table(String tempo_table) {
		this.tempo_table = tempo_table;
	}

	public static int getTemp_h() {
		return temp_h;
	}

	public static void setTemp_h(int temp_h) {
		syntheticTest.temp_h = temp_h;
	}
	
	public void createSynBoolean(){
		
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
    	
		String originalsql = "SELECT * FROM "+ Constant.DB_NAME + "." + Constant.ORI_TABLE;
	  
    	Statement st = AttributeSelection.dbconnect2();
    	String countsql = Constant.COUNTSQL + Constant.DB_NAME + "." + Constant.ORI_TABLE;
    	Constant.total = 0;
    	
    	while(temp_h<h ){   
    	
        BEYOND_h_GETNEXT bhg = new BEYOND_h_GETNEXT(originalsql, ori_table, tempo_table);
    
    	ResultSet rs = st.executeQuery(countsql);
    	rs.next();
    	int num = rs.getInt(1);
    	temp_h = num ;
    	}
    	
    	
    	System.out.println(h);
    	System.out.println(Constant.total);
    }
}
