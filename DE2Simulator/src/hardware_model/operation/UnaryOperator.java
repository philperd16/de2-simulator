package hardware_model.operation;


public abstract class UnaryOperator implements OperationElement {

	int precedence;
	
	public UnaryOperator(int precedence){
		this.precedence = precedence;
	}
	
	@Override
	public int getPrecedence(){
		return precedence;
	}

	public abstract int doOperation(int operand1);

	public boolean isInverted(){
		return getIdentifier().endsWith("!") && getIdentifier().length() < 1;
	}
	
	@Override
	public OperationElementType getType() {
		return OperationElementType.UNARY_OPERATOR;
	}
	
	@Override
	public String toString(){
		return getIdentifier();
	}
	
}
