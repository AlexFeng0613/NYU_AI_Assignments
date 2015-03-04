import java.util.List;
import java.util.ArrayList;

public class State {
	int value;
	int time;
	List<Integer> taskList = new ArrayList<Integer>();
	List<Integer> canBeAddedSucceeding = new ArrayList<Integer>();
	
	public State(){}
	
	public State(int value,int time, List<Integer> taskList, List<Integer> canBeAddedSucceeding){
		this.value=value;
		this.time=time;
		this.taskList.addAll(taskList);
		this.canBeAddedSucceeding.addAll(canBeAddedSucceeding);
	}
	
	public int getValue(){
		return value;
	}
	
	public int time(){
		return time;
	}
	//if a task(1) has no prerequisite (2) its prerequisites has been in the state (3) has not been added to the state then can be added
	public Boolean ifCanAdd(Task task){
		if(((task.havePreRequisite==0)||(canBeAddedSucceeding.contains(task.TaskId)))&&(!taskList.contains(task.TaskId))){
			return true;
		}
		else
			return false;
	}
	//add a task to this state and change its value, time, arraylist, etc.
	public void add(Task task){
		if((task.havePreRequisite==0)||(canBeAddedSucceeding.contains(task.TaskId)) ){
			taskList.add(task.TaskId);
			value = value+task.Value;
			time = time+task.TimeRequirement;
			canBeAddedSucceeding.addAll(task.Succeeding);
		}
	}
}
