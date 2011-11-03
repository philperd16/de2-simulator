package hardware_model;


public class LogicAndOperator extends Operator {

	public LogicAndOperator(int precedence) {
		super(precedence);
	}

	@Override
	public OperationElementType getType() {
		return OperationElementType.BINARY_OPERATOR;
	}

	@Override
	public String getIdentifier() {
		return "&&";
	}

}
