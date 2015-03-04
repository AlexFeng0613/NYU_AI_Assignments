import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileReader fr = new FileReader("./src/file/Maze.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./src/file/FrontEndOutput.txt")));
		
		Scanner scanner = new Scanner(fr);
		String nodeList = scanner.nextLine();
		String nodes[] = nodeList.split("\\s+");
		
		String treasureList = scanner.nextLine();
		String treasures[] = treasureList.split("\\s+");
		
		int allowedSteps = Integer.parseInt(scanner.nextLine());
		//stores the node and its next node, key is this node and value is a list of next nodes, toll is splitted by space
		Map<String,String> adjacentList = new LinkedHashMap<String,String>();
		//stores the node and its toll, key is this node and value is a list of tolls, toll is splitted by space
		Map<String,String> tollNode = new LinkedHashMap<String,String>();
		//stores the node and its corresponding node, key is this node and value is a list of treasure, treasure is splitted by space
		Map<String,String> treasureNode = new LinkedHashMap<String,String>();
		//process each line
		for(int i=0;i<nodes.length;i++){
			String line = scanner.nextLine();
			//split one line into four parts, as the following words show
			String splitLine[] = line.split("TREASURES|TOLLS|NEXT");
			for(int j=0;j<splitLine.length;j++){
				String tmpNode = null;
				//first part: node
				if(j==0){
					tmpNode = splitLine[j];
					System.out.print("Current node:");
					System.out.print(splitLine[j]);
					System.out.print("    ");
				}
				//second part: treasure
				else if(j ==1){
					System.out.print("Treasures:");
					if(!splitLine[j].equals(" ")){
						System.out.print(splitLine[j]);
						String treasureInNode = "";
						String split[] = splitLine[j].trim().split(" ");
						for(int k=0;k<split.length;k++){
							treasureInNode = treasureInNode + split[k] + " ";
						}
						treasureNode.put(splitLine[0].trim(), treasureInNode);
					}
					else{
						System.out.print("No treasure at this node;    ");
					}
				}
				else if(j ==2){
					System.out.print("Tolls:");
					if(!splitLine[j].equals(" ")){
						String tollInNode = "";
						System.out.print(splitLine[j]);
						String split[] = splitLine[j].trim().split(" ");
						for(int k=0;k<split.length;k++){
							tollInNode = tollInNode + split[k] + " ";
						}
						tollNode.put(splitLine[0].trim(),tollInNode);
					}
					else{
						System.out.print("No tolls at this node;    ");
					}
				}
				else{
					System.out.print("Next:");
					System.out.print(splitLine[j]);
					if(!splitLine[j].equals(" ")){
						String adjacentNode = "";
						String split[] = splitLine[j].trim().split(" ");
						for(int k=0;k<split.length;k++){
							adjacentNode = adjacentNode + split[k] + " ";
						}
						adjacentList.put(splitLine[0].trim(), adjacentNode);
					}
					else{
						System.out.print("No next point at this node;    ");
					}
				}
			}
			System.out.println("");
		}
		//System.out.println("tollNode.toString()"+tollNode.toString());
		//System.out.println("treasureNode.toString()"+treasureNode.toString());
		//Add symbols to atoms map, key is integer represents atoms and value is the atom
		Map<Integer,String> symbolNumberToAtoms = new LinkedHashMap<Integer,String>();
		//Add symbols to atoms map, key is atoms and value is the integer
		Map<String,Integer> atomTosymbolNumbers = new LinkedHashMap<String,Integer>();
		//the number symbols a list of nodes
		int atomNum = 1;
		for(int i=0;i<allowedSteps+1;i++){
			//At(START,0) forms, see the whole string as a atom
			for(int j=0;j<nodes.length;j++){
				String tmp = "At("+nodes[j]+","+i+")";
				symbolNumberToAtoms.put(atomNum, tmp);
				atomTosymbolNumbers.put(tmp, atomNum);
				atomNum++;
			}
			//Has(GOLD,0) forms, see the whole string as a atom
			for(int j=0;j<treasures.length;j++){
				String tmp = "Has("+treasures[j]+","+i+")";
				symbolNumberToAtoms.put(atomNum, tmp);
				atomTosymbolNumbers.put(tmp, atomNum);
				atomNum++;
			}
			//AVvailable(GOLD,0) forms, see the whole string as a atom
			for(int j=0;j<treasures.length;j++){
				String tmp = "Available("+treasures[j]+","+i+")";
				symbolNumberToAtoms.put(atomNum, tmp);
				atomTosymbolNumbers.put(tmp, atomNum);
				atomNum++;
			}
		}
		/*Generates Proposistions*/
		//Proposition 1: can not at 2 nodes
		for(int i=0;i<=allowedSteps;i++){
			for(int j=0;j<nodes.length;j++){
				for(int t=j+1;t<nodes.length;t++){
					String a = "At("+nodes[j]+","+i+")";
					String b = "At("+nodes[t]+","+i+")";
					String clause = (-atomTosymbolNumbers.get(a))+" "+(-atomTosymbolNumbers.get(b));
					bw.write(clause);
					bw.write("\r\n");
				}
			}
		}
		//Proposition 2: treasure T at time I then T is not available at time I
		for(int i=0;i<allowedSteps+1;i++){
			for(int j=0;j<treasures.length;j++){
				String a = "Has("+treasures[j]+","+i+")";
				String b = "Available("+treasures[j]+","+i+")";
				String clause = (-atomTosymbolNumbers.get(a))+" "+(-atomTosymbolNumbers.get(b));
				bw.write(clause);
				bw.write("\r\n");
			}
		}
		//Proposition 3: adjacent list
		for(int i=0;i<allowedSteps;i++){
			for(int j=0;j<nodes.length;j++){
				String tmp = adjacentList.get(nodes[j].toString());
				//System.out.println(nodes[j]);
				//System.out.println(tmp);
				String adjacentNodes[] = tmp.split(" ");
				String a = "At("+nodes[j]+","+i+")";
				String clause = "";
				int literal = -atomTosymbolNumbers.get(a);
				clause+=literal;
				clause+=" ";
				//System.out.println(a);
				for(int t=0;t<adjacentNodes.length;t++){
					String b = "At("+adjacentNodes[t]+","+(i+1)+")";
					int tmpLiteral = atomTosymbolNumbers.get(b);
					clause = clause + tmpLiteral +" ";
				}
				bw.write(clause);
				bw.write("\r\n");
			}
		}

		//Proposition 4: N has toll T and the player is at N at time I+1, then the player must have T at time I. 
		for(int i=0;i<allowedSteps;i++){
			//Proposition 4: N has toll T and the player is at N at time I+1, then the player must have T at time I. 
			for(int j=0;j<nodes.length;j++){
				if(tollNode.containsKey(nodes[j])){
					for(int t=0;t<treasures.length;t++){
						String a = "At("+nodes[j]+","+(i+1)+")";
						String b = "Has("+treasures[t]+","+i+")";
						String clause = (-atomTosymbolNumbers.get(a))+" "+(atomTosymbolNumbers.get(b));
						bw.write(clause);
						bw.write("\r\n");
					}
				}
			}
		}
		//Proposition 5:treasure T is initially at node N and is available at time I and the player
			//is at N at time I+1, then at time I+1 the player has T
		for(int i=0;i<allowedSteps;i++){
			for(int j=0;j<nodes.length;j++){
				for(int t=0;t<treasures.length;t++){
					if(treasureNode.containsKey(nodes[j])){
						if(treasureNode.get(nodes[j]).contains(treasures[t])){
							String a = "Available("+treasures[t]+","+i+")";
							String b = "At("+nodes[j]+","+(i+1)+")";
							String c = "Has("+treasures[t]+","+(i+1)+")";
							String clause = (-atomTosymbolNumbers.get(a))+" "+(-atomTosymbolNumbers.get(b))+" "+(atomTosymbolNumbers.get(c));
							bw.write(clause);
							bw.write("\r\n");
						}
					}
				}
			}
		}
		//Proposition 6: If node N has toll T and the player is at N at time I, then the player no longer has T at time I. 
		for(int i=0;i<allowedSteps+1;i++){
			for(int j=0;j<nodes.length;j++){
				if(tollNode.containsKey(nodes[j])){
					for(int t=0;t<treasures.length;t++){
						String a = "At("+nodes[j]+","+i+")";
						String b = "Has("+treasures[t]+","+i+")";
						String clause = (-atomTosymbolNumbers.get(a))+" "+(-atomTosymbolNumbers.get(b));
						bw.write(clause);
						bw.write("\r\n");
					}
				}
			}
		}

		//Proposition 7:T is available at I, and the player is at node N which is not the 
			//home of T at I+1, then T is available at I+1. 
		for(int i=0;i<allowedSteps;i++){
			//Proposition 7:T is available at I, and the player is at node N which is not the 
			//home of T at I+1, then T is available at I+1. 
			for(int j=0;j<nodes.length;j++){
				for(int t=0;t<treasures.length;t++){
					//Forgot if this point is toll
					//if(treasureNode.containsKey(nodes[j]) && treasureNode.get(nodes[j]).contains(treasures[t])){}
					if(treasureNode.containsKey(nodes[j]) && treasureNode.get(nodes[j]).contains(treasures[t])){}
					else{
						String a = "Available("+treasures[t]+","+i+")";
						String b = "At("+nodes[j]+","+(i+1)+")";
						String c = "Available("+treasures[t]+","+(i+1)+")";
						String clause = (-atomTosymbolNumbers.get(a))+" "+(-atomTosymbolNumbers.get(b))+" "+(atomTosymbolNumbers.get(c));
						bw.write(clause);
						bw.write("\r\n");
					}
				}
			}
		}

		//Proposition 8:treasure T is not available at time I, then it is not available at time I+1
		for(int i=0;i<allowedSteps;i++){
			//Proposition 8:treasure T is not available at time I, then it is not available at time I+1
			for(int j=0;j<treasures.length;j++){
				String a = "Available("+treasures[j]+","+i+")";
				String b = "Available("+treasures[j]+","+(i+1)+")";
				String clause = (atomTosymbolNumbers.get(a))+" "+(-atomTosymbolNumbers.get(b));
				bw.write(clause);
				bw.write("\r\n");
			}
		}

		//Proposition 9:T has been spend at time I, then the player does not have it at time I+1
		for(int i=0;i<allowedSteps;i++){
			//Proposition 9:T has been spend at time I, then the player does not have it at time I+1
			for(int j=0;j<treasures.length;j++){
				String a = "Available("+treasures[j]+","+i+")";
				String b = "Has("+treasures[j]+","+i+")";
				String c = "Has("+treasures[j]+","+(i+1)+")";
				String clause = (atomTosymbolNumbers.get(a))+" "+(atomTosymbolNumbers.get(b)+" "+(-atomTosymbolNumbers.get(c)));
				bw.write(clause);
				bw.write("\r\n");
			}
		}

		//Proposition 10:player has treasure T at time I and is at 
			//node N at time I+1, and N does not require T as a toll, then the player still has T at I+1. 
		for(int i=0;i<allowedSteps;i++){
			for(int j=0;j<nodes.length;j++){
				if(!tollNode.containsKey(nodes[j])){
					for(int t=0;t<treasures.length;t++){
						String a = "Has("+treasures[t]+","+i+")";
						String b = "At("+nodes[j]+","+(i+1)+")";
						String c = "Has("+treasures[t]+","+(i+1)+")";
						String clause = (-atomTosymbolNumbers.get(a))+" "+(-atomTosymbolNumbers.get(b)+" "+(atomTosymbolNumbers.get(c)));
						bw.write(clause);
						bw.write("\r\n");
					}
				}
			}
		}
		

		//add start, goal and available 0
		String start = "At(START,0)";
		String goal = "At(GOAL,"+allowedSteps+")";
		bw.write(atomTosymbolNumbers.get(start).toString());
		bw.write("\r\n");
		bw.write(atomTosymbolNumbers.get(goal).toString());
		bw.write("\r\n");
		for(int i=0;i<treasures.length;i++){
			String a = "Available("+treasures[i]+","+0+")";
			bw.write(atomTosymbolNumbers.get(a).toString());
			bw.write("\r\n");
		}
		
		bw.write("0");
		bw.write("\r\n");
		for(Iterator<Integer> iter = symbolNumberToAtoms.keySet().iterator();iter.hasNext();){
			Object obj = iter.next();
			//System.out.print(obj.toString()+" ");
			//System.out.println(atomMap.get(Integer.parseInt(obj.toString())));
			bw.write(obj.toString()+" ");
			bw.write(symbolNumberToAtoms.get(Integer.parseInt(obj.toString())));
			bw.write("\r\n");
		}
		bw.close();
	}

}
