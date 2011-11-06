package hardware_model;


public interface OperationElement {

	public enum OperationElementType{
		VARIABLE, VALUE, BINARY_OPERATOR, UNARY_OPERATOR, PROMPT;
	}
	
	OperationElementType getType();
	int getPrecedence();
	String getIdentifier();
	
}
