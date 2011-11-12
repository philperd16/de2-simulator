package hardware_model.operation;

public class LogicalLessThanOperator extends BinaryOperator {

	public LogicalLessThanOperator(int precedence) {
		super(precedence);
	}

	@Override
	public String getIdentifier() {
		return "<";
	}

	@Override
	public int doOperation(int operand1, int operand2) {
		return ( operand1 < operand2 ) ? 1 : 0;
	}

}
