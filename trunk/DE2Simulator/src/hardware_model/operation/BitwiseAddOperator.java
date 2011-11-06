package hardware_model.operation;


public class BitwiseAddOperator extends BinaryOperator {

	public BitwiseAddOperator(int precedence) {
		super(precedence);
	}

	@Override
	public String getIdentifier() {
		return "+";
	}

	@Override
	public int doOperation(int operand1, int operand2) {
		return operand1 + operand2;
	}

}
