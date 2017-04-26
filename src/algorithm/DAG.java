package algorithm;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.mysql.cj.api.jdbc.Statement;
import db.AttributeSelection;
import db.db_connection;

 // this class can generate candidate based on useful query 
 
public class DAG{
	
	public static String top_klimit = " limit 10 ";
	static String u = null;
	static String v = null;
	static String last_tuple ;
	
	HashMap<String, ArrayList<String>> Digraph=new HashMap<String, ArrayList<String>>();
	HashMap<String, ArrayList<String>> revDigraph=new HashMap<String, ArrayList<String>>();
	ArrayList<String> node=new ArrayList<String>();
	ArrayList<String> known = new ArrayList<String>();
    ArrayList<String> head_chain = new ArrayList<String>();
    ArrayList<ArrayList<String>> multiChain = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> temp_multiChain = new ArrayList<ArrayList<String>>();
    // the th tuple
	ArrayList<String> colName = new ArrayList<String>();
	String pre = " SELECT * FROM database_project.running ";
	// Statement st =AttributeSelection.dbconnect2();
	ArrayList<String> candidateSet = new ArrayList<String>();
	
	// pre = "SELECT * FROM TABLE_NAME WHERE 
	// i : the set of useful query
	// colName 
	// base on the result of set of query to generate DAG
	// generate Digraph and revDigraph and list of linear chain in constructor
	
	public DAG(String pre,Statement st, ArrayList<String> i, ArrayList<String> colName) throws SQLException, ClassNotFoundException, IOException{
		
		if(known.isEmpty())
		getKnowTuple(st, Constant.TEMP_TABLE);
		
		this.colName = colName ;
		
		for(String j: i){
			String sql=pre;
			String k= j.replace(",", " AND ");
			sql = sql + ' ' + k +' ' ;
		    sql = sql + top_klimit;
		    // generate linear chain and edge from query result 
			System.out.println("before query : " + sql);
			ResultSet rs= st.executeQuery(sql);
			Constant.total++;
			ArrayList<String> linearChain=new ArrayList<String>();
			while(rs.next()){
				
				int temp = rs.getInt("id");
				String temp3 = Integer.toString(temp) ;
				
				if(!known.contains(temp3)){
				if(u==null){
					
					u = Integer.toString(temp) ;
					linearChain.add(temp3);
				}else{
					v = Integer.toString(temp) ;
					System.out.println("add this edge :" + u + " " + v);
					addDAG(u, v);
					u = v ;
					linearChain.add(temp3);
				}
			}
		}
			
			u=null;
			v=null;
		    System.out.println(linearChain);
		    multiChain.add(linearChain);
		    
		    
		//    head_chain.add(linearChain.get(0));
		    
		//	st.executeQuery(sql);
		}
		System.out.println("dag : " + Digraph);
		System.out.println("redag : " + revDigraph);
		System.out.println(multiChain);
		
		GenerateCandidate(multiChain,st, colName);
		
		
	//	GenerateCandidate(multiChain ,st , colName);
	/*
	 * 
	 * 	for(String j: head_chain){
	
	
	
	
			if(!revDigraph.containsKey(j)||revDigraph.get(j).isEmpty()){
				for(ArrayList<String> chain: multiChain){
					if(chain.contains(j)){
						compareHead_chain(j, chain);
					}
					
				}
			}
		}
		
		for(String j: head_chain){
			if ((!revDigraph.containsKey(j)||revDigraph.get(j).isEmpty()))
				candidateSet.add(j);
		} */
		
		
	}
	
	
	void compareHead(ArrayList<String> head_chain) throws ClassNotFoundException, SQLException{
		int indexi = 0;
		int indexj = 1;
		String pre = " SELECT * FROM database_project.running ";
		Statement st =AttributeSelection.dbconnect2();
		
		while(indexi<head_chain.size()){
			while(indexj<head_chain.size()){
				if(indexi!=head_chain.size()-2&&indexj==head_chain.size()-1){
					indexi++;
					indexj=indexi+1;
					// do the pairwise comparison between tuples from different chains
					
					String temp = tuplesCompare(head_chain.get(indexi),head_chain.get(indexj), pre, st, colName);
					if(!temp.equals("not comparable")){
						if (temp.equals(head_chain.get(indexi))){
							addDAG(head_chain.get(indexi),head_chain.get(indexj));
						}else{
							addDAG(head_chain.get(indexj),head_chain.get(indexi));
						}
					}
				}else{
					indexj++;
				}
				
			}
		}   
		
	
			
	}
	
	
	
/*	void compareHead_chain(String head , ArrayList<String> chain) throws ClassNotFoundException, SQLException{
		
		for(String i:chain){
			
			String temp = tuplesCompare(i, head, pre, st, colName);
			if(!temp.equals("not comparable")){
				if(i.equals(temp)){
					addDAG(i, head);
				}else{
					addDAG(head , i);
				}
				break;
			}
		}
	}  */
	
