package hardware_model.operation;


public class BitwiseSubtractOperator extends BinaryOperator {

	public BitwiseSubtractOperator(int precedence) {
		super(precedence);
	}

	@Override
	public String getIdentifier() {
		return "-";
	}

	@Override
	public int doOperation(int operand1, int operand2) {
		return operand1 - operand2;
	}

}
