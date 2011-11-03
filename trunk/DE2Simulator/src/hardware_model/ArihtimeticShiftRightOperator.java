package hardware_model;

public class ArihtimeticShiftRightOperator extends Operator {

	public ArihtimeticShiftRightOperator(int precedence) {
		super(precedence);
	}

	@Override
	public OperationElementType getType() {
		return OperationElementType.BINARY_OPERATOR;
	}

	@Override
	public String getIdentifier() {
		return ">>>";
	}

}