	void linearChainAdd(ArrayList<String> linearChain){
		int indexi=0;
		int indexj=1;
	
		while(indexi<linearChain.size()){
			while(indexj<linearChain.size()){
				System.out.print(linearChain.get(indexi)+" ");
				System.out.print(linearChain.get(indexj));
				addDAG(linearChain.get(indexi),linearChain.get(indexj));
				System.out.println();
				if(indexi!=linearChain.size()-2&&indexj==linearChain.size()-1){
					indexi++;
					indexj=indexi+1;
					
				}else{
					indexj++;
				}
			}
		}
	}
	
	
	// function that search the tempo_run tuple to get the know tuple
	//tempo_table : 
	void getKnowTuple(Statement st, String tempo_table) throws SQLException{
		String get_all_sql = "SELECT * FROM "+Constant.DB_NAME + "." + tempo_table +" ;" ;
		ResultSet rs = st.executeQuery(get_all_sql);
		known.clear();
		while(rs.next()){
			String temp_id = rs.getString("id");
			known.add(temp_id);
		}
		
        if(rs.last())
		last_tuple = rs.getString("id");
	}
	
	
	
	//Given a list of multiChain, generate the candidate set
	void GenerateCandidate(ArrayList<ArrayList<String>> multiChain, Statement st, ArrayList<String> colName) throws ClassNotFoundException, SQLException, IOException{
	/*	ArrayList<String> tempCan = new ArrayList<String>();
		
		for(ArrayList<String> i:multiChain){
				if(!revDigraph.containsKey(i.get(0))||revDigraph.get(i.get(0)).isEmpty()){
					tempCan.add(i.get(0));
				}

		}
		
		int indexi=0;
		int indexj=1;
		while(indexi<tempCan.size()){
			while(indexj<tempCan.size()){
				String pre ="";
				System.out.println();
				if(indexi!=tempCan.size()-2&&indexj==tempCan.size()-1){
					indexi++;
					indexj=indexi+1;
					String temp = tuplesCompare(tempCan.get(indexi),tempCan.get(indexj),pre,st, colName);
					if(!temp.equals("not comparable")){
						if(temp.equals(tempCan.get(indexi))){
							addDAG(tempCan.get(indexi),tempCan.get(indexj));
						}else{
							addDAG(tempCan.get(indexj),tempCan.get(indexi));
						}
					}
				}else{
					indexj++;
				}
				
			}
		}   */
		
		int indexi = 0;
		int indexj = 1;
		String pre = "SELECT * FROM database_project.running ";
		System.out.println("into multiChain");
		
		while(indexi<multiChain.size()&&indexj<multiChain.size()){
			
				if(indexi!=multiChain.size()-2){
					if(indexi==indexj)
						System.out.println("same chain index!");
					if(multiChain.get(indexi).equals(multiChain.get(indexj)))
						System.out.println("here same chain!");
					linearChainCompare(multiChain.get(indexi),multiChain.get(indexj), pre, st, colName);
					
					if(indexj==multiChain.size()-1&&indexi<multiChain.size()-2){
						indexi++;
						indexj=indexi+1;
					}else{
						indexj++;
					}
					
					// do the pairwise comparison between tuples from different chains
					
				}
				else
				{
					if(indexi==indexj)
						System.out.println("else same chain!");
					if(!multiChain.get(indexi).isEmpty()&&!multiChain.get(indexj).isEmpty()&&indexi<indexj)
					linearChainCompare(multiChain.get(indexi),multiChain.get(indexj), pre, st, colName);
					break;
				}
				
			
		}   
		
		
		for(String node1: node){
			if(!revDigraph.containsKey(node1)||revDigraph.get(node1).isEmpty()){
				if(!candidateSet.contains(node1))
				candidateSet.add(node1);
				
			}
		}
		
		boolean flag = false;
		if(candidateSet.size()==1){
		    System.out.println("next rank tuple " + candidateSet.get(0));
		    remove_tuple(candidateSet.get(0),Digraph);
			remove_tuple(candidateSet.get(0),revDigraph);
			System.out.println(Digraph);
			System.out.println(revDigraph);
			System.out.println("candidateSet :"+ candidateSet);
			CandidateGeneration.insertNextRank(candidateSet.get(0));
			known.add(candidateSet.get(0));
			node.remove(candidateSet.get(0));
			
			for(int i=0;i<this.multiChain.size();i++){
				if(this.multiChain.get(i).contains(candidateSet.get(0))){
				   this.multiChain.get(i).remove(candidateSet.get(0));
				}
			}
			
			candidateSet.clear();
			temp_multiChain.clear();
			
			
			
			for(ArrayList<String> i: this.multiChain){
	//			System.out.println("print here" + this.multiChain);
				if(i.isEmpty()){
					System.out.println("run out of this chain and end this iteration");
					ResultSet rs = st.executeQuery("select count(*) from database_project.run_tempo ");
					rs.next();
					int num = rs.getInt(1);
					System.out.println("query cost: "+ Constant.total);
					System.out.println("tuple we found: "+ num);
					flag = true;
					this.multiChain.clear();
					break;
				}
				else if(revDigraph.get(i.get(0)).isEmpty()&&!temp_multiChain.contains(i)){
		//			System.out.println("add this chain" +i );
					temp_multiChain.add(i);
				}
			}
			
			System.out.println("temp_multiChain " + temp_multiChain);
			
			
		}else{
			
			
			for(ArrayList<String> i: this.multiChain){
				//	System.out.println("print here" + this.multiChain);
					if(i.isEmpty()){
						System.out.println("run out of this chain and end this iteration");
						ResultSet rs = st.executeQuery("select count(*) from database_project.run_tempo ");
						int num = rs.getInt(1);
						System.out.println("query cost: "+ Constant.total);
						System.out.println("tuple we found: "+ num);
						flag = true;
						break;
							}
					else if((!revDigraph.containsKey(i.get(0))||revDigraph.get(i.get(0)).isEmpty())&&!temp_multiChain.contains(i)){
					//	System.out.println("add this chain" +i );
						temp_multiChain.add(i);
							}
						}
			System.out.println("multiple candidate");
			System.out.println(this.multiChain);
			System.out.println(this.temp_multiChain);
			
			
			System.out.println(candidateSet);
			
			checkWithToph(candidateSet, pre, st ,colName);
		//	candidateCompare(candidateSet,pre,st,colName);
			
			if(candidateSet.size()==1){
				System.out.println("get new candidate");
				 System.out.println("next rank tuple " + candidateSet.get(0));
				    remove_tuple(candidateSet.get(0),Digraph);
					remove_tuple(candidateSet.get(0),revDigraph);
					System.out.println(Digraph);
					System.out.println(revDigraph);
					System.out.println("candidateSet :"+ candidateSet);
					CandidateGeneration.insertNextRank(candidateSet.get(0));
					known.add(candidateSet.get(0));
					node.remove(candidateSet.get(0));
					
					for(int i=0;i<this.multiChain.size();i++){
						if(this.multiChain.get(i).contains(candidateSet.get(0))){
						   this.multiChain.get(i).remove(candidateSet.get(0));
						}
					}
					
					candidateSet.clear();
					temp_multiChain.clear();
					
					
					
			}else{
				System.out.println("still cannot reject");
				CandidateTesting ct = new CandidateTesting(candidateSet,colName);
				
				if(!candidateSet.isEmpty()){
					for(int i=0;i<candidateSet.size();i++){
					System.out.println("next rank tuple " + candidateSet.get(0));
				    remove_tuple(candidateSet.get(i),Digraph);
					remove_tuple(candidateSet.get(i),revDigraph);
					System.out.println(Digraph);
					System.out.println(revDigraph);
					System.out.println("candidateSet :"+ candidateSet);
					CandidateGeneration.insertNextRank(candidateSet.get(i));
					known.add(candidateSet.get(i));
					node.remove(candidateSet.get(i));
					
					
					for(int j=0;j<this.multiChain.size();j++){
						if(this.multiChain.get(j).contains(candidateSet.get(i))){
						   this.multiChain.get(j).remove(candidateSet.get(i));
						}
					}
					
					
					
					}	
					
					candidateSet.clear();
					temp_multiChain.clear();
					
					for(ArrayList<String> k: this.multiChain){
			//			System.out.println("print here" + this.multiChain);
						if(k.isEmpty()){
							System.out.println("run out of this chain and end this iteration");
							Statement st2 = db_connection.getSt();
							ResultSet rs = st2.executeQuery("select count(*) from database_project.run_tempo ");
							rs.next();
							int num = rs.getInt(1);
							System.out.println("query cost: "+ Constant.total);
							System.out.println("tuple we found: "+ num);
							flag = true;
							this.multiChain.clear();
							break;
						}
						else if(!(revDigraph.containsKey(k.get(0))||revDigraph.get(k.get(0)).isEmpty())&&!temp_multiChain.contains(k)){
				//			System.out.println("add this chain" +i );
							temp_multiChain.add(k);
						}
					}
					
					System.out.println("temp_multiChain " + temp_multiChain);
					
					}
				}
				
			}
			
			
			
		
		
		System.out.println(revDigraph);
		
		
		
		
		if(!flag){
		GenerateCandidate(temp_multiChain, st, colName);
		}else{
			this.multiChain.clear();
			 
		}
	}
	
