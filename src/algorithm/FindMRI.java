package algorithm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import db.db_connection;

// The method that find minimal rare itemset = find the beyond-h minimal query for 
// equals to find the beyond-h minimal query
public class FindMRI {
	// input  dataset + min_supp
	// all_MRI all beyond-h minimal query (infrequent itemset)
	// all_fre all frequent itemset
	// all_can all candidate set 
	ArrayList<ArrayList<String>> all_MRI = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> all_fre = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> all_can = new ArrayList<ArrayList<String>>();
	
	
	HashMap<String, Integer> count= new HashMap<String, Integer>();
	
	
    FindMRI(ArrayList<String> dataset, int min_sup) throws ClassNotFoundException, SQLException{
    	int index = 0 ;
    	ArrayList<String> temp = dataset ; 
    	
    	
    	all_can.add(temp);        // C1  -> {1-itemsets}     
    	//while (Ci is not empty) 
    	while(index < all_can.size() && !all_can.get(index).isEmpty()&& all_can.get(index).size()!=0){
    		System.out.println("not empty");
    		// SupportCount(Ci)
    		supportCount(all_can.get(index), min_sup , index);
    		
    		index = index + 1;
    		System.out.println("index = " +index);
    		System.out.println("size "+ all_MRI.size());
    		
    		System.out.println(all_fre);
        	System.out.println(all_MRI);
        	System.out.println(all_can);
    	}
		
    	
    }
     
    public ArrayList<ArrayList<String>> getAll_MRI() {
		return all_MRI;
	}
	public void setAll_MRI(ArrayList<ArrayList<String>> all_MRI) {
		this.all_MRI = all_MRI;
	}
	public ArrayList<ArrayList<String>> getAll_fre() {
		return all_fre;
	}
	public void setAll_fre(ArrayList<ArrayList<String>> all_fre) {
		this.all_fre = all_fre;
	}
	public ArrayList<ArrayList<String>> getAll_can() {
		return all_can;
	}
	public void setAll_can(ArrayList<ArrayList<String>> all_can) {
		this.all_can = all_can;
	}
     
	static ArrayList<String[]> predicate = new ArrayList<String[]>();
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
		
		  }	
	}
     
    private ArrayList<String> generateNext(ArrayList<String> f,int level) {
		// TODO Auto-generated method stub
		String reallist[] = f.toArray(new String[f.size()]);
		int list[]= new int[reallist.length];
		for(int i=0;i<list.length;i++){
			list[i]=i;
			
		}
		
		combination(reallist, list, level , 0, reallist.length);
		ArrayList<String> temp = new ArrayList<String>();
		System.out.println("predicate size " + predicate.size());
		System.out.println(predicate);
		for(String [] l:predicate){
			for(String m : l){
				System.out.println(m);
			}
		}
		for(String[] i :predicate){
			String k = "";
			System.out.println("len "+ i.length);
			for(int j= 0; j<i.length-1; j++){
				k = k + i[j]+", ";
			}
			k = k + i[i.length-1] ;
			System.out.println("k " + k);
		    temp.add(k);
		   
		}
		
		 predicate.clear();
	//	System.out.println(" temp " + temp);
		 System.out.println("here temp" + temp);
		return temp;
	}

	// counts the support of the candidate itemsets
    // dataset = c
    private void supportCount(ArrayList<String> dataset, int k , int index ) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
    	Statement st = db_connection.dbconnect2();
    	// i will be something like "a1 = 0 ";
    	ArrayList<String> tempF = new ArrayList<String>();
    	ArrayList<String> tempR = new ArrayList<String>();
    	for(String i:dataset){
    		
    		String sql = "SELECT count(*) FROM database_project.run_tempo WHERE "+ i;
    		sql = sql.replace(",", " and ");
    		System.out.println("before query " + sql);
    		ResultSet rs = st.executeQuery(sql);
    		rs.next();
    		int num = rs.getInt(1);
    		if(num < k && num >0){
    		//	Ri   {r belong to Ci | support(r) < min_supp} 
    			System.out.println("add this to R " + sql);
    			tempR.add(i);
    		}else if (num >= k){
    		//   Fi   {f belong to Ci | support(f) >= min_supp}	
    			System.out.println("add this to F " + sql);
    			tempF.add(i);
    		}
    	}
    	
    	System.out.println(tempR);
    	System.out.println(tempF);
    	
    	if(tempR.size()!=0){
    	all_MRI.add(tempR);
    	System.out.println("add to MRI:");
    	System.out.println(all_MRI);
    	}
    	
    	//Ci+1  =  Apriori-Gen(Fi)
    	if(tempF.size()!=0){
    		System.out.println("add to F:");
    		all_fre.add(tempF);
    		System.out.println(all_fre);
    		if (generateNext(tempF, index+1).size()!=0 && ! generateNext(tempF, index+1).isEmpty()){
    			
    		    all_can.add(generateNext(tempF, index+1));
    		for(ArrayList<String> j: all_can){
    		 for(String i:j){
    			 System.out.println("all_can: "+ i);
    		 	}
    	    }
    	}
    }	
    	
    	st.close();
	}

	// test whether this class work
    public static void main(String[] args) throws ClassNotFoundException, SQLException 
	{
    	ArrayList<String> dataset = new ArrayList<String>();
    	dataset.add("a1 = 0");
    	dataset.add("a2 = 1");
    	dataset.add("a3 = 0");
    	dataset.add("a4 = 1");
    	dataset.add("a5 = 0");
    	FindMRI  fmr = new FindMRI(dataset, 3);
    	
    	for(ArrayList<String> i: fmr.all_fre){
    	System.out.println(i);
    	}
    	for(ArrayList<String> i: fmr.all_can){
        	System.out.println(i);
        	}
    	for(ArrayList<String> i: fmr.all_MRI){
        	for(String j : i){
        		j=j.replace(",", " AND ");
        		System.out.println("SELECT * FROM running WHERE " + j+ " ");
        		}
        	}
    	
	}
}
