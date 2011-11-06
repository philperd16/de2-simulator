package hardware_model.operation;


public class BitwiseXorOperator extends BinaryOperator {

	public BitwiseXorOperator(int precedence) {
		super(precedence);
	}

	@Override
	public String getIdentifier() {
		return "^";
	}

	@Override
	public int doOperation(int operand1, int operand2) {
		return operand1 ^ operand2;
	}

}
