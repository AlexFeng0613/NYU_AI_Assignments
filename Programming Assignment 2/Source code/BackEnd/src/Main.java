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
import java.util.Map;
import java.util.TreeMap;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Read file from ./src/file/BackEndInput.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(   
	            new FileInputStream("./src/file/BackEndInput.txt")));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./src/file/BackEndOutput.txt")));
			ArrayList<String> solution = new ArrayList<String>();
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				//after this line are the atoms and corresponding literature
				if(line.equals("0")){
					break;
				}
				else{
					//if this line is the position of the player
					if(line.contains("At")){
						//System.out.println(line);
						String result[] = line.split("\\s+");
						if(result[1].contains("T")){
							String split[] = line.split("\\s+");
							solution.add(split[0]);
						}
					}
				}
			}
			bw.write("Solution is:");
			bw.write("\r\n");
			if(!solution.isEmpty()){
				Map<Integer,String> path = new TreeMap<Integer,String>();
				//Put solution into a solution map, for string at(start, 0) just take out the node "Start" and the step num 0
				for(int i=0;i<solution.size();i++){
					String stateAtThisStep = solution.get(i);
					String solutionItem[] = stateAtThisStep.split(",");
					String stepNum = solutionItem[1].replace(")", "");
					String node = solutionItem[0].replace("At(", "");
					int step = Integer.parseInt(stepNum);
					path.put(step, node);
				}
				//write the map into the file
				Collection solutionEntry = path.entrySet();
				for(Iterator iter = solutionEntry.iterator();iter.hasNext();){
					Object obj = iter.next();
					String keyAndValue[] = obj.toString().split("=");
					bw.write("At step "+keyAndValue[0]+" at node :"+keyAndValue[1]);
					bw.write("\r\n");
				}
			}
			else{
				bw.write("No Solution");
			}
			bw.close();
			System.out.println("Write data back into  ./src/file/BackEndOutput.txt");
	}

}
