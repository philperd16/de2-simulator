package hardware_model.operation;

public class LogicalNotOperator extends UnaryOperator {

	public LogicalNotOperator(int precedence) {
		super(precedence);
	}

	@Override
	public String getIdentifier() {
		return "!";
	}

	@Override
	public int doOperation(int operand1) {
		return ( operand1 == 0 ) ? 1 : 0;
	}

}
