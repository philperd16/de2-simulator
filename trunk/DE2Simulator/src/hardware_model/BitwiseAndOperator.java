package hardware_model;

public class BitwiseAndOperator extends BinaryOperator {

	public BitwiseAndOperator(int precedence) {
		super(precedence);
	}


	@Override
	public String getIdentifier() {
		return "&";
	}

	@Override
	public int doOperation(int operand1, int operand2) {
		return operand1 & operand2;
	}

}
