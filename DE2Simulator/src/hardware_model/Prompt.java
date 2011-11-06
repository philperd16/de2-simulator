package hardware_model;

public class Prompt implements OperationElement {

	OperationElement operator;
	int precedence;
	String identifier;
	
	public Prompt(OperationElement operator, int precedence, boolean isOpening) {
		this.operator = operator;
		this.precedence = precedence;
		this.identifier = ((isOpening)? "{" : "}") + operator.getIdentifier();
	}

	@Override
	public OperationElementType getType() {
		return OperationElementType.PROMPT;
	}

	@Override
	public int getPrecedence() {
		return precedence;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String toString() {
		return getIdentifier();
	}
	
}
