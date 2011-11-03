package hardware_model;

public class LogicShiftLeftOperator extends Operator {

	public LogicShiftLeftOperator(int precedence) {
		super(precedence);
	}

	@Override
	public OperationElementType getType() {
		return OperationElementType.BINARY_OPERATOR;
	}

	@Override
	public String getIdentifier() {
		return "<<";
	}

}
