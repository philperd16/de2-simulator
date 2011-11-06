package hardware_model;


public abstract class BinaryOperator implements OperationElement {

	int precedence;
	
	public BinaryOperator(int precedence){
		this.precedence = precedence;
	}
	
	@Override
	public int getPrecedence(){
		return precedence;
	}

	public abstract int doOperation(int operand1, int operand2);
	
	@Override
	public OperationElementType getType() {
		return OperationElementType.BINARY_OPERATOR;
	}
	
	@Override
	public String toString(){
		return getIdentifier();
	}
	
}
