package hardware_model;

public class BitwiseAndOperator extends Operator {

	public BitwiseAndOperator(int precedence) {
		super(precedence);
	}

	@Override
	public OperationElementType getType() {
		return OperationElementType.BINARY_OPERATOR;
	}

	@Override
	public String getIdentifier() {
		return "&";
	}

}
