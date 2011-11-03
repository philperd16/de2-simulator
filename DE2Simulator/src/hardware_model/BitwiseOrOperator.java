package hardware_model;


public class BitwiseOrOperator extends Operator {

	public BitwiseOrOperator(int precedence) {
		super(precedence);
	}

	@Override
	public OperationElementType getType() {
		return OperationElementType.BINARY_OPERATOR;
	}

	@Override
	public String getIdentifier() {
		return "|";
	}

}
