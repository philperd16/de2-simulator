package hardware_model;

public interface OperationElement {

	public enum OperationElementType{
		VARIABLE, VALUE, BINARY_OPERATOR, UNARY_OPERATOR;
	}
	
	OperationElementType getType();
	int getPrecedence();
	String getIdentifier();
	
}
