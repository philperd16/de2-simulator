package simulator;

import hardware_model.Condition;
import hardware_model.Variable;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

public class ConditionalThread extends InstructionThread {

	Set<InstructionThread> instructions;
	Condition condition;
	Timer instructionsController;
	
	public ConditionalThread(Condition condition, Set<Variable> environmentVariables) {
		super(environmentVariables);
		this.condition = condition;
		this.instructions = new HashSet<InstructionThread>();
		this.instructionsController = new Timer();
	}
	
	public void add(InstructionThread compositeInstruction){
		this.instructions.add(compositeInstruction);
	}

	@Override
	public void run() {
		if ( condition == null ? true : testCondition(condition) ){
			for ( InstructionThread thread : instructions ){
				instructionsController.schedule(thread, 0);
			}
		}
	}

	private boolean testCondition(Condition condition) {
		return calculateResult(condition) == condition.getExpectedValue();
	}

	private Object calculateResult(Condition condition) {
		return calculateResult(condition.getTest());
	}

}
