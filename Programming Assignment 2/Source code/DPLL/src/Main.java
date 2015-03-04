import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("DPLL begins! The program is reading file from the ./src/files directories");
		BufferedReader br = new BufferedReader(new InputStreamReader(   
	              new FileInputStream("./src/file/FrontEndOutput.txt")));
				  //new FileInputStream("./src/file/DPLL Test File.txt")));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./src/file/DPLLOutput.txt")));
		int clauseFlag = 0;
		int symbolNum = 0;
		ArrayList<Clause> clauses = new ArrayList<Clause>();
		List<Integer> ATOMS = new ArrayList<Integer>();
		Map<String,Integer> atomToNum = new LinkedHashMap<String,Integer>();
		Map<Integer,String> numToAtom = new TreeMap<Integer,String>();
		
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			if(line.equals("0")){
				clauseFlag = 1;
			}
			else{
				if(clauseFlag == 0){
					Clause clause = new Clause();
					String tmpClause[] = line.split("\\s+");
					Integer numClause[] = new Integer[tmpClause.length];
					for(int i=0;i<tmpClause.length;i++){
						numClause[i] = Integer.parseInt(tmpClause[i]);
						clause.add(numClause[i]);
						if(!ATOMS.contains(numClause[i]))	ATOMS.add(numClause[i]);
					}
					clauses.add(clause);
				}
				else{
					String symbolTable[] = line.split("\\s+");
					//System.out.println(line);
					numToAtom.put(Integer.parseInt(symbolTable[0]),symbolTable[1]);
					atomToNum.put(symbolTable[1],Integer.parseInt(symbolTable[0]));
					symbolNum++;
				}
			}
		}
		//clause is constructed by the series of clauses that has already been done
		ClausesSet Clauses = new ClausesSet(clauses);
		
		Integer[] asb = ATOMS.toArray(new Integer[0]);
		for(int i=0;i<asb.length;i++){
			if(asb[i]<0&&ATOMS.contains(-asb[i])) ATOMS.remove(asb[i]);
			else if(asb[i]<0&&!ATOMS.contains(-asb[i])){
				ATOMS.remove(asb[i]);
				ATOMS.add(-asb[i]);
			}
		}
		
		Map<Integer,Integer> nullValuation = new LinkedHashMap<Integer,Integer>();	
		for(int i=0;i<ATOMS.size();i++){
			nullValuation.put(ATOMS.get(i), 0);
		}
		//Write into file
		//System.out.println(ATOMS.toString());
		new DPLL();
		Map<Integer,Integer> result = new TreeMap<Integer,Integer>();
		result = DPLL.DP(ATOMS,Clauses);
		if(result.isEmpty()){
			System.out.println("Error!");
		}
		//traverse the result table, and split it into two parts, then the first is the atom and the second is value
		else{
			Collection resultEntry = result.entrySet();
			for(Iterator iter = resultEntry.iterator(); iter.hasNext();){
				Object obj = iter.next();
				String splittedResult[] = obj.toString().split("=");
				int resultAtom = Integer.parseInt(splittedResult[0]);
				int resultValue = Integer.parseInt(splittedResult[1]);
				bw.write(numToAtom.get(resultAtom)+"  ");
				if(resultValue > 0){
					bw.write("T");
					bw.write("\r\n");
				}
				else{
					bw.write("F");
					bw.write("\r\n");
				}
			}
		}
		bw.write("0");
		bw.write("\r\n");
		//Write atoms and its number into file
		Collection entry = numToAtom.entrySet();
		for(Iterator iterator = entry.iterator();iterator.hasNext();){
			Object obj = iterator.next();
			String splitted[] = obj.toString().split("=");
			bw.write(splitted[0]);
			bw.write(" ");
			bw.write(splitted[1]);
			bw.write("\r\n");
		}
		bw.close();	
	}

}
