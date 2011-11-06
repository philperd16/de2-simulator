package hardware_model;

import java.util.Collection;
import java.util.Comparator;

public class Operation {

	String operationDescription;
//	PriorityQueue<OperationElement> operationElements;
	Steps operationElements;
	
	public Operation(String assignedOperation) {
		operationDescription = assignedOperation;
/*		operationElements = new PriorityQueue<OperationElement>(3, new Comparator<OperationElement>(){

			@Override
			public int compare(OperationElement o1, OperationElement o2) {
				System.out.println("\ncomparing "+o1.getIdentifier()+" with "+o2.getIdentifier()+": ");
				int result = o2.getPrecedence() - o1.getPrecedence();
				System.out.println(result > 0 ? "o2 is bigger" : (result < 0 ? "o1 is bigger" : "o1 and o2 are equally leveled\n"));
				return result;
			}
			
		});*/
		operationElements = new Steps(new Comparator<OperationElement>(){

			@Override
			public int compare(OperationElement o1, OperationElement o2) {
				return o2.getPrecedence() - o1.getPrecedence();
			}
			
		});
		
		handleWithOperationDescription();
		String[] opsElements = operationDescription.trim().split(" {1,}+");
		interpretDescribedOperation(opsElements, 0, opsElements.length, 1); 
	}
	
	public Collection<OperationElement> getSteps(){
		return operationElements.getListedSteps();
	}
	
	private void handleWithOperationDescription() {

		int openingParentesisPosition = -2;
		while(true){
			openingParentesisPosition = operationDescription.indexOf("(", openingParentesisPosition+2);
			if ( openingParentesisPosition == -1 ){
				break;
			}
			operationDescription =
					operationDescription.substring(0, openingParentesisPosition)
					+" "+operationDescription.substring(openingParentesisPosition,openingParentesisPosition+1)
					+" "+operationDescription.substring(openingParentesisPosition+1);
		}
		
		int closingParentesisPosition = -2;
		while(true){
			closingParentesisPosition = operationDescription.indexOf(")", closingParentesisPosition+2);
			if ( closingParentesisPosition == -1 ){
				break;
			}
			operationDescription =
					operationDescription.substring(0, closingParentesisPosition)
					+" "+operationDescription.substring(closingParentesisPosition, closingParentesisPosition+1)
					+" "+operationDescription.substring(closingParentesisPosition+1);
		}
		
		String[] operators = { "&", "&&", "|", "||", "+", "-", ">>", "<<", "~", "^" };
		for ( int i=0; i<operators.length; i++ ){
			int operatorPosition = -5;
			while ( true ){
				operatorPosition = operationDescription.indexOf(operators[i], operatorPosition+1);
				if ( operatorPosition == -1 ){
					break;
				}
				int operatorFinish = operatorPosition;
				while ( operationDescription.charAt(operatorFinish) == operators[i].charAt(0) ){
					operatorFinish++;
				}
				operationDescription = operationDescription.substring(0, operatorPosition)
					+" "+operationDescription.substring(operatorPosition, 
							operatorFinish)
							+" "+operationDescription.substring(operatorFinish);
				operatorPosition = operatorFinish;
			}
		}
	}

	private void interpretDescribedOperation(String[] opsElements, int begin, int end, int precedenceLevel){

		int operatorPrecedence = precedenceLevel;
		
		for ( int i=begin; i<end; ){
			opsElements[i] = opsElements[i].trim();
			if ( isOpeningParentesis(opsElements[i]) ){
				int closingParentesisIndex = findProperClosingParentesisIndex(opsElements, i);
				
				interpretDescribedOperation(opsElements, i+1, closingParentesisIndex, operatorPrecedence+2);
				i=closingParentesisIndex;
			}
			else if ( isUnaryOperator(opsElements[i]) ){
				if ( isOpeningParentesis(opsElements[i+1]) ){
					OperationElement operator = UnaryOperatorFactory.createNewUnaryOperator(opsElements[i]+"!", operatorPrecedence+1);
					operationElements.add(new Prompt(operator, operatorPrecedence+1, true));
					operationElements.add(operator);
					operationElements.add(new Prompt(operator, operatorPrecedence, false));
				}
				else operationElements.add(UnaryOperatorFactory.createNewUnaryOperator(opsElements[i], operatorPrecedence));
				i++;
			}
			else if ( isCommutativeBinaryOperator(opsElements[i]) ){
				if ( isOpeningParentesis(opsElements[i+1]) ||
						( isUnaryOperator(opsElements[i+1]) && isOpeningParentesis(opsElements[i+2])) ){
					OperationElement operator = BinaryOperatorFactory.createNewBinaryOperator(opsElements[i], operatorPrecedence+1);
					operationElements.add(new Prompt(operator, operatorPrecedence+1, true));
					operationElements.add(operator);
					operationElements.add(new Prompt(operator, operatorPrecedence, false));
				}
				else operationElements.add(BinaryOperatorFactory.createNewBinaryOperator(opsElements[i], operatorPrecedence));
				i++; 
			}
			else if ( isNonCommutativeBinaryOperator(opsElements[i]) ){
				if ( isOpeningParentesis(opsElements[i+1]) ||
						( isUnaryOperator(opsElements[i+1]) && isOpeningParentesis(opsElements[i+2])) ){
					OperationElement operator = BinaryOperatorFactory.createNewBinaryOperator(opsElements[i]+"!", operatorPrecedence+1);
					operationElements.add(new Prompt(operator, operatorPrecedence+1, true));
					operationElements.add(operator);
					operationElements.add(new Prompt(operator, operatorPrecedence, false));
				}
				else operationElements.add(BinaryOperatorFactory.createNewBinaryOperator(opsElements[i], operatorPrecedence));
				i++;
			}
			else if ( isClosingParentesis(opsElements[i]) ){
				operatorPrecedence = precedenceLevel;
				i++;
			}
			else if ( isVariableOperand(opsElements[i]) || isValueOperand(opsElements[i])){
				operationElements.add(new Operand(isVariableOperand(opsElements[i]), opsElements[i], operatorPrecedence));
				i++;
			}
		}
		
	}
	
	private int findProperClosingParentesisIndex(String[] opsElements, int i) {
		int matcher = 0;
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
		if ( !Character.isDigit(string.charAt(0)) ){
			return !string.contains("'");
		}
		return false;
	}
	
	private boolean isOpeningParentesis(String string) {
		return string.equals("(");
	}

	private boolean isClosingParentesis(String string) {
		return string.equals(")");
	}

	private boolean isCommutativeBinaryOperator(String string) {
		return  string.equals("&")   ||
				string.equals("&&")  ||
				string.equals("|")   ||
				string.equals("||")  ||
				string.equals("^")   ||
				string.equals("+");
	}
	
	private boolean isNonCommutativeBinaryOperator(String string){
		return 	string.equals("-")  ||
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
