package hardware_model;

public class LogicShiftRightOperator extends Operator {

	public LogicShiftRightOperator(int precedence) {
		super(precedence);
	}

	@Override
	public OperationElementType getType() {
		return OperationElementType.BINARY_OPERATOR;
	}

	@Override
	public String getIdentifier() {
		return ">>";
	}

}
