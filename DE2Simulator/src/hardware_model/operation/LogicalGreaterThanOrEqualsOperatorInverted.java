package hardware_model.operation;

public class LogicalGreaterThanOrEqualsOperatorInverted extends LogicalGreaterThanOrEqualsOperator {

	public LogicalGreaterThanOrEqualsOperatorInverted(int precedence) {
		super(precedence);
	}

	@Override
	public String getIdentifier() {
		return ">=!";
	}

	@Override
	public int doOperation(int operand1, int operand2) {
		return super.doOperation(operand2, operand1);
	}

}
