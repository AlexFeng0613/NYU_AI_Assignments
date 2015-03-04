import java.util.ArrayList;


public class Clause {
	
	ArrayList<Integer> clause = new ArrayList<Integer>();
	public Clause(){
		
	}
	
	public Clause(ArrayList<Integer> atomsList){
		clause.addAll(atomsList);
	}
	
	public Boolean isEmpty(){
		if(clause.isEmpty())	return true;
		else return false;
	}
		
	public void add(int addSymbol){
		clause.add(addSymbol);
	}	
	
	public void remove(int i){
		Integer[] tmp = clause.toArray(new Integer[0]);
		for(int j=0;j<tmp.length;j++){
			if(tmp[j] == i)		clause.remove(tmp[j]);
		}
	}
	
	public Boolean contains(int symbol){
		if(clause.contains(symbol))	return true;
		else return false;
	}
	
	public Boolean isUnitClause(){
		if(clause.size() == 1) return true;
		else return false;
	}
}
