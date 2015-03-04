import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class DPLL {
	Map<Integer,Integer> solution = new TreeMap<Integer,Integer>();
	
	public static Map<Integer,Integer> DP(List<Integer> aTOMS, ClausesSet clauseInCNF){
		Map<Integer,Integer> valuation = new LinkedHashMap<Integer,Integer>();
		ArrayList<Integer> pATOMS = new ArrayList<Integer>();
		for(int i=0;i<aTOMS.size();i++){
			valuation.put(aTOMS.get(i), 0);
			pATOMS.add(aTOMS.get(i));
		}
		ClausesSet pClausesSet = clauseInCNF.deepCopy();
		return DP1(pATOMS,pClausesSet,valuation);
	}
	
	public static Map<Integer,Integer> DP1(ArrayList<Integer> ATOMS1, ClausesSet S1, Map<Integer, Integer> V1){
		ArrayList<Integer> ATOMS = (ArrayList<Integer>) ATOMS1.clone();
		ClausesSet S = S1.deepCopy();
		Map<Integer, Integer> V = new LinkedHashMap<Integer,Integer>();
		Collection entry = V1.entrySet();
		for(Iterator iter = entry.iterator();iter.hasNext();){
			Object obj=iter.next();
			String mapItem[] = obj.toString().split("=");
			int key = Integer.parseInt(mapItem[0]);
			int value = Integer.parseInt(mapItem[1]);
			V.put(key, value);
		}
		System.out.println("New Recursive-----------------------------------------------------------------------------------------------------------------");
		//System.out.println(V.toString());
		//S1.print();
		/* BASE OF THE RECURSION: SUCCESS OR FAILURE */
		while(true){
			//System.out.println("New Loop--------------------------------------------------"+i++);
			//System.out.println(V.toString());
			//System.out.println("S.literalList.toString()"+S.literalList.toString());
			//S.print();
			if(S.clauseSet.size()==0){
				System.out.println("True");
				for(int A : ATOMS){
					//System.out.println(A+"              "+V.get(A)+"   ");
					if(V.get(A) == 0){
						V.put(A, 1);
					}
					
				}
				return V;
			}
			else if(S.someClauseIsEmpty()){
				System.out.println("FAIL"+V.toString());
				Map<Integer,Integer> nullResult = new LinkedHashMap<Integer,Integer>();
				//S.print();
				return nullResult;
			}
			
			/* EASY CASES: PURE LITERAL ELIMINATION AND FORCED ASSIGNMENT */			
			else if(S.containsPureLiteral()){			/* Pure literal elimination */
				int pureLiteral = S.getPureLiteral();
				System.out.println("pureLiteral case: "+pureLiteral);
				//System.out.println("pureLiteral: "+pureLiteral);
				S.remove(pureLiteral);
				V.put(pureLiteral, 1);
				if(V.containsKey(-pureLiteral)){
					if(pureLiteral>0){
						V.remove(-pureLiteral);
					}
					else{
						V.put(-pureLiteral, -1);
						V.remove(pureLiteral);
					}
				}
				Clause tmp[] = S.clauseSet.toArray(new Clause[0]);
				for(int j=0;j<tmp.length;j++){
					if(tmp[j].clause.contains(pureLiteral)){
						System.out.println("under Pure Literal removed clause:"+tmp[j].clause);
						//S.clauseSet.remove(tmp[j]);
						S.removeClause(tmp[j]);
					}
				}
				//S.print();
			}
			else if(S.containsUnitClause()){			/* Forced assignment */
				int atom = S.getUnitClause();
				S.remove(atom);
				
				System.out.println("unit clause case: "+atom);
				//System.out.println("unitClause:"+unitClause);
				if(atom>0){
					V.put(atom, 1);
					if(V.containsKey(-atom))	V.remove(-atom);
					S.propagate(atom,V);
				}
				else{
					if(V.containsKey(-atom))	V.put(-atom, -1);
					S.propagate(-atom,V);
				}
				//S.print();
			}
			else break;										/* No easy cases found */
		}/* End LOOP */

		System.out.println("Contains none of these condidtions,End Loop!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		//System.out.println("S.literalList.toString()"+S.literalList.toString());
		//S.print();
		/* PICK SOME ATOM AND TRY EACH ASSIGNMENT IN TURN */
		int a = 0;
		int b = 0;
		Collection vEntrySet = V.entrySet();
		for(Iterator iter = vEntrySet.iterator();iter.hasNext();){
			Object obj = iter.next();
			String split[] = obj.toString().split("=");
			a = Integer.parseInt(split[0]);
			b = Integer.parseInt(split[1]);
			if(b == 0) break;
		}
		//b=0 then a is unbounded
		V.put(a, 1);
		System.out.println("choose unbounded atom: "+a);
		ClausesSet SCopy = S.deepCopy();
		SCopy.propagate(a, V);
		SCopy.remove(a);
		Map<Integer,Integer> VNEW = DP1(ATOMS,SCopy,V);
		System.out.println("VNEW: "+VNEW.toString());
		//System.out.println(VNEW == null);
		if(!VNEW.isEmpty()){
			return VNEW;
		}
		/* IF V[A] := TRUE didn't work, try V[A} := FALSE; */
		else{
			System.out.println("V:+ "+ V.toString());
			V.put(a, -1);
			S.propagate(a, V);
			return DP1(ATOMS,S,V);
		}
	}
	
}
