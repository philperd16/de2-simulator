package hardware_model;

import hardware_model.operation.Operation;

public class Assignment implements Instruction {

	Variable assigningVariable;
	Operation operation;
	
	public Variable getAssigningVariable() {
		return assigningVariable;
	}

	public Operation getAssignedOperation() {
		return operation;
	}

	public void setAssigningVariable(Variable variable) {
		assigningVariable = variable;
	}

	public void setAssignedOperation(Operation assignedOperation) {
		operation = assignedOperation;
	}
	
	@Override
	public InstructionType getType() {
		return InstructionType.ASSIGNMENT;
	}
	
	@Override
	public String toString(){
		return assigningVariable + " <= " + operation;
	}

}