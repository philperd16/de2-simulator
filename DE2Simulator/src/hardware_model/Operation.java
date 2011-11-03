package hardware_model;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Operation {

	String operationDescription;
	PriorityQueue<OperationElement> operationElements;
	
	public Operation(String assignedOperation) {
		operationDescription = assignedOperation;
		operationElements = new PriorityQueue<OperationElement>(3, new Comparator<OperationElement>(){

			@Override
			public int compare(OperationElement o1, OperationElement o2) {
				return o1.getPrecedence() - o2.getPrecedence();
			}
			
		});
		
		handleWithOperationDescription();
		String[] opsElements = operationDescription.trim().split(" ");
		interpretDescribedOperation(opsElements, 0, opsElements.length, 0);
	}
	
	public PriorityQueue<OperationElement> getSteps(){
		return operationElements;
	}
	
	private void handleWithOperationDescription() {

		int openingParentesisPosition = 0;
		while(true){
			openingParentesisPosition = operationDescription.indexOf("(", openingParentesisPosition+2);
			if ( openingParentesisPosition == -1 ){
				break;
			}
			operationDescription =
					operationDescription.substring(0, openingParentesisPosition)
					+" "+operationDescription.substring(openingParentesisPosition);
		}
		
		int closingParentesisPosition = 0;
		while(true){
			closingParentesisPosition = operationDescription.indexOf(")", closingParentesisPosition+2);
			if ( closingParentesisPosition == -1 ){
				break;
			}
			operationDescription =
					operationDescription.substring(0, closingParentesisPosition+1)
					+" "+operationDescription.substring(closingParentesisPosition+1);
		}
		
	}

	private void interpretDescribedOperation(String[] opsElements, int begin, int end, int precedence){

		int operatorPrecedence = precedence;
		
		for ( int i=begin; i<end; ){
			opsElements[i] = opsElements[i].trim();
			if ( isOpeningParentesis(opsElements[i]) ){
				int closingParentesisIndex = findProperClosingParentesisIndex(opsElements, i);
				
				interpretDescribedOperation(opsElements, i, closingParentesisIndex, operatorPrecedence+1);
				i=closingParentesisIndex;
			}
			else if ( isUnaryOperator(opsElements[i]) ){
				operationElements.add(new Operand(isVariableOperand(opsElements[i+1]), opsElements[i+1], precedence));
				i++;
			}
			else if ( isBinaryOperator(opsElements[i]) ){
				if ( isOpeningParentesis(opsElements[i+1]) ){
					operatorPrecedence++;
				}
				operationElements.add(BinaryOperatorFactory.createNewBinaryOperator(opsElements[i], operatorPrecedence));
				i++;
			}
			else if ( isVariableOperand(opsElements[i]) || isValueOperand(opsElements[i])){
				operationElements.add(new Operand(isVariableOperand(opsElements[i]), opsElements[i], precedence));
				i++;
			}
			else if ( isClosingParentesis(opsElements[i]) ){
				i++;
			}
		}
		
	}
	
	private int findProperClosingParentesisIndex(String[] opsElements, int i) {
		int matcher = 1;
		for ( int j=i; j<opsElements.length; j++ ){
			if ( isOpeningParentesis(opsElements[j]) ){
				matcher++;
			}
			else if ( isClosingParentesis(opsElements[j]) ){
				matcher--;
				if ( matcher == 0 ){
					return j;
				}
			}
		}
		return -1;
	}
	
	private boolean isValueOperand(String string) {
		for ( int i=0; i<string.length(); i++ ){
			if ( !(string.charAt(i) == '\'' || Character.isDigit(string.charAt(i))) ){
				return false;
			}
		}
		return true;
	}
	
	private boolean isVariableOperand(String string) {
		return !string.contains("'");
	}
	
	private boolean isOpeningParentesis(String string) {
		return string.equals("(");
	}

	private boolean isClosingParentesis(String string) {
		return string.equals(")");
	}

	private boolean isBinaryOperator(String string) {
		return  string.equals("&")   ||
				string.equals("&&")  ||
				string.equals("|")   ||
				string.equals("||")  ||
				string.equals("^")   ||
				string.equals("+")   ||
				string.equals("-")   ||
				string.equals(">>")  ||
				string.equals(">>>") ||
				string.equals("<<");
	}
	
	private boolean isUnaryOperator(String string) {
		return string.equals("~");
	}

	@Override
	public String toString(){
		return operationDescription;
	}

}
