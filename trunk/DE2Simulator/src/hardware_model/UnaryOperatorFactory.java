package hardware_model;

public class UnaryOperatorFactory {

	public static OperationElement createNewUnaryOperator(String operator, int precedence) {
		if ( operator.equals("~") ){
			return new BitwiseNotOperator(precedence);
		}
		return null;
	}

}