	void checkWithToph(ArrayList<String> candidateSet, String pre,Statement st, ArrayList<String>colName) throws ClassNotFoundException, SQLException{
		last_tuple = known.get(known.size()-1);
		
		Iterator<String> iter = candidateSet.iterator();
		
		while(iter.hasNext()){
			String temp = iter.next();
			String temp1 = tuplesCompare(temp,last_tuple,pre,st,colName );
			if(temp1.equals("not comparable")){
				iter.remove();
			}
		}
	}


	// perform pairwise comparison between tuples from two chains
	void linearChainCompare(ArrayList<String> chain1, ArrayList<String> chain2, String pre, Statement st, ArrayList<String> colName) throws ClassNotFoundException, SQLException{
		if(chain1.equals(chain2))
			System.out.println("same chain!");
		System.out.println("compare two chains: " + chain1 + " "+ chain2);
		
		
		
		
	if(!chain1.isEmpty()&&!chain2.isEmpty()){
		outerloop:
			
		for(String i:chain1){
			for(String j:chain2){
				System.out.println("compare two tuples: " + i + " "+ j);
				if(!i.equals(j)){
				String temp = tuplesCompare(i, j, pre, st, colName);
				if(temp!="not comparable"){
					if(i.equals(temp)){
	//					System.out.println("add this relationship "+ i +"->"+j );
						addDAG(i,j);
					}else{
						addDAG(j,i);
			//			System.out.println("add this relationship "+ j +"->"+i );
					}
					break outerloop;
					}
				}
			}
		}
			
		if(!revDigraph.containsKey(chain1.get(0))||!revDigraph.containsKey(chain2.get(0))||revDigraph.get(chain1.get(0)).isEmpty()||revDigraph.get(chain2.get(0)).isEmpty()||!revDigraph.get(chain1.get(0)).contains(chain2.get(0))||!revDigraph.get(chain2.get(0)).contains(chain1.get(0))){
			outerloop:
				
				for(String i:chain2){
					for(String j:chain1){
						System.out.println("compare two tuples: " + i + " "+ j);
						if(!i.equals(j)){
						String temp = tuplesCompare(i, j, pre, st, colName);
						if(temp!="not comparable"){
							if(i.equals(temp)){
			//					System.out.println("add this relationship "+ i +"->"+j );
								addDAG(i,j);
							}else{
								addDAG(j,i);
					//			System.out.println("add this relationship "+ j +"->"+i );
							}
							break outerloop;
							}
						}
					}
				}
		}
	}	
		
	
}
	
