package hardware_model;

public class Operand implements OperationElement {

	boolean isVariable;
	String variableName;
	String simpleValue;
	int precedence;
	
	private Operand(boolean isVariable, String variableName, String value, int precedence){
		this.isVariable = isVariable;
		if ( isVariable ){
			this.variableName = variableName;
		}
		else{
			this.simpleValue = value;
		}
		this.precedence = precedence;
	}
	
	public Operand(boolean isVariable, String entry, int precedence){
		this(isVariable, entry, entry, precedence);
	}
	
	@Override
	public OperationElementType getType() {
		return (isVariable)? OperationElementType.VARIABLE : OperationElementType.VALUE;
	}

	@Override
	public int getPrecedence() {
		return precedence;
	}

	@Override
	public String getIdentifier() {
		return (isVariable)? variableName : simpleValue;
	}
	
	@Override
	public String toString() {
		return getIdentifier();
	}
	
}
