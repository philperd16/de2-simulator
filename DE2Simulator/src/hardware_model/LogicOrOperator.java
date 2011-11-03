package hardware_model;


public class LogicOrOperator extends Operator {

	public LogicOrOperator(int precedence) {
		super(precedence);
	}

	@Override
	public OperationElementType getType() {
		return OperationElementType.BINARY_OPERATOR;
	}

	@Override
	public String getIdentifier() {
		return "||";
	}
	
}
