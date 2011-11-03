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
			else if ( current.getType().equals(OperationElementType.VALUE) ){
				result += getParsedValue(current.getIdentifier());
				steps.remove();
			}
			else if ( current.getType().equals(OperationElementType.BINARY_OPERATOR) ){
				//create a method doOperation for the Operator implementations, which receives both operands and returns result
			}else if ( current.getType().equals(OperationElementType.UNARY_OPERATOR) ){
				//create a method doOperation for the Operator implementations, which receives both operands and returns result
			}
		}
		return -1;
	}
	
	private int getParsedValue(String identifier) {
		if ( !identifier.contains("'") ){
			return Integer.parseInt(identifier);
		}
		else {
			if ( Character.toLowerCase(identifier.charAt(identifier.indexOf("'"))) == 'b' ){
				return parseBinaryString(identifier);
			}
			else if ( Character.toLowerCase(identifier.charAt(identifier.indexOf("'"))) == 'x' ){
				return parseBinaryString(hexToBinaryString(identifier));
			}
			return -1;
		}
	}

	private String hexToBinaryString(String identifier) {
		String hex = identifier.split("'")[1];
		StringBuilder binary = new StringBuilder();
		for ( int i=0; i<hex.length(); i++ ){
			if ( Character.toLowerCase(hex.charAt(i)) == '0' ){
				binary.append("0000");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == '1' ){
				binary.append("0001");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == '2' ){
				binary.append("0010");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == '3' ){
				binary.append("0011");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == '4' ){
				binary.append("0100");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == '5' ){
				binary.append("0101");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == '6' ){
				binary.append("0110");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == '7' ){
				binary.append("0111");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == '8' ){
				binary.append("1000");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == '9' ){
				binary.append("1001");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == 'a' ){
				binary.append("1010");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == 'b' ){
				binary.append("1011");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == 'c' ){
				binary.append("1100");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == 'd' ){
				binary.append("1101");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == 'e' ){
				binary.append("1110");
			}
			else if ( Character.toLowerCase(hex.charAt(i)) == 'f' ){
				binary.append("1111");
			}
		}
		return new String(binary);
	}

	public int parseBinaryString(String identifier) {
		int result = 0;
		int k = 1;
		String binary = identifier.split("'")[1];
		for ( int i=binary.length(); i>-1; i-- ){
			result += k * Integer.parseInt(""+binary.charAt(i));
			k = k*2;
		}
		return result;
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
