package hardware_model.operation;

public class LogicalShiftLeftOperator extends BinaryOperator {

	public LogicalShiftLeftOperator(int precedence) {
		super(precedence);
	}

	@Override
	public String getIdentifier() {
		return "<<";
	}

	@Override
	public int doOperation(int operand1, int operand2) {
		return operand1 << operand2;
	}

}
