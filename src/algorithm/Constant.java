package algorithm;

public class Constant {
	
	
   public static final String ORI_TABLE = "running" ;
   public static final String TEMP_TABLE = "run_tempo";
   public static final String PRESQL = "SELECT * FROM ";
   public static final String COUNTSQL = "SELECT count(*) FROM ";
   public static final String DB_NAME = "database_project";
   public static final String CANDIDATE_TABLE = "candidateset";
   public static final String OCCUR_TABLE="occurence";
   public static final String BOOL_CATE[] ={ "false", "true"};
   public static final String BOOL_PK = "id" ;
   public static final String AMAZON_PK = "ASIN";
   public static final String SYNTHETIC_TABLE = "";
   public static final int k1 = 5;
   public static final int k2 = 15;
   public static final int k3 = 20;
   public static final int h1 = 20;
   public static final int h2 = 75;
   public static final int h3 = 100;
   // the variable used to count the query number 
   public static int total;
public static String getOriTable() {
	return ORI_TABLE;
}
public static String getPresql() {
	return PRESQL;
}
public static String getOccurTable() {
	return OCCUR_TABLE;
}
public static int getK1() {
	return k1;
}
public static int getK2() {
	return k2;
}
public static int getK3() {
	return k3;
}
public static int getH1() {
	return h1;
}
public static int getH2() {
	return h2;
}
public static int getH3() {
	return h3;
}



}
