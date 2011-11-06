package simulator;

import hardware_model.Variable;
import hardware_model.operation.BinaryOperator;
import hardware_model.operation.Operation;
import hardware_model.operation.OperationElement;
import hardware_model.operation.UnaryOperator;
import hardware_model.operation.OperationElement.OperationElementType;

import java.util.Iterator;
import java.util.Set;
import java.util.TimerTask;

public abstract class InstructionThread extends TimerTask{

	Set<Variable> environmentVariables;
	
	public InstructionThread(Set<Variable> environmentVariables){
		this.environmentVariables = environmentVariables;
	}
	
	public int calculateResult(Operation assignedOperation){
		return calculateResult(assignedOperation.getSteps().iterator(), null);
	}
	
	public int calculateResult(Iterator<OperationElement> steps, String stopPrompt) {
		int result = 0;
		while ( steps.hasNext() ){
			OperationElement current = steps.next();
			//Following 3 conditions may only be caught by the first operation element
			if ( current.getType().equals(OperationElementType.VARIABLE) ){
				result = getVariableValue(current.getIdentifier());
			}
			else if ( current.getType().equals(OperationElementType.VALUE) ){
				result = getParsedValue(current.getIdentifier());
			}else if ( current.getType().equals(OperationElementType.UNARY_OPERATOR) ){
				if ( !((UnaryOperator) current).isInverted() ){
					OperationElement nextOperand = steps.next();
					if ( nextOperand.getType().equals(OperationElementType.VALUE) ){
						result = ((UnaryOperator) current).doOperation(getParsedValue(nextOperand.getIdentifier()));
					}
					else if ( nextOperand.getType().equals(OperationElementType.VARIABLE) ){
						result = ((UnaryOperator) current).doOperation(getVariableValue(nextOperand.getIdentifier()));
					}
					else if ( nextOperand.getType().equals(OperationElementType.PROMPT) ){
						result = ((UnaryOperator) current).doOperation(calculateResult(steps, nextOperand.getIdentifier().replace("{", "}")));
					}
				}
				else{
					result = ((UnaryOperator) current).doOperation(result);
				}
			}
			//This condition governs the rest of the operation elements
			else if ( current.getType().equals(OperationElementType.BINARY_OPERATOR) ){
				OperationElement nextOperand = steps.next();
				if ( nextOperand.getType().equals(OperationElementType.VALUE) ){
					result = ((BinaryOperator) current).doOperation(result, getParsedValue(nextOperand.getIdentifier()));
				}
				else if ( nextOperand.getType().equals(OperationElementType.VARIABLE) ){
					result = ((BinaryOperator) current).doOperation(result, getVariableValue(nextOperand.getIdentifier()));
				}
				else if ( current.getType().equals(OperationElementType.UNARY_OPERATOR) ){
					OperationElement finalOperand = steps.next();
					int unaryOperationResult = 0;
					if ( finalOperand.getType().equals(OperationElementType.VALUE) ){
						unaryOperationResult = ((UnaryOperator) current).doOperation(getParsedValue(finalOperand.getIdentifier()));
					}
					else if ( finalOperand.getType().equals(OperationElementType.VARIABLE) ){
						unaryOperationResult = ((UnaryOperator) current).doOperation(getVariableValue(finalOperand.getIdentifier()));
					}
					else if ( nextOperand.getType().equals(OperationElementType.PROMPT) ){
						result = ((UnaryOperator) current).doOperation(calculateResult(steps, nextOperand.getIdentifier().replace("{", "}")));
					}
					result = ((BinaryOperator) current).doOperation(result, unaryOperationResult);
				}
				else if ( nextOperand.getType().equals(OperationElementType.PROMPT) ){
					result = ((BinaryOperator) current).doOperation(result, calculateResult(steps, nextOperand.getIdentifier().replace("{", "}")));
				}
			}
			else if ( current.getType().equals(OperationElementType.PROMPT) ) {
				if ( stopPrompt != null && current.getIdentifier().equals(stopPrompt) ){
					return result;
				}
			}
		}
		return result;
	}
	
	private int getParsedValue(String identifier) {
		if ( !identifier.contains("'") ){
			return Integer.parseInt(identifier);
		}
		else {
			if ( Character.toLowerCase(identifier.charAt(identifier.indexOf("'")+1)) == 'b' ){
				return parseBinaryString(identifier);
			}
			else if ( Character.toLowerCase(identifier.charAt(identifier.indexOf("'")+1)) == 'x' ){
				return parseBinaryString(hexToBinaryString(identifier));
			}
			else if ( Character.toLowerCase(identifier.charAt(identifier.indexOf("'")+1)) == 'd' ){
				return Integer.parseInt(identifier.split("'")[1]);
			}
			else return -1;
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
	
}