	void addDAG(String node1, String node2){
		
		System.out.println("add this two node: " + node1 + " "+ node2);
		
		if(!node.contains(node1)){
			node.add(node1);	
		}
		if(!node.contains(node2)){
			node.add(node2);	
		}
		
		if(!Digraph.containsKey(node1)){
			ArrayList<String> tempList=new ArrayList<String>();
			tempList.add(node2);
			Digraph.put(node1, tempList);
			
		}
		else if(!Digraph.get(node1).contains(node2)){
			ArrayList<String> tempList=new ArrayList<String>();
			tempList=Digraph.get(node1);
			tempList.add(node2);
			Digraph.put(node1, tempList);
		}
		
		if(!revDigraph.containsKey(node2)){
			ArrayList<String> tempList=new ArrayList<String>();
			tempList.add(node1);
			revDigraph.put(node2, tempList);
			
		}
		else if(!revDigraph.get(node2).contains(node1)){
			ArrayList<String> tempList=new ArrayList<String>();
			tempList=revDigraph.get(node2);
			tempList.add(node1);
			revDigraph.put(node2, tempList);
		
		}
		
		System.out.println("revDigraph : " + revDigraph);
		
		
	}		
	public HashMap<String, ArrayList<String>> getDigraph() {
		return Digraph;
	}

