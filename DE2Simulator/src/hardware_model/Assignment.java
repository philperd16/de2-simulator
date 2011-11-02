package hardware_model;

import hardware_model.Instruction;

public class Assignment implements Instruction {

	String variableName;
	Operation operation;
	
	public String getAssigningVariable() {
		return variableName;
	}

	public Operation getAssignedOperation() {
		return operation;
	}

	public void setAssigningVariable(String assigningVariable) {
		variableName = assigningVariable;
	}

	public void setAssignedOperation(Operation assignedOperation) {
		operation = assignedOperation;
	}
	
	@Override
	public String toString(){
		return variableName + " <= " + operation;
	}

}