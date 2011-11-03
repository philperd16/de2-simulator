package hardware_model;


public class BitwiseSubtractOperator extends Operator {

	public BitwiseSubtractOperator(int precedence) {
		super(precedence);
	}

	@Override
	public OperationElementType getType() {
		return OperationElementType.BINARY_OPERATOR;
	}

	@Override
	public String getIdentifier() {
		return "-";
	}

}
