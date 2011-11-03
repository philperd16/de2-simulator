package simulator;

import hardware_model.Assignment;
import hardware_model.Operation;
import hardware_model.OperationElement;
import hardware_model.OperationElement.OperationElementType;
import hardware_model.Variable;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

public class InstructionThread implements Runnable{

	Assignment instruction;
	Set<Variable> environmentVariables;
	
	public InstructionThread(Assignment instruction, Set<Variable> environmentVariables){
		this.instruction = instruction;
		this.environmentVariables = environmentVariables;
	}
	
	public int calculateResult(Operation assignedOperation) {
		//FIXME Finish this method
		int result = 0;
		PriorityQueue<OperationElement> steps = assignedOperation.getSteps();
		Iterator<OperationElement> iterator = steps.iterator();
		while ( iterator.hasNext() ){
			OperationElement current = iterator.next();
			if ( current.getType().equals(OperationElementType.VARIABLE) ){
				result += getVariableValue(current.getIdentifier());
				steps.remove();
			}
			else if ( current.getType().equals(OperationElementType.BINARY_OPERATOR) ){
			}
		}
		return -1;
	}
	
	private int getVariableValue(String identifier) {
		for ( Variable var : environmentVariables ){
			if ( var.getName().equals(identifier) ){
				return var.getValue();
			}
		}
		return 0;
	}

	@Override
	public void run() {
		while ( true ){
			int result = calculateResult(this.instruction.getAssignedOperation());
			this.instruction.getAssigningVariable().setValue(result);
			Thread.yield();
		}
	}
	
}