	public void setDigraph(HashMap<String, ArrayList<String>> digraph) {
		Digraph = digraph;
	}

	public HashMap<String, ArrayList<String>> getRevDigraph() {
		return revDigraph;
	}

	public void setRevDigraph(HashMap<String, ArrayList<String>> revDigraph) {
		this.revDigraph = revDigraph;
	}
    
	ArrayList<String> temp_candidate=new ArrayList<String>();
	void generateCandidate(ArrayList<String> known_node, HashMap<String, ArrayList<String>> revDigraph){
		ArrayList<String> candidate=new ArrayList<String>();
		ArrayList<String> remove_index=new ArrayList<String>();
		
		Iterator iter = revDigraph.entrySet().iterator();
		
		while (iter.hasNext()) {
		HashMap.Entry entry = (HashMap.Entry) iter.next();
		ArrayList<String> list = (ArrayList<String>) entry.getValue();
		String tuple = (String) entry.getKey();
		if(list.isEmpty()){
			if(!known_node.contains(tuple)){
				
				candidate.add(tuple);
		}else{
			    remove_index.add(tuple);
		}
	}
		
		}		
		/*
		for(String tuple:revDigraph.keySet()){
			if(!revDigraph.containsKey(tuple)||revDigraph.get(tuple).isEmpty()){
				if(!known_node.contains(tuple)){
					candidate.add(tuple);
					System.out.println("add to candidate " + tuple);
					revDigraph.remove(tuple);
					Iterator iter = revDigraph.entrySet().iterator();
					while (iter.hasNext()) {
					HashMap.Entry entry = (HashMap.Entry) iter.next();
					ArrayList<String> list = (ArrayList<String>) entry.getValue();
					if(list.contains(tuple)){
						list.remove(tuple);
					}
					entry.setValue(list);
					}
					
				}else if(revDigraph.containsKey(tuple)){
					System.out.println("here");
					remove_index.add(tuple);
				}
			}
	}
		*/
		
		if(!candidate.isEmpty()&&remove_index.isEmpty()){
			System.out.println("add this empty: "+candidate );
			
			temp_candidate.addAll(candidate);
			System.out.println(temp_candidate);

			System.out.println(revDigraph);
		}else if(!candidate.isEmpty()&&!remove_index.isEmpty()){
			temp_candidate.addAll(candidate);
                 for(String tuple:candidate){
				
				remove_tuple(tuple,revDigraph);
			}
               for(String tuple:remove_index){
				
				remove_tuple(tuple,revDigraph);
			}
			System.out.println("round");
			System.out.println("loop");
			System.out.println(revDigraph);
			generateCandidate(known_node,revDigraph);
		}
		else if(!remove_index.isEmpty()){
			for(String tuple:remove_index){
				remove_tuple(tuple,revDigraph);	
				
			}
			System.out.println("round");
			
			generateCandidate(known_node,revDigraph);
		}
	}
	
