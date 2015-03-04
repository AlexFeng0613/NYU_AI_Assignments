import java.util.ArrayList;
import java.util.List;

public class StateResult {
	List<Integer> solution = new ArrayList<Integer>();
	int value;
	int time;
	int haveOrNoHaveSolution;
	int numberOfStates;
	public StateResult(List<Integer> solution, int val, int tim, int haveOrNoSolution,int numberOfStates){
		if(solution!=null)
			this.solution.addAll(solution);
		this.value=val;
		this.time=tim;
		this.haveOrNoHaveSolution=haveOrNoSolution;
		this.numberOfStates = numberOfStates;
	}
	
	public void modifyNumberOfStates(int num){
		this.numberOfStates = num;
	}
	
	public StateResult(){}
	
	public void set(int a){
		haveOrNoHaveSolution = a;
	}
}