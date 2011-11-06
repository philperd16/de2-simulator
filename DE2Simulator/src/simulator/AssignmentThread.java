package simulator;

import hardware_model.Assignment;
import hardware_model.Variable;

import java.util.Set;

public class AssignmentThread extends InstructionThread{

	Assignment instruction;
	
	public AssignmentThread(Assignment instruction,	Set<Variable> environmentVariables){
		super(environmentVariables);
		this.instruction = instruction;
	}

	@Override
	public void run() {
		int result = calculateResult(this.instruction.getAssignedOperation());
		this.instruction.getAssigningVariable().setValue(result);
	}

}
