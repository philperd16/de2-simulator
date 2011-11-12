package hardware_model.operation;

public class UnaryOperatorFactory {

	public static UnaryOperator createNewUnaryOperator(String operator, int precedence) {
		if ( operator.equals("~") ){
			return new BitwiseNotOperator(precedence);
		}
		else if ( operator.equals("~!") ){
			return new BitwiseNotOperatorInverted(precedence);
		}
		else if ( operator.equals("!") ){
			return new LogicalNotOperator(precedence);
		}
		else if ( operator.equals("!!") ){
			return new LogicalNotOperatorInverted(precedence);
		}
		return null;
	}

}
