package hardware_model.operation;

public class BitwiseSubtractOperatorInverted extends BitwiseSubtractOperator {

	public BitwiseSubtractOperatorInverted(int precedence) {
		super(precedence);
	}
	
	@Override
	public String getIdentifier() {
		return "-!";
	}

	@Override
	public int doOperation(int operand1, int operand2) {
		return operand2 - operand1;
	}

}
