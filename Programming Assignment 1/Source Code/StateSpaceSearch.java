import java.util.LinkedHashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class StateSpaceSearch {
	/**
	 * @param tasks 
	 * @param args
	 * @return 
	 */
	//Depth Limited Search
	public static StateResult DLS(State beginState, Task[] tasks,int targetValue,int deadline,int depth){
		//number of states created in this searching
		int numberOfState = 1;
		//Find a solution in a selected depth
		while(depth>=0){
			for(int i=0;i<tasks.length;i++){
				if(beginState.ifCanAdd(tasks[i])){
					State tmpState = 
						new State(beginState.value,beginState.time,
								beginState.taskList,beginState.canBeAddedSucceeding);
					tmpState.add(tasks[i]);
					//created one state
					numberOfState++;
					//Find a solution, class Result is the result storing task list, value, time and the result status: cut off, have solution or not
					if(tmpState.time<=deadline&&tmpState.value>=targetValue){
						//0 is found the solution, numberOfState means states created from this state
						return new StateResult(tmpState.taskList,tmpState.value,tmpState.time,0,numberOfState);
					}
					//Not deciding whether it is a solution, continue searching recursively
					else if(tmpState.time<deadline&&tmpState.value<targetValue){
						StateResult result = DLS(tmpState,tasks,targetValue,deadline,depth-1);
						//Solution to be found
						if(result.haveOrNoHaveSolution!=1){
							int stateNumberInThisState=0;
							//states created from this layer and from those has been searched
							stateNumberInThisState+=result.numberOfStates;
							result.modifyNumberOfStates(stateNumberInThisState);
							return result;
						}
					}
					//No solution at this depth,created one state though
					else{
						return new StateResult(null,-1,-1,1,1);
					}
				}
			}
			//Can not expand the state any more though having not reach the depth, then no solution for sure
			return new StateResult(null,-1,-1,2,1);
		}
		//No solution at this depth, error code is 1
		return new StateResult(null,-1,-1,1,1);
	}
	//Iterative Deepening Searching
	public static StateResult IDS(State beginState, Task[] tasks,int targetValue,int deadline){
		//storing states created from every iterative search
		int numberOfStates = 1;
		for(int depth=1;depth<tasks.length;depth++){
			StateResult result = DLS(beginState,tasks,targetValue,deadline,depth);
			//every search creates some states
			numberOfStates+=result.numberOfStates;
			//Find a solution at some depth then return
			if(!result.solution.isEmpty()){
				result.modifyNumberOfStates(numberOfStates);
				return result;
			}
		}
		//did not find the solution after traversing all path
		return new StateResult(null,-1,-1,2,numberOfStates);
	}
	//BFS search the state
	public static StateResult BFS(State beginState, Task[] tasks,int maxSizeOfQueue,int targetValue,int deadline){
		Queue stateQueue = new Queue();
		stateQueue.enqueue(beginState);
		//enqueue
		while(!stateQueue.isEmpty()){
			State headState = stateQueue.dequeue();
			for(int i=0;i<tasks.length;i++){
				State tmpState = 
					new State(headState.value,headState.time,
						headState.taskList,headState.canBeAddedSucceeding);
				if(headState.ifCanAdd(tasks[i])){
					tmpState.add(tasks[i]);
					stateQueue.enqueue(tmpState);
				}
			}
//			System.out.println(stateQueue.size());
//			for(int i=0;i<stateQueue.size();i++){
//				System.out.print(stateQueue.getState(i).taskList.toString());
//			}
//			System.out.println("");
			if(stateQueue.size()==maxSizeOfQueue)	break;
		}
		int numberOfCreatedStates = 1;
		//while exceed max size of queue, Using IDS
		while(stateQueue.size()!=0){
			State stateUsingIDS = stateQueue.dequeue();	
			StateResult result = IDS(stateUsingIDS,tasks,targetValue,deadline);
			numberOfCreatedStates+=result.numberOfStates;
			//if find a solution rooted at one state
			if(result.haveOrNoHaveSolution==0){
				System.out.print(result.solution.toString()+"     ");
				System.out.print(result.value+"     ");
				System.out.print(result.time+"     ");
				System.out.println("");
				result.modifyNumberOfStates(numberOfCreatedStates);
				return result;
			}
		}
		//Do not find a solution
		System.out.println(0);
		return new StateResult(null,-1,-1,2,numberOfCreatedStates);
	}
	
	public static ExperienmentResult experiment(){
		// Defination of N,targetValue,deadline
		Random random =  new Random();
		int N = random.nextInt(5)+4;
		double sqrN = Math.sqrt(N);
		int lowerBound = (int) (N*N*(1-2/sqrN)/4);
		int upperBound = (int) (N*N*(1+2/sqrN)/4);
		int maxSizeOfQueue = 3;
		int targetValue = random.nextInt(upperBound-lowerBound)+lowerBound;
		int deadline = random.nextInt(upperBound-lowerBound)+lowerBound;
		//Randomly give value to task value and time, array stores the value and time of task[i]
		Integer taskID[] = new Integer[N];
		Integer taskValue[] = new Integer[N];
		Integer timeRequirement[] = new Integer[N];
		Integer havePreRequisite[] = new Integer[N];
		for(int i=0;i<N;i++){
			taskID[i] = i;
			taskValue[i] = random.nextInt(N);
			timeRequirement[i] = random.nextInt(N);
			havePreRequisite[i] = 0;
		}
		//Storing the succeeding state
		LinkedHashMap<Integer,ArrayList<Integer>> DAG = new LinkedHashMap<Integer,ArrayList<Integer>>();
		//Create the DAG, there's half the probablity that two states are depended
		for(int i=0;i<N;i++){
			List<Integer> succeeding = new ArrayList<Integer>();
			for(int j=i+1;j<N;j++){
				//50%
				int arcs = random.nextInt()%2;
				//if prerequisites
				if(arcs==1){
					//i is prerequisite of j
					havePreRequisite[j] = 1;
					succeeding.add(j);
				}
				//if i isn't prerequisite of j and the value have not been modified
				else	if(havePreRequisite[j]!=1){
					havePreRequisite[j] = 0;
				}
			}
			//task[i] and its succeeding
			DAG.put(i, new ArrayList<Integer>(succeeding));
		}
		//create and initialize the tasks and value
		Task tasks[] = new Task[taskID.length];
		State state = new State();
		//initialize tasks, set the property of class tasks have prerequisite
		for(int i=0;i<taskID.length;i++){
			tasks[i] = new Task(taskID[i],taskValue[i],timeRequirement[i],DAG.get(i));
			if(havePreRequisite[i]==0){
				tasks[i].havePreRequisite=0;
			}
			else{
				tasks[i].havePreRequisite=1;
			}
		}
		//print the result
		System.out.println("deadline is "+deadline);
		System.out.println("value is "+targetValue);
		System.out.println("taskID   taskvalue   tasktime   tasksucceeding");
		for(int i=0;i<tasks.length;i++){
			System.out.print(tasks[i].TaskId+"        "+tasks[i].Value+"           "+tasks[i].TimeRequirement+"          "+
					tasks[i].Succeeding.toString()+"         "+tasks[i].havePreRequisite);
			System.out.println();
		}
		System.out.println("Solution, time and value:");
		//BFS is the entrance of the initial state null
		StateResult result = BFS(state,tasks,maxSizeOfQueue,targetValue,deadline);
		System.out.println("States created: "+result.numberOfStates);
		if(result.haveOrNoHaveSolution==2){
			return new ExperienmentResult((double) 0,result.numberOfStates);
		}
		else{
			return new ExperienmentResult((double) 1,result.numberOfStates);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double amount = 0;
		System.out.println("The task number N is defined ranging from 4 to 9");
		System.out.println("Thus I define the max state to be 10! as the initial condition though it cannot in fact exceed this number");
		int minStates = 3628800 ;
		int maxStates = 0;
		//Do the experienment 1000 times
		for(int i=0;i<1000;i++){
			ExperienmentResult experienmentResult = experiment();
			amount += experienmentResult.ifHasSolution;
			if(experienmentResult.numberOfCreatedStates<minStates)
				minStates = experienmentResult.numberOfCreatedStates;
			if(experienmentResult.numberOfCreatedStates>maxStates)
				maxStates = experienmentResult.numberOfCreatedStates;
		}
		System.out.println("");
		System.out.println("Fraction of have solution is: "+ (double)amount/1000);
		System.out.println("The max states created is "+maxStates);
		System.out.println("The min states created is "+ minStates);
		System.out.println("");
		}
}
