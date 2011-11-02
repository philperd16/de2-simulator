package hardware_model;

import hardware_model.Instruction;

public class Operation implements Instruction{

	String operation;
	
	public Operation(String assignedOperation) {
		operation = assignedOperation;
	}
	
	@Override
	public String toString(){
		return operation;
	}

}
