import java.util.ArrayList;
import java.util.List;

public class Task {
	
	int TaskId;
	int Value;
	int TimeRequirement;
	//Storing the succeed task of this one
	List<Integer> Succeeding = new ArrayList<Integer>();
	//If this task has a prerequisite task,1 means has one and 0 means has no
	int havePreRequisite;
	public Task(int taskid, int value, int timerequirement,List<Integer> succeeding){
		TaskId = taskid;
		Value = value;
		TimeRequirement = timerequirement;
		if(!succeeding.isEmpty())
			this.Succeeding.addAll(succeeding);
	}
}