	void remove_tuple(String tuple,HashMap<String, ArrayList<String>> revDigraph){
		Iterator iter = revDigraph.entrySet().iterator();
		while (iter.hasNext()) {
		HashMap.Entry entry = (HashMap.Entry) iter.next();
		ArrayList<String> list = (ArrayList<String>) entry.getValue();
		if(list.contains(tuple)){
			list.remove(tuple);
		}
		entry.setValue(list);
		}
		
		revDigraph.remove(tuple);
	}
	
	void candidateCompare(ArrayList<String> CandidateSet, String pre, Statement st, ArrayList<String> colName) throws ClassNotFoundException, SQLException{
		int indexi = 0;
		int indexj = 1;
		
		ArrayList<Integer> index =new ArrayList<Integer>();
		while(indexi<CandidateSet.size()){
			while(indexj<CandidateSet.size()){
				String temp = tuplesCompare(CandidateSet.get(indexi),CandidateSet.get(indexj), pre, st, colName);
				if(!temp.equals("not comparable")){
					if(temp.equals(CandidateSet.get(indexi)))
						index.add(indexj);
				}
				if(indexi!=CandidateSet.size()-2&&indexj==CandidateSet.size()-1){
					indexi++;
					indexj=indexi+1;
					
				}else{
					indexj++;
				}
			}
		}
		
		
		for(int i: index){
			CandidateSet.remove(i);
		}
	}
	
