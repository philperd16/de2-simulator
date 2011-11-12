package hardware_model.operation;

public class BinaryOperatorFactory {

	public static BinaryOperator createNewBinaryOperator(String operator, int precedence) {
		if ( operator.equals("&") ){
			return new BitwiseAndOperator(precedence);
		}
		else if ( operator.equals("&&") ){
			return new LogicalAndOperator(precedence);
		}
		else if ( operator.equals("|") ){
			return new BitwiseOrOperator(precedence);
		}
		else if ( operator.equals("||") ){
			return new LogicalOrOperator(precedence);
		}
		else if ( operator.equals("^") ){
			return new BitwiseXorOperator(precedence);
		}
		else if ( operator.equals("+") ){
			return new BitwiseAddOperator(precedence);
		}
		else if ( operator.equals("-") ){
			return new BitwiseSubtractOperator(precedence);
		}
		else if ( operator.equals("-!") ){
			return new BitwiseSubtractOperatorInverted(precedence);
		}
		else if ( operator.equals(">>") ){
			return new LogicalShiftRightOperator(precedence);
		}
		else if ( operator.equals(">>!") ){
			return new LogicalShiftRightOperatorInverted(precedence);
		}
		else if ( operator.equals(">>>") ){
			return new ArithmeticShiftRightOperator(precedence);
		}
		else if ( operator.equals(">>>!") ){
			return new ArihtimeticShiftRightOperatorInverted(precedence);
		}
		else if ( operator.equals("<<") ){
			return new LogicalShiftLeftOperator(precedence);
		}
		else if ( operator.equals("<<!") ){
			return new LogicalShiftLeftOperatorInverted(precedence);
		}
		else if ( operator.equals("==") ){
			return new LogicalEqualsOperator(precedence);
		}
		else if ( operator.equals("!=") ){
			return new LogicalDifferentThanOperator(precedence);
		}
		else if ( operator.equals(">") ){
			return new LogicalGreaterThanOperator(precedence);
		}
		else if ( operator.equals(">!") ){
			return new LogicalGreaterThanOperatorInverted(precedence);
		}
		else if ( operator.equals("<") ){
			return new LogicalLessThanOperator(precedence);
		}
		else if ( operator.equals("<!") ){
			return new LogicalLessThanOperatorInverted(precedence);
		}
		else if ( operator.equals(">=") ){
			return new LogicalGreaterThanOrEqualsOperator(precedence);
		}
		else if ( operator.equals(">=!") ){
			return new LogicalGreaterThanOrEqualsOperatorInverted(precedence);
		}
		else if ( operator.equals("<=") ){
			return new LogicalLessThanOrEqualsOperator(precedence);
		}
		else if ( operator.equals("<=!") ){
			return new LogicalLessThanOrEqualsOperatorInverted(precedence);
		}
		else{
			return null;
		}
	}

}
