package hardware_model.operation;

public class ArithmeticShiftRightOperator extends BinaryOperator {

	public ArithmeticShiftRightOperator(int precedence) {
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

	@Override
	public int doOperation(int operand1, int operand2) {
		return operand1 >>> operand2;
	}

}