	// Given 2 tuples id , return the tuple which have higher rank
	// pre : SELECT * FROM database_project.running 
	String tuplesCompare(String tuple1, String tuple2, String pre, Statement st, ArrayList<String> colName) throws SQLException, ClassNotFoundException{
		Statement st2 = db_connection.getSt();
		ResultSet rs1= st.executeQuery(pre+" WHERE id =" +tuple1);
		ResultSet rs2= st2.executeQuery(pre+ " WHERE id ="+ tuple2);
		// temp store the name
		// value store the value
		ArrayList<String> temp = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();
		// find their longest match, generate the query to test
 		while(rs1.next()&&rs2.next()){
			for(String i: colName){
				if (rs1.getString(i).equals(rs2.getString(i))){ 
					temp.add(i); 
					// record the attribute name with same value
				    value.add(rs1.getString(i));
				}
			}
		}
 		
 		
 		boolean flag = false;
 		if(value.size()!=0){
 		// generate their compare query
		String compareSql = pre + "WHERE ";
		for (int j= 0;j <value.size()-1;j++){
		compareSql = compareSql + temp.get(j) + "=" + value.get(j) + " AND ";
		}
//		System.out.println("mark here: " + value.size() + " "+ temp.size());
		compareSql = compareSql + temp.get(value.size()-1) + "=" + value.get(value.size()-1) + " limit "+ Constant.k1;
		ResultSet rs3 = st.executeQuery(compareSql);
		Constant.total++;
		
		String temp1 = null;
		while(rs3.next()){
			// return the tuple name 
		String temp2 = rs3.getString("id");
		if(temp2.equals(tuple1)){
	//		System.out.println(temp2 + " rank higher between " + tuple1 + " "+ tuple2 );
			temp1 = tuple1;
			flag = true;
		break;
		}else if(temp2.equals(tuple2)){
	//		System.out.println(temp2 + " rank higher between " + tuple1 + " "+ tuple2 );
			temp1 = tuple2;
			flag = true;
			break;
		}
	}  
		st2.close();
		if(flag) return temp1;
		// if nothing return , means their incomparable directly
		else return "not comparable" ;
		
 		}else{
 			return "not comparable";
 		}
 		
	}
	
	
	public static String getTop_klimit() {
		return top_klimit;
	}


	public static void setTop_klimit(String top_klimit) {
		DAG.top_klimit = top_klimit;
	}


	public static String getLast_tuple() {
		return last_tuple;
	}


	public static void setLast_tuple(String last_tuple) {
		DAG.last_tuple = last_tuple;
	}


	public ArrayList<String> getNode() {
		return node;
	}


	public void setNode(ArrayList<String> node) {
		this.node = node;
	}


	public ArrayList<String> getKnown() {
		return known;
	}


	public void setKnown(ArrayList<String> known) {
		this.known = known;
	}


	public ArrayList<ArrayList<String>> getMultiChain() {
		return multiChain;
	}


	public void setMultiChain(ArrayList<ArrayList<String>> multiChain) {
		this.multiChain = multiChain;
	}


	public ArrayList<ArrayList<String>> getTemp_multiChain() {
		return temp_multiChain;
	}


	public void setTemp_multiChain(ArrayList<ArrayList<String>> temp_multiChain) {
		this.temp_multiChain = temp_multiChain;
	}


	public ArrayList<String> getColName() {
		return colName;
	}


	public void setColName(ArrayList<String> colName) {
		this.colName = colName;
	}


	public ArrayList<String> getCandidateSet() {
		return candidateSet;
	}


	public void setCandidateSet(ArrayList<String> candidateSet) {
		this.candidateSet = candidateSet;
	}


	public ArrayList<String> getTemp_candidate() {
		return temp_candidate;
	}


	public void setTemp_candidate(ArrayList<String> temp_candidate) {
		this.temp_candidate = temp_candidate;
	}


	public static void main(String[] args) throws ClassNotFoundException, SQLException{
	
	/*	   ArrayList<String> know=new ArrayList<String>();
	  	   know.add("t1");
	 	   know.add("t2");
	 	   know.add("t3");
	 	   know.add("t4");         
	 	   */
		
	/*	String pre = "SELECT * FROM database_project.running WHERE ";
		
		Statement st = AttributeSelection.dbconnect2();
		
		ArrayList<String> i = new ArrayList<String>();
		i.add("a1=0,a2=1");
		i.add("a1=1,a2=1");
		i.add("a1=0,a2=0");
		i.add("a1=1,a2=0");
		
		ArrayList<String> colName = new ArrayList<String>();
		
		colName.add("a1");
		colName.add("a2");
		colName.add("a3");
		colName.add("a4");
		colName.add("a5");
		
 	    DAG dag=new DAG(pre, st, i, colName);  */
 	   
 
 		} 
	
	
	
}