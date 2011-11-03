package hardware_model;


public class BitwiseAddOperator extends Operator {

	public BitwiseAddOperator(int precedence) {
		super(precedence);
	}

	@Override
	public OperationElementType getType() {
		return OperationElementType.BINARY_OPERATOR;
	}

	@Override
	public String getIdentifier() {
		return "+";
	}

}
