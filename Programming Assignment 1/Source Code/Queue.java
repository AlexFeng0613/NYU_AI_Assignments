import java.util.ArrayList;
import java.util.List;
//state queue storing states, operations are as general queue
public class Queue {
	List<State> StateList = new ArrayList<State>();
	int queuesize = 0;
	
	public State getState(int i){
		return StateList.get(i);
	}
	
	public void enqueue(State state){
		StateList.add(state);
		queuesize++;
	}
	
	public State dequeue(){
		if(queuesize == 0){
			return null;
		}
		else{
			queuesize = queuesize-1;
			State newState = StateList.get(0);
			StateList.remove(0);
			return newState;
		}
	}
	
	public Boolean isEmpty(){
		if(queuesize==0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public int size(){
		return queuesize;
	}
}
