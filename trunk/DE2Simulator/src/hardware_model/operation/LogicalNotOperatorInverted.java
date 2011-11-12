package hardware_model.operation;

public class LogicalNotOperatorInverted extends LogicalNotOperator {

	public LogicalNotOperatorInverted(int precedence) {
		super(precedence);
	}

	@Override
	public String getIdentifier() {
		return "!!";
	}

}
