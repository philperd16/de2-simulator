package hardware_model;

public class BitwiseNotOperator extends Operator {

	public BitwiseNotOperator(int precedence) {
		super(precedence);
	}

	@Override
	public OperationElementType getType() {
		return OperationElementType.UNARY_OPERATOR;
	}

	@Override
	public String getIdentifier() {
		return "~";
	}

}
