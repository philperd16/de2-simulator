package hardware_model;

public class BitwiseNotOperator extends UnaryOperator {

	public BitwiseNotOperator(int precedence) {
		super(precedence);
	}

	@Override
	public String getIdentifier() {
		return "~";
	}

	@Override
	public int doOperation(int operand1) {
		return ~operand1;
	}

}
