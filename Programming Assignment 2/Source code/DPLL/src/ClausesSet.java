import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ClausesSet {
	ArrayList<Clause> clauseSet = new ArrayList<Clause>();
	Set<Integer> literalList = new HashSet<Integer>();
	public ClausesSet(){
		
	}
	
	public void remove(int literal){
		literalList.remove(literal);
		literalList.remove(-literal);
	}
	
	public void removeClause(Clause clause){
		clauseSet.remove(clause);
	}
	
	public Boolean someClauseIsEmpty(){
		Boolean flag = false;
		for(int i=0;i<clauseSet.size();i++){
			if(clauseSet.get(i).clause.size() == 0){
				//System.out.println(clauseSet.get(i).clause.toString());
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	public ClausesSet(ArrayList<Clause> ClauseSet){
		clauseSet.addAll(ClauseSet);
		for(int i=0;i<clauseSet.size();i++){
			literalList.addAll(clauseSet.get(i).clause);
		}
	}
	//print out this clause set
	public void print(){
		for(int i=0;i<clauseSet.size();i++){
		System.out.println(clauseSet.get(i).clause.toString());
		}
	}
	/*deep copy*/
	public ClausesSet deepCopy(){
		Set<Integer> tmpClausesLiteral = new HashSet<Integer>();
		ArrayList<Clause> tmpClausesSet = new ArrayList<Clause>();
		//copy all clauses
		for(int i=0;i<clauseSet.size();i++){
			ArrayList<Integer> tmp = new ArrayList<Integer>();
			Clause tmpClause =clauseSet.get(i);
			for(int j=0;j<tmpClause.clause.size();j++){
				int a = tmpClause.clause.get(j);
				tmp.add(a);
			}
			Clause newClause = new Clause(tmp);
			//newClause.clauseResult = tmpClause.clauseResult;
			tmpClausesSet.add(newClause);
		}
		//copy the atoms contained in this clause set
		for(Iterator iterator = literalList.iterator();iterator.hasNext();){
			Object obj = iterator.next();
			int a = Integer.parseInt(obj.toString());
			tmpClausesLiteral.add(a);
		}
		//return new class
		ClausesSet returnClass = new ClausesSet(tmpClausesSet) ;
		returnClass.literalList = tmpClausesLiteral;
		return returnClass;
	}
	//propagate
	public void propagate(int A, Map<Integer, Integer> V){
		Clause C[] = clauseSet.toArray(new Clause[0]);
		//literalList.remove(A);
		//System.out.println("in function, A is: "+ A);
		//System.out.println("in function, V is: "+ V.toString());
		for(int i=0;i<C.length;i++){
			//If one clause contains the atom A and its value is true, delete clause
			if((C[i].contains(A) && V.get(A)>0) || C[i].contains(-A) && V.get(A)<0){
				//System.out.println("Propagate clause removed:"+C[i].clause.toString());
				clauseSet.remove(C[i]);
			}
			//If one clause contains the atom A and its value is true, delete this atom, but need to think whether this is atom form or ~atom form
			else if(C[i].contains(A) && V.get(A)<0){
				//System.out.println("Propagate clause modified:"+C[i].clause);
				C[i].remove(A);
			}
			else if(C[i].contains(-A) && V.get(A)>0){
				//System.out.println("Propagate clause modified:"+C[i].clause);
				C[i].remove(-A);
			}
		}
	}	
	
	public Boolean containsUnitClause(){
		Boolean result = false;
		for(int i=0;i<clauseSet.size();i++){
			if(clauseSet.get(i).isUnitClause()){
				result = true;
				break;
			}
		}
		return result;
	}
	
	public int getUnitClause(){
		int result = 0;
		for(int i=0;i<clauseSet.size();i++){
			if(clauseSet.get(i).isUnitClause()){
				result = clauseSet.get(i).clause.get(0);
				break;
			}
		}
		return result;
	}
	//if find one pure literal then stop loop and return
	public Boolean containsPureLiteral(){
		Boolean result = false;
		Integer[] tmp = literalList.toArray(new Integer[0]);
		for(int i=0;i<tmp.length;i++){
			if(literalList.contains(tmp[i])&&!literalList.contains(-tmp[i])){
				//System.out.println(tmp[i]);
				result = true;
				break;
			}
		}
		//System.out.println(result);
		return result;
	}

	public int getPureLiteral(){
		int result = 0;
		Integer[] tmp = literalList.toArray(new Integer[0]);
		for(int i=0;i<tmp.length;i++){
			
			if(literalList.contains(tmp[i])&&!literalList.contains(-tmp[i])){
				result = tmp[i];
				break;
			}
		}
		return result;
	}
}