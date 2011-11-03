package hardware_model;


public class BitwiseXorOperator extends Operator {

	public BitwiseXorOperator(int precedence) {
		super(precedence);
	}

	@Override
	public OperationElementType getType() {
		return OperationElementType.BINARY_OPERATOR;
	}

	@Override
	public String getIdentifier() {
		return "^";
	}

}
