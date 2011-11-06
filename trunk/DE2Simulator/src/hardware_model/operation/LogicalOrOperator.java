package hardware_model.operation;


public class LogicalOrOperator extends BinaryOperator {

	public LogicalOrOperator(int precedence) {
		super(precedence);
	}

	@Override
	public String getIdentifier() {
		return "||";
	}

	@Override
	public int doOperation(int operand1, int operand2) {
		return (operand1 != 0) && (operand2 != 0) ? 1 : 0;
	}

}
